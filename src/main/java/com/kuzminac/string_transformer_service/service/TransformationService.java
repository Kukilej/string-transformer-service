package com.kuzminac.string_transformer_service.service;

import com.kuzminac.string_transformer_service.exception.TransformationException;
import com.kuzminac.string_transformer_service.exception.ValidationException;
import com.kuzminac.string_transformer_service.model.Element;
import com.kuzminac.string_transformer_service.model.TransformRequest;
import com.kuzminac.string_transformer_service.model.TransformResponse;
import com.kuzminac.string_transformer_service.model.TransformerConfig;
import com.kuzminac.string_transformer_service.service.transformer.StringTransformer;
import com.kuzminac.string_transformer_service.service.transformer.TransformerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransformationService {

    private final TransformerRegistry transformerRegistry;

    /**
     * Transforms a list of elements based on the request.
     *
     * @param request The transformation request containing elements to transform.
     * @return A list of transformation responses.
     */
    public List<TransformResponse> transformElements(TransformRequest request) {
        return request.elements().stream()
                .map(this::transformElement)
                .collect(Collectors.toList());
    }

    private TransformResponse transformElement(Element element) {
        String originalValue = element.value();
        String transformedValue = originalValue;

        for (TransformerConfig config : element.transformers()) {
            String groupId = config.groupId();
            String transformerId = config.transformerId();

            StringTransformer transformer = transformerRegistry.getTransformer(groupId, transformerId)
                    .orElseThrow(() -> new ValidationException("Invalid transformer type: " + groupId + ":" + transformerId));

            try {
                log.debug("Applying transformer [{}:{}] with parameters: {}", groupId, transformerId, config.parameters());
                transformedValue = transformer.transform(transformedValue, config.parameters());
            } catch (ValidationException e) {
                log.error("Validation error in transformer [{}:{}]: {}", groupId, transformerId, e.getMessage(), e);
                throw e;
            } catch (TransformationException e) {
                log.error("Error during transformation [{}:{}]: {}", groupId, transformerId, e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                log.error("Unexpected error applying transformer [{}:{}]: {}", groupId, transformerId, e.getMessage(), e);
                throw new TransformationException("Unexpected transformation error", e);
            }
        }

        return TransformResponse.of(originalValue, transformedValue);
    }
}
