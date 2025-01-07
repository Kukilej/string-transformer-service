package com.kuzminac.string_transformer_service.service.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing and retrieving string transformers.
 */
@Slf4j
@Component
public final class TransformerRegistry {

    private final Map<TransformerKey, StringTransformer> registry = new ConcurrentHashMap<>();

    @Autowired
    public TransformerRegistry(List<StringTransformer> allTransformers) {
        for (StringTransformer transformer : allTransformers) {
            TransformerType annotation = transformer.getClass().getAnnotation(TransformerType.class);
            if (annotation != null) {
                TransformerKey key = new TransformerKey(annotation.groupId(), annotation.transformerId());
                registry.put(key, transformer);
            }
        }
    }


    /**
     * Retrieves a transformer from the registry by groupId and transformerId.
     *
     * @param groupId       the groupId of the transformer
     * @param transformerId the transformerId of the transformer
     * @return the matching StringTransformer, or null if not found
     */
    public Optional<StringTransformer> getTransformer(String groupId, String transformerId) {

        TransformerKey key = new TransformerKey(groupId, transformerId);
        return Optional.ofNullable(registry.get(key));
    }
}
