package com.kuzminac.string_transformer_service.service;

import java.util.Map;

public interface StringTransformer {

    boolean supports(String groupId, String transformerId);

    String transform(String input, Map<String, String> parameters);
}