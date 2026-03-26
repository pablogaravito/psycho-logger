package com.pablogb.psychologger.transcription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private final AudioConversionService audioConversionService;
    private final WhisperCppService whisperCppService;
    private final TranscriptionJobStore jobStore;

    @Async("transcriptionExecutor")
    public void processAsync(String jobId, File inputFile, String language) {
        File wavFile = null;
        try {
            log.info("Processing transcription job: {}", jobId);

            wavFile = audioConversionService.convertToWav(inputFile);
            String text = whisperCppService.transcribe(wavFile, language);

            jobStore.complete(jobId, text);
            log.info("Transcription job completed: {}", jobId);

        } catch (Exception e) {
            log.error("Transcription job failed: {}", jobId, e);
            jobStore.fail(jobId, e.getMessage());
        } finally {
            // cleanup temp files
            try {
                Files.deleteIfExists(inputFile.toPath());
                if (wavFile != null) Files.deleteIfExists(wavFile.toPath());
            } catch (Exception e) {
                log.warn("Failed to cleanup temp files for job: {}", jobId);
            }
        }
    }

    // clean up old jobs every hour
    @Scheduled(fixedRate = 3600000)
    public void cleanupOldJobs() {
        log.info("Cleaning up old transcription jobs");
        jobStore.cleanup();
    }
}
