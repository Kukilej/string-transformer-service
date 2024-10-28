package com.kuzminac.string_transformer_service.model;

public record TransformResponse(String originalValue, String transformedValue) {
    public static TransformResponse of(String originalValue, String transformedValue) {
        return new TransformResponse(originalValue, transformedValue);
    }
}