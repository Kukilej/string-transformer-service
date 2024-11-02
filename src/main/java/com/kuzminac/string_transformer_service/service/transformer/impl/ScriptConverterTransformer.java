package com.kuzminac.string_transformer_service.service.transformer.impl;

import com.ibm.icu.text.Transliterator;
import com.kuzminac.string_transformer_service.service.transformer.TransformerType;
import com.kuzminac.string_transformer_service.exception.TransformationException;
import com.kuzminac.string_transformer_service.service.transformer.StringTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Map;

@Slf4j
@Component
@TransformerType(groupId = "script", transformerId = "convert")
public class ScriptConverterTransformer implements StringTransformer {

    private static final Transliterator transliterator = Transliterator.getInstance("Any-Latin");


    @Override
    public String transform(String input, Map<String, String> parameters) {
        if (input == null || input.isBlank()) {
            log.debug("Input is null or blank, skipping script conversion");
            return input;
        }

        try {
            // Convert any script to Latin
            String converted = transliterator.transliterate(input);
            // Optionally normalize and remove diacritics ("é" becomes "e", "č" becomes "c", etc)
            String normalized = Normalizer.normalize(converted, Normalizer.Form.NFKD)
                    .replaceAll("\\p{M}", "");

            log.debug("Successfully converted script to Latin: {}", converted);
            return normalized;
        } catch (Exception e) {
            log.error("Error converting scripts to Latin for input: {}", input, e);
            throw new TransformationException("Error during script conversion", e);
        }
    }
}
