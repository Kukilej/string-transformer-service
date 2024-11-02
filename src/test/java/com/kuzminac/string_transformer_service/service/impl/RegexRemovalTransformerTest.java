package com.kuzminac.string_transformer_service.service.impl;

import com.kuzminac.string_transformer_service.service.transformer.impl.RegexRemovalTransformer;
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
class RegexRemovalTransformerTest {

    @InjectMocks
    private RegexRemovalTransformer transformer;

    @ParameterizedTest
    @MethodSource("provideTransformTestCases")
    void transform_ShouldCorrectlyRemovePatterns(String input, String pattern, String expected) {
        // Arrange
        Map<String, String> parameters = Map.of("pattern", pattern);

        // Act
        String result = transformer.transform(input, parameters);

        // Assert
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideTransformTestCases() {
        return Stream.of(
                Arguments.of("hello123world", "\\d+", "helloworld"),
                Arguments.of("test@email.com", "@.*", "test"),
                Arguments.of("no-matches-here", "xyz", "no-matches-here"),
                Arguments.of("", "\\d+", ""),
                Arguments.of("12345", "\\d+", ""),
                Arguments.of("abc123def456", "\\d+", "abcdef"),
                Arguments.of("Special *&^% characters", "[*&^%]", "Special  characters"),
                Arguments.of("EntireStringMatch", "EntireStringMatch", "")

        );
    }
}