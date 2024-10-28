package com.kuzminac.string_transformer_service.service.impl;

import com.kuzminac.string_transformer_service.service.TransformerType;
import com.kuzminac.string_transformer_service.exception.ValidationException;
import com.kuzminac.string_transformer_service.service.StringTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
@Component
@TransformerType(groupId = "regex", transformerId = "replace")
@RequiredArgsConstructor
public class RegexReplacementTransformer implements StringTransformer {

    @Override
    public String transform(String input, Map<String, String> parameters) {
        if (input == null) {
            log.warn("Input string is null");
            throw new ValidationException("Input string cannot be null");
        }
        if (parameters == null || !parameters.containsKey("pattern") || !parameters.containsKey("replacement")) {
            log.warn("Missing required parameters 'pattern' or 'replacement' for regex replacement transformer");
            throw new ValidationException("Missing required parameters: 'pattern' and 'replacement'");
        }

        String patternStr = parameters.get("pattern");
        String replacement = parameters.get("replacement");

        if (patternStr == null || replacement == null) {
            log.warn("Missing required parameters 'pattern' or 'replacement'");
            throw new ValidationException("Missing required parameters: 'pattern' and 'replacement'");
        }

        try {
            Pattern pattern = Pattern.compile(patternStr);
            return pattern.matcher(input).replaceAll(replacement);
        } catch (PatternSyntaxException e) {
            log.error("Invalid regex pattern: {}", patternStr, e);
            throw new ValidationException("Invalid regex pattern", e);
        }
    }
}