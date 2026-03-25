package com.pablogb.psychologger.transcription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class WhisperCppService {

    private static final Logger logger = LoggerFactory
            .getLogger(WhisperCppService.class);

    @Value("${whisper.model.path}")
    private String modelPath;

    @Value("${whisper.exe.path}")
    private String whisperExePath;

    private final File workingDir = new File("tmp").getAbsoluteFile();

    public String transcribe(File wavFile, String language)
            throws IOException, InterruptedException {

        logger.info("Starting transcription for file: {} (lang: {})",
                wavFile.getAbsolutePath(), language);

        File whisperExe = new File(whisperExePath).getAbsoluteFile();
        File modelFile = new File(modelPath).getAbsoluteFile();

        ProcessBuilder pb = new ProcessBuilder(
                whisperExe.getAbsolutePath(),
                "-m", modelFile.getAbsolutePath(),
                "-f", wavFile.getAbsolutePath(),
                "-l", language != null ? language : "auto", // fallback to auto
                "-nt", // no timestamps
                "-np"  // no extra prints, only transcription
        );

        pb.directory(workingDir);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder transcription = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                logger.debug("[whisper.cpp] {}", line);

                if (!line.trim().isEmpty()) {
                    transcription.append(line).append(" ");
                }
            }
        }

        int exitCode = process.waitFor();
        logger.debug("Whisper process exited with code: {}", exitCode);

        if (exitCode != 0) {
            logger.error("Whisper transcription failed with exit code: {}", exitCode);
            throw new IOException("Whisper process failed with exit code " + exitCode);
        }

        String result = transcription.toString()
                .replaceAll("\\s+", " ") // collapse multiple spaces/newlines
                .trim();

        logger.info("Transcription completed successfully");
        logger.debug("Final transcription result: {}", result);

        return result;
    }
}