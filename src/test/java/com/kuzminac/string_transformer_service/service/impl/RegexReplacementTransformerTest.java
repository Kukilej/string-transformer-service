package com.kuzminac.string_transformer_service.service.impl;

import com.kuzminac.string_transformer_service.exception.ValidationException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class RegexReplacementTransformerTest {

    @InjectMocks
    private RegexReplacementTransformer transformer;

    @ParameterizedTest
    @MethodSource("provideTransformTestCases")
    void transform_ShouldCorrectlyReplacePatterns(
            String input, String pattern, String replacement, String expected) {
        // Arrange
        Map<String, String> parameters = Map.of(
                "pattern", pattern,
                "replacement", replacement
        );

        // Act
        String result = transformer.transform(input, parameters);

        // Assert
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideTransformTestCases() {
        return Stream.of(
                Arguments.of("hello123world", "\\d+", "!", "hello!world"),
                Arguments.of("test@email.com", "@.*", "@example.com", "test@example.com"),
                Arguments.of("replace spaces", "\\s+", "-", "replace-spaces"),
                Arguments.of("", "\\d+", "x", ""),
                Arguments.of("12345", "\\d+", "num", "num")
        );
    }

    @ParameterizedTest
    @MethodSource("provideErrorTestCases")
    void transform_ShouldHandleErrorsGracefully(String input, String pattern, String replacement) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("pattern", pattern);
        parameters.put("replacement", replacement);

        assertThrows(ValidationException.class, () -> {
            transformer.transform(input, parameters);
        });
    }

    private static Stream<Arguments> provideErrorTestCases() {
        return Stream.of(
                Arguments.of("test", null, "replacement"),
                Arguments.of("test", "\\d+", null),
                Arguments.of(null, "\\d+", "replacement")
        );
    }
}