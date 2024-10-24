package com.kuzminac.string_transformer_service.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record Element(
        @NotBlank String value,
        @NotEmpty @Valid List<TransformerConfig> transformers
) {}
