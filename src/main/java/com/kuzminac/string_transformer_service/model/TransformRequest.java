package com.kuzminac.string_transformer_service.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TransformRequest(
        @NotEmpty @Valid List<Element> elements
) {}
