package com.kuzminac.string_transformer_service.controller;

import com.kuzminac.string_transformer_service.exception.ErrorResponse;
import com.kuzminac.string_transformer_service.model.TransformRequest;
import com.kuzminac.string_transformer_service.model.TransformResponse;
import com.kuzminac.string_transformer_service.service.TransformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transform")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "String Transformation", description = "APIs for transforming strings using different transformers")
public class TransformationController {

    private final TransformationService transformationService;

    @Operation(summary = "Transform elements", description = "Apply a series of transformations to a list of elements.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transformation successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransformResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<List<TransformResponse>> transformStrings(
            @Valid @RequestBody TransformRequest request) {
        log.info("Received transformation request with {} elements", request.elements().size());
        List<TransformResponse> response = transformationService.transformElements(request);
        return ResponseEntity.ok(response);
    }
}
