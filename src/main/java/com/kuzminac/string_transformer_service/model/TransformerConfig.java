package com.kuzminac.string_transformer_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record TransformerConfig(@NotBlank String groupId, @NotBlank String transformerId,
                                @NotNull Map<String, String> parameters) {
    public TransformerConfig {
        if (parameters == null) {
            parameters = Map.of();
        }
    }
}