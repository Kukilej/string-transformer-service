package com.kuzminac.string_transformer_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzminac.string_transformer_service.model.Element;
import com.kuzminac.string_transformer_service.model.TransformRequest;
import com.kuzminac.string_transformer_service.model.TransformerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransformationIntegrationTest {

    private static final String TRANSFORM_ENDPOINT = "/api/v1/transform";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void transformEndpoint_ShouldTransformString_WhenValidRequest() throws Exception {
        TransformerConfig regexConfig = new TransformerConfig("regex", "remove", Map.of("pattern", "\\d+"));
        TransformerConfig scriptConfig = new TransformerConfig("script", "convert", Map.of());
        Element element = new Element("Привет123", List.of(regexConfig, scriptConfig));
        TransformRequest request = new TransformRequest(List.of(element));

        mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originalValue").value("Привет123"))
                .andExpect(jsonPath("$[0].transformedValue").value("Privet"));
    }

    @Test
    void transformEndpoint_ShouldHandleLargeRequest() throws Exception {
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            elements.add(new Element("test" + i, List.of(new TransformerConfig("regex", "remove", Map.of("pattern", "\\d+")))));
        }
        TransformRequest request = new TransformRequest(elements);

        mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(100)));
    }

    @Test
    void transformEndpoint_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        TransformRequest request = new TransformRequest(List.of());

        mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void transformEndpoint_ShouldReturnBadRequest_WhenInvalidTransformerType() throws Exception {
        // Arrange: Use an invalid transformer to trigger a 400 Bad Request
        Element element = new Element("test", List.of(new TransformerConfig("invalid", "id", Map.of())));
        TransformRequest request = new TransformRequest(List.of(element));

        // Act & Assert
        mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid transformer type: invalid:id"));
    }
}
