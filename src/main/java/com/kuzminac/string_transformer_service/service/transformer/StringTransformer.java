package com.kuzminac.string_transformer_service.service.transformer;

import java.util.Map;

public interface StringTransformer {

    /**
     * Transforms the input string based on the provided parameters.
     *
     * @param input The input string to be transformed.
     * @param parameters The parameters for the transformation process.
     * @return The transformed string.
     */
    String transform(String input, Map<String, String> parameters);
}