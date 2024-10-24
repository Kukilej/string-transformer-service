package com.kuzminac.string_transformer_service.model;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record TransformerConfig(
        @NotBlank String groupId,
        @NotBlank String transformerId,
        Map<String, String> parameters
) {}