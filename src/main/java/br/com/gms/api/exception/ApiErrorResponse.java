package br.com.gms.api.exception;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public record ApiErrorResponse(int status, String error, String message, OffsetDateTime timestamp,
                List<FieldError> fields) {

        public record FieldError(String field, String message) {
        }

        public static ApiErrorResponse from(BusinessException ex) {
                return new ApiErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unprocessable Entity",
                                ex.getMessage(),
                                OffsetDateTime.now(), null);
        }

        public static ApiErrorResponse from(NotFoundException ex) {
                return new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
                                OffsetDateTime.now(), null);
        }

        public static ApiErrorResponse from(MethodArgumentNotValidException ex) {
                List<ApiErrorResponse.FieldError> fields = ex.getBindingResult().getFieldErrors()
                                .stream()
                                .map(error -> new ApiErrorResponse.FieldError(error.getField(),
                                                error.getDefaultMessage()))
                                .toList();
                return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "Dados inválidos na requisição",
                                OffsetDateTime.now(), fields);
        }

        public static ApiErrorResponse from(MethodArgumentTypeMismatchException ex) {
                return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "Parâmetro inválido: " + ex.getValue(),
                                OffsetDateTime.now(), null);
        }

        public static ApiErrorResponse from() {
                return new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                "Erro inesperado. Tente novamente mais tarde.",
                                OffsetDateTime.now(),
                                null);
        }

}
