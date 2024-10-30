package com.kuzminac.string_transformer_service.service;

import com.kuzminac.string_transformer_service.exception.ValidationException;
import com.kuzminac.string_transformer_service.model.Element;
import com.kuzminac.string_transformer_service.model.TransformRequest;
import com.kuzminac.string_transformer_service.model.TransformResponse;
import com.kuzminac.string_transformer_service.model.TransformerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransformationServiceTest {


    @Mock
    private StringTransformer mockTransformer1;
    @Mock
    private TransformerRegistry transformerRegistry;

    @Mock
    private StringTransformer mockTransformer2;

    @InjectMocks
    private TransformationService transformationService;


    @Test
    void transformElements_ShouldApplyTransformersSuccessfully() {
        // Arrange
        TransformerConfig config1 = new TransformerConfig("group1", "id1", Map.of("key1", "value1"));
        TransformerConfig config2 = new TransformerConfig("group2", "id2", Map.of("key2", "value2"));

        Element element = new Element("test", List.of(config1, config2));
        TransformRequest request = new TransformRequest(List.of(element));

        when(transformerRegistry.getTransformer("group1", "id1")).thenReturn(mockTransformer1);
        when(transformerRegistry.getTransformer("group2", "id2")).thenReturn(mockTransformer2);

        when(mockTransformer1.transform("test", Map.of("key1", "value1"))).thenReturn("intermediate");
        when(mockTransformer2.transform("intermediate", Map.of("key2", "value2"))).thenReturn("final");

        // Act
        List<TransformResponse> responses = transformationService.transformElements(request);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).originalValue()).isEqualTo("test");
        assertThat(responses.get(0).transformedValue()).isEqualTo("final");

        verify(transformerRegistry).getTransformer("group1", "id1");
        verify(transformerRegistry).getTransformer("group2", "id2");
        verify(mockTransformer1).transform("test", Map.of("key1", "value1"));
        verify(mockTransformer2).transform("intermediate", Map.of("key2", "value2"));
    }

    @Test
    void transformElements_ShouldHandleMissingTransformerGracefully() {
        // Arrange
        TransformerConfig config1 = new TransformerConfig("group1", "id1", Map.of("key1", "value1"));
        TransformerConfig config2 = new TransformerConfig("group3", "id3", Map.of("key3", "value3"));

        Element element = new Element("test", List.of(config1, config2));
        TransformRequest request = new TransformRequest(List.of(element));

        when(transformerRegistry.getTransformer("group1", "id1")).thenReturn(mockTransformer1);
        when(transformerRegistry.getTransformer("group3", "id3")).thenReturn(null); // Transformer not found

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            transformationService.transformElements(request);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid transformer type: group3:id3");

        verify(transformerRegistry).getTransformer("group1", "id1");
        verify(transformerRegistry).getTransformer("group3", "id3");
        verify(mockTransformer1).transform("test", Map.of("key1", "value1"));
        verifyNoInteractions(mockTransformer2);
    }

    @Test
    void transformElements_ShouldHandleEmptyTransformersList() {
        // Arrange
        Element element = new Element("test", List.of());
        TransformRequest request = new TransformRequest(List.of(element));

        // Act
        List<TransformResponse> responses = transformationService.transformElements(request);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).originalValue()).isEqualTo("test");
        assertThat(responses.get(0).transformedValue()).isEqualTo("test");
    }

    @Test
    void transformElements_ShouldReturnEmptyResponseForEmptyRequest() {
        // Arrange
        TransformRequest request = new TransformRequest(List.of());

        // Act
        List<TransformResponse> responses = transformationService.transformElements(request);

        // Assert
        assertThat(responses).isEmpty();
    }
}