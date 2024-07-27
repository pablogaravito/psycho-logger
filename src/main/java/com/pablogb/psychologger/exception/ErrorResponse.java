package com.pablogb.psychologger.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private List<String> message;

    public ErrorResponse(List<String> message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

}
