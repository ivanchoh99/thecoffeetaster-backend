package com.thecoffeetaster.common.response;

import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public record ApiErrorResponse (

    Instant timestamp,
    Integer status,
    String error,
    String code,
    String message,
    String path,
    Map<String, Object> details){

    public static class Builder {
        private final Instant timestamp = Instant.now();
        private Integer status;
        private String error;
        private String code;
        private String message;
        private String path;
        private Map<String, Object> details = new HashMap<>();

        public Builder status(Integer status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder detail(String key, Object value) {
            this.details.put(key, value);
            return this;
        }

        public Builder details(Map<String, Object> details) {
            this.details.putAll(details);
            return this;
        }

        public Builder validationErrors(BindingResult bindingResult) {
            Map<String, String> fieldErrors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            });
            this.details.put("fieldErrors", fieldErrors);
            this.details.put("errorCount", bindingResult.getErrorCount());
            return this;
        }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(timestamp, status, error, code, message, path, details);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}