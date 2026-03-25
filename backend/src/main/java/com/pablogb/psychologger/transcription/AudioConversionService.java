package com.pablogb.psychologger.transcription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class AudioConversionService {

    private static final Logger logger = LoggerFactory
            .getLogger(AudioConversionService.class);

    @Value("${ffmpeg.exe.path}")
    private String ffmpegExePath;

    private final File workingDir = new File("tmp").getAbsoluteFile();

    public File convertToWav(File inputFile) throws IOException, InterruptedException {
        if (!workingDir.exists()) workingDir.mkdirs();

        File outputFile = new File(workingDir, "converted_"
                + System.currentTimeMillis() + ".wav");

        ProcessBuilder pb = new ProcessBuilder(
                ffmpegExePath,
                "-y",
                "-i", inputFile.getAbsolutePath(),
                "-ar", "16000",
                "-ac", "1",
                "-f", "wav",
                outputFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.debug("[ffmpeg] {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg failed with exit code " + exitCode);
        }

        return outputFile;
    }
}
