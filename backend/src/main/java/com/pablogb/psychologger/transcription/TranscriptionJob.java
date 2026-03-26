package com.pablogb.psychologger.transcription;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class TranscriptionJob {

    public enum Status { PROCESSING, DONE, FAILED }

    private final String id;
    private final String userId;
    private Status status;
    private String text;
    private String errorMessage;
    private final LocalDateTime createdAt;

    public TranscriptionJob(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.status = Status.PROCESSING;
        this.createdAt = LocalDateTime.now();
    }
}
