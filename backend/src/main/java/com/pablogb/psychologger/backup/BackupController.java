package com.pablogb.psychologger.backup;

import com.pablogb.psychologger.model.enums.AuditAction;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;
    private final AuditService auditService;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadBackup(
            @RequestBody BackupRequestDto request) throws Exception {

        log.info("Backup requested by admin");

        // validate password before proceeding
        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            log.warn("Backup rejected — no password provided");
            return ResponseEntity.status(400).build();
        }

        String currentPasswordHash = securityUtils
                .getCurrentUser().getPasswordHash();

        if (!passwordEncoder.matches(
                request.getPassword(), currentPasswordHash)) {
            log.warn("Backup rejected — wrong password");
            return ResponseEntity.status(403).build();
        }

        byte[] backupData = backupService
                .createEncryptedBackup(request.getPassword());
        String filename = backupService.generateFilename();

        auditService.logWithUser(AuditAction.CREATE, "Backup", 0,
                "Encrypted backup downloaded: " + filename,
                securityUtils.getCurrentUser());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(backupData.length)
                .body(backupData);
    }

    @PostMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> restoreBackup(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password) throws Exception {

        log.info("Restore requested by admin");

        // verify password first
        String currentPasswordHash = securityUtils
                .getCurrentUser().getPasswordHash();

        if (password == null || password.isBlank()) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Password is required"));
        }

        if (!passwordEncoder.matches(password, currentPasswordHash)) {
            log.warn("Restore rejected — wrong password");
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Invalid password"));
        }

        // save uploaded file temporarily
        File tempDir = new File("tmp").getAbsoluteFile();
        if (!tempDir.exists()) tempDir.mkdirs();

        File tempFile = new File(tempDir,
                "restore_" + System.currentTimeMillis() + ".enc");

        try (InputStream is = file.getInputStream()) {
            java.nio.file.Files.copy(is, tempFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            backupService.restoreBackup(tempFile, password);

            auditService.logWithUser(AuditAction.UPDATE, "Backup", 0,
                    "Database restored from backup",
                    securityUtils.getCurrentUser());

            return ResponseEntity.ok(
                    Map.of("message", "Database restored successfully"));
        } finally {
            java.nio.file.Files.deleteIfExists(tempFile.toPath());
        }
    }
}