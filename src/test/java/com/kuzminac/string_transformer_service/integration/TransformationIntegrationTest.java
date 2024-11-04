package com.kuzminac.string_transformer_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzminac.string_transformer_service.model.Element;
import com.kuzminac.string_transformer_service.model.TransformRequest;
import com.kuzminac.string_transformer_service.model.TransformResponse;
import com.kuzminac.string_transformer_service.model.TransformerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransformationIntegrationTest {

    private static final String TRANSFORM_ENDPOINT = "/api/v1/transform";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void transformEndpoint_ShouldTransformStringsSuccessfully() throws Exception {
        TransformerConfig regexConfig = new TransformerConfig("regex", "remove", Map.of("pattern", "\\d+"));
        TransformerConfig scriptConfig = new TransformerConfig("script", "convert", Map.of());
        Element element1 = new Element("Привет123", List.of(regexConfig, scriptConfig));

        TransformerConfig replacementConfig = new TransformerConfig("regex", "replace", Map.of("pattern", "test", "replacement", "exam"));
        Element element2 = new Element("test456string", List.of(replacementConfig));

        TransformRequest request = new TransformRequest(List.of(element1, element2));

        // Act: Perform the POST request using MockMvc
        MvcResult mvcResult = mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize the response, read as bytes and decode using UTF-8 to prevent loss of Cyrilic
        byte[] responseBytes = mvcResult.getResponse().getContentAsByteArray();
        String jsonResponse = new String(responseBytes, StandardCharsets.UTF_8);

        List<TransformResponse> responses = objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, TransformResponse.class));

        // Assert: Verify the transformations
        assertThat(responses).hasSize(2);

        TransformResponse response1 = responses.get(0);
        assertThat(response1.originalValue()).isEqualTo("Привет123");
        assertThat(response1.transformedValue()).isEqualTo("Privet"); // Assuming "Привет123" -> remove digits -> "Привет" -> convert script -> "Privet"

        TransformResponse response2 = responses.get(1);
        assertThat(response2.originalValue()).isEqualTo("test456string");
        assertThat(response2.transformedValue()).isEqualTo("exam456string"); // Assuming "test456string" -> replace "test" with "exam" -> "exam456string"
    }

    @Test
    void transformEndpoint_ShouldHandleInvalidTransformerType() throws Exception {
        // Arrange: Create a TransformRequest with an invalid transformer type
        TransformerConfig invalidConfig = new TransformerConfig("invalid_group", "invalid_id", Map.of());
        Element element = new Element("sample", List.of(invalidConfig));
        TransformRequest request = new TransformRequest(List.of(element));

        // Act: Perform the POST request using MockMvc
        MvcResult mvcResult = mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Deserialize the error response
        byte[] responseBytes = mvcResult.getResponse().getContentAsByteArray();
        String jsonResponse = new String(responseBytes, StandardCharsets.UTF_8);
        Map<String, String> errorResponse = objectMapper.readValue(jsonResponse, Map.class);

        // Assert: Verify the error message
        assertThat(errorResponse.get("message")).isEqualTo("Invalid transformer type: invalid_group:invalid_id");
    }

    @Test
    void transformEndpoint_ShouldHandleLargePayload() throws Exception {

        TransformerConfig regexConfig = new TransformerConfig("regex", "remove", Map.of("pattern", "\\d+"));
        TransformerConfig scriptConfig = new TransformerConfig("script", "convert", Map.of());

        List<Element> elements = new java.util.ArrayList<>();
        List<TransformResponse> expectedResponses = new java.util.ArrayList<>();

        for (int i = 0; i < 100; i++) {
            String original = "Привет" + i;
            Element element = new Element(original, List.of(regexConfig, scriptConfig));
            elements.add(element);

            String transformed = "Privet"; // Assuming all numbers are removed and converted
            expectedResponses.add(new TransformResponse(original, transformed));
        }

        TransformRequest request = new TransformRequest(elements);

        // Act: Perform the POST request using MockMvc
        MvcResult mvcResult = mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize the response
        byte[] responseBytes = mvcResult.getResponse().getContentAsByteArray();
        String jsonResponse = new String(responseBytes, StandardCharsets.UTF_8);
        List<TransformResponse> responses = objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, TransformResponse.class));

        // Assert: Verify that all transformations are correct
        assertThat(responses).hasSize(100);
        for (int i = 0; i < 100; i++) {
            TransformResponse response = responses.get(i);
            assertThat(response.originalValue()).isEqualTo("Привет" + i);
            assertThat(response.transformedValue()).isEqualTo("Privet");
        }
    }

    @Test
    void transformEndpoint_ShouldReturnBadRequest_ForInvalidPayload() throws Exception {
        String invalidJson = "{}";

        // Act: Perform the POST request using MockMvc
        MvcResult mvcResult = mockMvc.perform(post(TRANSFORM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Deserialize the error response
        byte[] responseBytes = mvcResult.getResponse().getContentAsByteArray();
        String jsonResponse = new String(responseBytes, StandardCharsets.UTF_8);
        Map<String, String> errorResponse = objectMapper.readValue(jsonResponse, Map.class);

        // Assert: Verify the error message
        assertThat(errorResponse.get("message")).contains("Validation failed"); // Adjust based on actual validation message
    }
}
