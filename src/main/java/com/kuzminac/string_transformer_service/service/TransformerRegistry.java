package com.kuzminac.string_transformer_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing and retrieving string transformers.
 */
@Slf4j
@Component
public final class TransformerRegistry {

    private final ApplicationContext applicationContext;
    private final Map<String, StringTransformer> registry = new ConcurrentHashMap<>();

    /**
     * Constructs a new TransformerRegistry with the provided ApplicationContext.
     *
     * @param applicationContext the application context for retrieving beans
     */
    public TransformerRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        registerTransformers();
    }

    /**
     * Registers all beans annotated with @TransformerType in the application context.
     */
    private void registerTransformers() {
        Map<String, Object> transformerBeans = applicationContext.getBeansWithAnnotation(TransformerType.class);
        transformerBeans.values().stream()
                .filter(StringTransformer.class::isInstance)
                .map(StringTransformer.class::cast)
                .forEach(this::registerTransformer);
    }

    /**
     * Registers a single transformer in the registry.
     *
     * @param transformer the transformer to register
     */
    private void registerTransformer(StringTransformer transformer) {
        TransformerType annotation = transformer.getClass().getAnnotation(TransformerType.class);
        String key = buildKey(annotation.groupId(), annotation.transformerId());

        registry.computeIfAbsent(key, k -> {
            log.info("Registered transformer: {}", k);
            return transformer;
        });
    }

    /**
     * Retrieves a transformer from the registry by groupId and transformerId.
     *
     * @param groupId       the groupId of the transformer
     * @param transformerId the transformerId of the transformer
     * @return the matching StringTransformer, or null if not found
     */
    public StringTransformer getTransformer(String groupId, String transformerId) {
        String key = buildKey(groupId, transformerId);
        StringTransformer transformer = registry.get(key);

        if (transformer == null) {
            log.warn("Transformer not found for key: {}", key);
        }

        return transformer;
    }

    /**
     * Constructs a key using groupId and transformerId.
     *
     * @param groupId       the groupId of the transformer
     * @param transformerId the transformerId of the transformer
     * @return a concatenated key
     */
    private String buildKey(String groupId, String transformerId) {
        return groupId + ":" + transformerId;
    }
}
