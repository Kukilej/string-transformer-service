package com.kuzminac.string_transformer_service.service.impl;

import com.kuzminac.string_transformer_service.annotation.TransformerType;
import com.kuzminac.string_transformer_service.service.StringTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
@TransformerType(groupId = "regex", transformerId = "replace")
public class RegexReplacementTransformer implements StringTransformer {

    private static final String GROUP_ID = "regex";
    private static final String TRANSFORMER_ID = "replace";

    @Override
    public boolean supports(String groupId, String transformerId) {
        return GROUP_ID.equals(groupId) && TRANSFORMER_ID.equals(transformerId);
    }

    @Override
    public String transform(String input, Map<String, String> parameters) {
        if (parameters == null || !parameters.containsKey("pattern") || !parameters.containsKey("replacement")) {
            log.warn("Missing required parameters 'pattern' or 'replacement' for regex replacement transformer");
            return input;
        }

        String patternStr = parameters.get("pattern");
        String replacement = parameters.get("replacement");

        try {
            Pattern pattern = Pattern.compile(patternStr);
            return pattern.matcher(input).replaceAll(replacement);
        } catch (Exception e) {
            log.error("Error applying regex replacement with pattern: {} and replacement: {}", patternStr, replacement, e);
            return input;
        }
    }
}