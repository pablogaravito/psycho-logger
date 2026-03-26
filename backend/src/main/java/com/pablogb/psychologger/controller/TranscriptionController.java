package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.repository.OrgSettingsRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.transcription.TranscriptionJob;
import com.pablogb.psychologger.transcription.TranscriptionJobStore;
import com.pablogb.psychologger.transcription.TranscriptionService;
import com.pablogb.psychologger.model.entity.OrgSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transcription")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;
    private final TranscriptionJobStore jobStore;
    private final OrgSettingsRepository orgSettingsRepository;
    private final SecurityUtils securityUtils;

    @PostMapping("/transcribe")
    public ResponseEntity<Map<String, String>> transcribe(
            @RequestParam("audio") MultipartFile audioFile) throws Exception {

        // get language from org settings
        String language = orgSettingsRepository
                .findByOrganizationId(securityUtils.getCurrentOrgId())
                .map(OrgSettings::getPreferredLanguage)
                .orElse("auto");

        // save uploaded file
        File tempDir = new File("tmp").getAbsoluteFile();
        if (!tempDir.exists()) tempDir.mkdirs();

        File tempInput = new File(tempDir,
                "upload_" + System.currentTimeMillis() + "_"
                        + audioFile.getOriginalFilename());

        try (InputStream inputStream = audioFile.getInputStream()) {
            Files.copy(inputStream, tempInput.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        // create job and start async processing
        String jobId = UUID.randomUUID().toString();
        String userId = securityUtils.getCurrentUserId().toString();

        TranscriptionJob job = new TranscriptionJob(jobId, userId);
        jobStore.put(job);

        // this returns immediately — processing happens on background thread
        transcriptionService.processAsync(jobId, tempInput, language);

        return ResponseEntity.ok(Map.of("jobId", jobId));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<?> getStatus(@PathVariable String jobId) {
        TranscriptionJob job = jobStore.get(jobId);

        if (job == null) {
            return ResponseEntity.notFound().build();
        }

        // security check — only the user who started the job can poll it
        String currentUserId = securityUtils.getCurrentUserId().toString();
        if (!job.getUserId().equals(currentUserId)) {
            return ResponseEntity.status(403).build();
        }

        return switch (job.getStatus()) {
            case PROCESSING -> ResponseEntity.ok(Map.of(
                    "status", "PROCESSING"
            ));
            case DONE -> ResponseEntity.ok(Map.of(
                    "status", "DONE",
                    "text", job.getText()
            ));
            case FAILED -> ResponseEntity.ok(Map.of(
                    "status", "FAILED",
                    "error", job.getErrorMessage() != null
                            ? job.getErrorMessage()
                            : "Unknown error"
            ));
        };
    }
}