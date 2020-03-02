package com.oguz.waes.scalableweb.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class PrettyException extends RuntimeException {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private HttpStatus status;
    private String message;

    private PrettyException(String message) {
        super(message);
        this.message = message;
    }

    public static PrettyException getInstance(String message) {
        return new PrettyException(message);
    }

    public PrettyException setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
