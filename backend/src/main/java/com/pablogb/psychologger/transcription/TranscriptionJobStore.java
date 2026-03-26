package com.pablogb.psychologger.transcription;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TranscriptionJobStore {

    // thread safe map
    private final Map<String, TranscriptionJob> jobs = new ConcurrentHashMap<>();

    public void put(TranscriptionJob job) {
        jobs.put(job.getId(), job);
    }

    public TranscriptionJob get(String jobId) {
        return jobs.get(jobId);
    }

    public void complete(String jobId, String text) {
        TranscriptionJob job = jobs.get(jobId);
        if (job != null) {
            job.setStatus(TranscriptionJob.Status.DONE);
            job.setText(text);
        }
    }

    public void fail(String jobId, String errorMessage) {
        TranscriptionJob job = jobs.get(jobId);
        if (job != null) {
            job.setStatus(TranscriptionJob.Status.FAILED);
            job.setErrorMessage(errorMessage);
        }
    }

    // clean up old jobs periodically to avoid memory leak
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        jobs.entrySet().removeIf(e ->
                e.getValue().getCreatedAt().isBefore(cutoff));
    }
}