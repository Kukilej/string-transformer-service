package com.kuzminac.string_transformer_service.service.impl;

import com.kuzminac.string_transformer_service.annotation.TransformerType;
import com.kuzminac.string_transformer_service.service.StringTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
@TransformerType(groupId = "regex", transformerId = "remove")
public class RegexRemovalTransformer implements StringTransformer {

    private static final String GROUP_ID = "regex";
    private static final String TRANSFORMER_ID = "remove";

    @Override
    public boolean supports(String groupId, String transformerId) {
        return GROUP_ID.equals(groupId) && TRANSFORMER_ID.equals(transformerId);
    }

    @Override
    public String transform(String input, Map<String, String> parameters) {
        if (parameters == null || !parameters.containsKey("pattern")) {
            log.warn("No 'pattern' parameter provided for regex removal transformer");
            return input;
        }

        String patternStr = parameters.get("pattern");
        try {
            Pattern pattern = Pattern.compile(patternStr);
            return pattern.matcher(input).replaceAll("");
        } catch (Exception e) {
            log.error("Error applying regex removal transformer with pattern: {}", patternStr, e);
            return input;
        }
    }
}
