package com.i5e2.likeawesomevegetable.domain.verification.exception;

import com.i5e2.likeawesomevegetable.domain.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VerificationExceptionManager {
    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<?> appExceptionHandler(VerificationException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(
                Result.error(
                        VerificationErrorResponse.builder()
                                .errorCode(e.getErrorCode())
                                .contents(e.getMessage())
                                .build()
                )
        );
    }
}
