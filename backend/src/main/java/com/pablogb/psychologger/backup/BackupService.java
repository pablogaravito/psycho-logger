package com.pablogb.psychologger.backup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Service
public class BackupService {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.mysql.path:mysql}")
    private String mysqlPath;

    @Value("${backup.mysqldump.path:mysqldump}")
    private String mysqldumpPath;

    // fixed salt for key derivation — not secret, just needs to be consistent
    private static final byte[] SALT = "PsychologgerBackupSalt2026".getBytes();
    private static final int ITERATIONS = 310000; // OWASP recommended
    private static final int KEY_LENGTH = 256;

    public byte[] createEncryptedBackup(String password)
            throws Exception {
        log.info("Starting encrypted database backup...");

        byte[] dumpData = runMysqldump();
        byte[] compressedData = compress(dumpData);
        byte[] encryptedData = encrypt(compressedData, password);

        log.info("Encrypted backup completed, {} bytes", encryptedData.length);
        return encryptedData;
    }

    private byte[] runMysqldump() throws IOException, InterruptedException {
        String[] urlParts = datasourceUrl
                .replace("jdbc:mysql://", "").split("/");
        String hostPort = urlParts[0];
        String dbName = urlParts[1].split("\\?")[0];
        String host = hostPort.split(":")[0];
        String port = hostPort.contains(":")
                ? hostPort.split(":")[1] : "3306";

        ProcessBuilder pb = new ProcessBuilder(
                mysqldumpPath,
                "-h", host,
                "-P", port,
                "-u", dbUsername,
                "--password=" + dbPassword,
                "--single-transaction",
                "--routines",
                "--triggers",
                dbName
        );
        pb.redirectErrorStream(false);
        Process process = pb.start();

        byte[] dumpData = process.getInputStream().readAllBytes();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            String error = new String(
                    process.getErrorStream().readAllBytes());
            throw new RuntimeException("mysqldump failed: " + error);
        }

        return dumpData;
    }

    private byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(compressed)) {
            gzip.write(data);
        }
        return compressed.toByteArray();
    }

    private byte[] encrypt(byte[] data, String password) throws Exception {
        // derive AES-256 key from password using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                SALT,
                ITERATIONS,
                KEY_LENGTH
        );
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secretKey = new SecretKeySpec(
                tmp.getEncoded(), "AES");

        // generate random IV (initialization vector)
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        // encrypt with AES-256-CBC
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey,
                new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(data);

        // prepend IV to encrypted data
        // format: [16 bytes IV][encrypted data]
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(iv);
        result.write(encrypted);
        return result.toByteArray();
    }

    public String generateFilename() {
        return "psychologger_backup_" +
                java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter
                                .ofPattern("yyyy-MM-dd_HH-mm")) +
                ".sql.gz.enc";
    }

    public void restoreBackup(File encryptedFile, String password)
            throws Exception {
        log.info("Starting database restore...");

        // decrypt
        byte[] encryptedData = java.nio.file.Files
                .readAllBytes(encryptedFile.toPath());
        byte[] compressedData = decrypt(encryptedData, password);

        // decompress
        byte[] sqlData = decompress(compressedData);
        log.info("Decrypted and decompressed: {} bytes", sqlData.length);

        // write sql to temp file
        File tempDir = new File("tmp").getAbsoluteFile();
        File sqlFile = new File(tempDir,
                "restore_" + System.currentTimeMillis() + ".sql");
        java.nio.file.Files.write(sqlFile.toPath(), sqlData);

        try {
            // extract connection details
            String[] urlParts = datasourceUrl
                    .replace("jdbc:mysql://", "").split("/");
            String hostPort = urlParts[0];
            String dbName = urlParts[1].split("\\?")[0];
            String host = hostPort.split(":")[0];
            String port = hostPort.contains(":")
                    ? hostPort.split(":")[1] : "3306";

            // run mysql restore
            ProcessBuilder pb = new ProcessBuilder(
                    getMysqlPath(),
                    "-h", host,
                    "-P", port,
                    "-u", dbUsername,
                    "--password=" + dbPassword,
                    dbName
            );
            pb.redirectInput(sqlFile);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            String output = new String(
                    process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                log.error("MySQL restore failed: {}", output);
                throw new RuntimeException("Restore failed: " + output);
            }

            log.info("Database restored successfully");

        } finally {
            java.nio.file.Files.deleteIfExists(sqlFile.toPath());
        }
    }

    private byte[] decompress(byte[] compressed) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (java.util.zip.GZIPInputStream gzip =
                     new java.util.zip.GZIPInputStream(
                             new ByteArrayInputStream(compressed))) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = gzip.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
        return out.toByteArray();
    }

    private byte[] decrypt(byte[] data, String password) throws Exception {
        // extract IV from first 16 bytes
        byte[] iv = new byte[16];
        System.arraycopy(data, 0, iv, 0, 16);
        byte[] encrypted = new byte[data.length - 16];
        System.arraycopy(data, 16, encrypted, 0, encrypted.length);

        // derive key same way as encryption
        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                SALT,
                ITERATIONS,
                KEY_LENGTH
        );
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        // decrypt
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey,
                new IvParameterSpec(iv));
        return cipher.doFinal(encrypted);
    }

    private String getMysqlPath() {
        // mysql client is on PATH in Docker
        // on Windows dev it might need full path
        return mysqlPath;
    }

    private String getMysqldumpPath() {
        return mysqldumpPath;
    }
}