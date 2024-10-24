package com.kuzminac.string_transformer_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        String message,
        Map<String, String> errors
) {}
