package com.thecoffeetaster.coffee.infrastructure.adapter.in;

import com.thecoffeetaster.common.response.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalInExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request){
        String path = request.getContextPath();
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .code("VALIDATION_FAILED")
                .message("Error in entry fields")
                .path(path)
                .validationErrors(ex.getBindingResult())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

}
