package com.kuzminac.string_transformer_service.service.impl;

import com.kuzminac.string_transformer_service.service.TransformerType;
import com.kuzminac.string_transformer_service.exception.TransformationException;
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
@TransformerType(groupId = "regex", transformerId = "remove")
@RequiredArgsConstructor
public class RegexRemovalTransformer implements StringTransformer {

    @Override
    public String transform(String input, Map<String, String> parameters) {
        if (parameters == null || !parameters.containsKey("pattern")) {
            log.warn("Missing required parameter 'pattern' for regex removal transformer");
            throw new ValidationException("Missing required parameter: 'pattern'");
        }
        String patternStr = parameters.get("pattern");

        try {
            Pattern pattern = Pattern.compile(patternStr);
            return pattern.matcher(input).replaceAll("");
        } catch (PatternSyntaxException e) {
            log.error("Invalid regex pattern: {}", patternStr, e);
            throw new ValidationException("Invalid regex pattern", e);
        } catch (Exception e) {
            log.error("Unexpected error during regex removal with pattern: {}", patternStr, e);
            throw new TransformationException("Error during regex removal", e);
        }
    }
}
