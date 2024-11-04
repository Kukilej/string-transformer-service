package com.kuzminac.string_transformer_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzminac.string_transformer_service.model.Element;
import com.kuzminac.string_transformer_service.model.TransformRequest;
import com.kuzminac.string_transformer_service.model.TransformResponse;
import com.kuzminac.string_transformer_service.model.TransformerConfig;
import com.kuzminac.string_transformer_service.service.TransformationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;

import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransformationController.class)
class TransformationControllerTest {

    private static final String TRANSFORM_ENDPOINT = "/api/v1/transform";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransformationService transformationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void transformEndpoint_ShouldTransformString_WhenValidRequest() throws Exception {
        TransformerConfig regexConfig = new TransformerConfig("regex", "remove", Map.of("pattern", "\\d+"));
        TransformerConfig scriptConfig = new TransformerConfig("script", "convert", Map.of());
        Element element = new Element("Привет123", List.of(regexConfig, scriptConfig));
        TransformRequest request = new TransformRequest(List.of(element));

        TransformResponse response = new TransformResponse("Привет123", "Privet");
        when(transformationService.transformElements(any(TransformRequest.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originalValue").value("Привет123"))
                .andExpect(jsonPath("$[0].transformedValue").value("Privet"));

        Mockito.verify(transformationService, Mockito.times(1)).transformElements(any(TransformRequest.class));

    }

    @Test
    void transformEndpoint_ShouldHandleLargeRequest() throws Exception {
        List<Element> elements = new ArrayList<>();
        List<TransformResponse> responses = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            elements.add(new Element("test" + i, List.of(new TransformerConfig("regex", "remove", Map.of("pattern", "\\d+")))));
            responses.add(new TransformResponse("test" + i, "test"));

        }
        TransformRequest request = new TransformRequest(elements);

        when(transformationService.transformElements(any(TransformRequest.class)))
                .thenReturn(responses);

        mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(100)));

        Mockito.verify(transformationService, Mockito.times(1)).transformElements(any(TransformRequest.class));

    }

    @Test
    void transformEndpoint_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        TransformRequest request = new TransformRequest(List.of());

        mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));

        Mockito.verify(transformationService, Mockito.never()).transformElements(any(TransformRequest.class));

    }

    @Test
    void transformEndpoint_ShouldReturnBadRequest_WhenInvalidTransformerType() throws Exception {
        // Arrange: Use an invalid transformer to trigger a 400 Bad Request
        Element element = new Element("test", List.of(new TransformerConfig("invalid", "id", Map.of())));
        TransformRequest request = new TransformRequest(List.of(element));

        when(transformationService.transformElements(any(TransformRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid transformer type: invalid:id"));


        // Act & Assert
        mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid transformer type: invalid:id"));

        Mockito.verify(transformationService, Mockito.times(1)).transformElements(any(TransformRequest.class));

    }
}
