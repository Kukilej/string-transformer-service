package com.kuzminac.string_transformer_service.service.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing and retrieving string transformers.
 */
@Slf4j
@Component
public final class TransformerRegistry {

    private final ApplicationContext applicationContext;
    private final Map<TransformerKey, StringTransformer> registry = new ConcurrentHashMap<>();

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
        for (Object bean : transformerBeans.values()) {
            if (bean instanceof StringTransformer transformer) {
                TransformerType annotation = transformer.getClass().getAnnotation(TransformerType.class);
                TransformerKey key = new TransformerKey(annotation.groupId(), annotation.transformerId());

                if (registry.containsKey(key)) {
                    log.warn("Duplicate transformer registration attempted for key: {}", key);
                    continue;
                }
                registry.put(key, transformer);
                log.info("Registered transformer: {}", key);
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
