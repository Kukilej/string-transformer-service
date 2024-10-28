package com.kuzminac.string_transformer_service.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ScriptConverterTransformerTest {

    @InjectMocks
    private ScriptConverterTransformer transformer;

    @ParameterizedTest
    @MethodSource("provideTransformTestCases")
    void transform_ShouldCorrectlyConvertScripts(String input, String expected) {
        // Act
        String result = transformer.transform(input, Map.of());

        // Assert
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideTransformTestCases() {
        return Stream.of(
                Arguments.of("Привет", "Privet"),
                Arguments.of("Hello World", "Hello World"),
                Arguments.of("Café", "Cafe"),
                Arguments.of("Пример", "Primer"),
                Arguments.of("αβγ", "abg"),
                Arguments.of("éçà", "eca"),
                Arguments.of(null, null),
                Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @MethodSource("provideErrorHandlingTestCases")
    void transform_ShouldHandleErrorsGracefully(String input, Map<String, String> parameters, String expected) {
        // Act
        String result = transformer.transform(input, parameters);

        // Assert
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideErrorHandlingTestCases() {
        return Stream.of(
                Arguments.of(null, Map.of(), null),  // Null input should throw an exception
                Arguments.of("", Map.of(), ""),      // Empty input should return an empty string
                Arguments.of("Пример", null, "Primer") // Null parameters should default to normal transformation
        );
    }
}