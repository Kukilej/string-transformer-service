package com.kuzminac.string_transformer_service.service.impl;

import com.ibm.icu.text.Transliterator;
import com.kuzminac.string_transformer_service.annotation.TransformerType;
import com.kuzminac.string_transformer_service.service.StringTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Map;

@Slf4j
@Component
@TransformerType(groupId = "script", transformerId = "convert")
public class ScriptConverterTransformer implements StringTransformer {

    private static final String GROUP_ID = "script";
    private static final String TRANSFORMER_ID = "convert";

    private static final Transliterator transliterator = Transliterator.getInstance("Any-Latin");


    @Override
    public boolean supports(String groupId, String transformerId) {
        return GROUP_ID.equals(groupId) && TRANSFORMER_ID.equals(transformerId);
    }

    @Override
    public String transform(String input, Map<String, String> parameters) {
        if (input == null || input.isEmpty()) {
            log.warn("Input is null or empty, skipping script conversion");
            return input;
        }

        try {
            // Convert any script to Latin
            String converted = transliterator.transliterate(input);
            // Optionally normalize and remove diacritics ("é" becomes "e", "č" becomes "c", etc)
            String normalized = Normalizer.normalize(converted, Normalizer.Form.NFKD)
                    .replaceAll("\\p{M}", "");


            log.debug("Successfully converted script to Latin: {}", converted);
            return converted;
        } catch (Exception e) {
            log.error("Error converting scripts to Latin for input: {}", input, e);
            return input;
        }
    }
}
