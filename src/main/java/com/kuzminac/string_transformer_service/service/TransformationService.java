package com.kuzminac.string_transformer_service.service;

import com.kuzminac.string_transformer_service.annotation.TransformerType;
import com.kuzminac.string_transformer_service.model.Element;
import com.kuzminac.string_transformer_service.model.TransformRequest;
import com.kuzminac.string_transformer_service.model.TransformResponse;
import com.kuzminac.string_transformer_service.model.TransformerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransformationService {

    private final Map<String, StringTransformer> transformerRegistry = new HashMap<>();

    public TransformationService(List<StringTransformer> transformers) {
        registerTransformers(transformers);
    }

    private void registerTransformers(List<StringTransformer> transformers) {
        for (StringTransformer transformer : transformers) {
            TransformerType annotation = transformer.getClass().getAnnotation(TransformerType.class);
            if (annotation != null) {
                String key = buildTransformerKey(annotation.groupId(), annotation.transformerId());
                transformerRegistry.put(key, transformer);
                log.info("Registered transformer: {}", key);
            } else {
                log.warn("Transformer {} does not have a TransformerType annotation", transformer.getClass().getName());
            }
        }
    }


    public List<TransformResponse> transformElements(TransformRequest request) {
        return request.elements().stream()
                .map(this::transformElement)
                .collect(Collectors.toList());
    }

    private TransformResponse transformElement(Element element) {

        String originalValue = element.value();
        String transformedValue = originalValue;

        for (TransformerConfig config : element.transformers()) {
            String key = buildTransformerKey(config.groupId(), config.transformerId());
            StringTransformer transformer = transformerRegistry.get(key);

            if (transformer != null) {
                try {
                    log.debug("Applying transformer [{}] with parameters: {}", key, config.parameters());
                    transformedValue = transformer.transform(transformedValue, config.parameters());
                } catch (Exception e) {
                    log.error("Error applying transformer [{}]: {}", key, e.getMessage(), e);
                    // Continue with the last successful transformation value
                }
            } else {
                log.warn("Transformer not found: {}", key);
            }
        }
        return TransformResponse.of(originalValue, transformedValue);
    }
    private String buildTransformerKey(String groupId, String transformerId) {
        return String.format("%s:%s", groupId, transformerId);
    }
}