package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.repository.OrgSettingsRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.transcription.AudioConversionService;
import com.pablogb.psychologger.transcription.WhisperCppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RestController
@RequestMapping("/api/transcription")
@RequiredArgsConstructor
public class TranscriptionController {

    private final AudioConversionService audioConversionService;
    private final WhisperCppService whisperCppService;
    private final OrgSettingsRepository orgSettingsRepository;
    private final SecurityUtils securityUtils;

    @PostMapping("/transcribe")
    public ResponseEntity<Map<String, String>> transcribe(
            @RequestParam("audio") MultipartFile audioFile) throws Exception {

        // get language from org settings
        String language = orgSettingsRepository
                .findByOrganizationId(securityUtils.getCurrentOrgId())
                .map(s -> s.getPreferredLanguage())
                .orElse("auto");

        File tempDir = new File("tmp").getAbsoluteFile();
        if (!tempDir.exists()) tempDir.mkdirs();

        File tempInput = new File(tempDir,
                "upload_" + System.currentTimeMillis() + "_"
                        + audioFile.getOriginalFilename());

        // use Files.copy instead of transferTo — more reliable
        try (InputStream inputStream = audioFile.getInputStream()) {
            Files.copy(inputStream, tempInput.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            File wavFile = audioConversionService.convertToWav(tempInput);
            String transcription = whisperCppService.transcribe(wavFile, language);

            Files.deleteIfExists(tempInput.toPath());
            Files.deleteIfExists(wavFile.toPath());

            return ResponseEntity.ok(Map.of("text", transcription));

        } catch (Exception e) {
            Files.deleteIfExists(tempInput.toPath());
            throw e;
        }
    }
}