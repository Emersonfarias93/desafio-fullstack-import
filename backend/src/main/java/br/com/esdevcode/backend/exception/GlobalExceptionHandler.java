package br.com.esdevcode.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
                NoHandlerFoundException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.NOT_FOUND,
                    "Rota não encontrada.",
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
                NoResourceFoundException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.NOT_FOUND,
                    "Recurso não encontrado.",
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
                ResourceNotFoundException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiErrorResponse> handleBusinessException(
                BusinessException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage(),
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
                IllegalArgumentException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage(),
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidationException(
                MethodArgumentNotValidException exception,
                HttpServletRequest request
        ) {
            List<String> details = exception.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    "Erro de validação nos campos enviados.",
                    request.getRequestURI(),
                    details
            );
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiErrorResponse> handleMissingParameter(
                MissingServletRequestParameterException exception,
                HttpServletRequest request
        ) {
            String message = "Parâmetro obrigatório não informado: " + exception.getParameterName();

            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    message,
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
                MethodArgumentTypeMismatchException exception,
                HttpServletRequest request
        ) {
            String message = "Parâmetro inválido: " + exception.getName();

            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    message,
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
        public ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType(
                HttpMediaTypeNotSupportedException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Tipo de mídia não suportado.",
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceeded(
                MaxUploadSizeExceededException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Arquivo muito grande. O tamanho máximo permitido é 100MB.",
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
                DataIntegrityViolationException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.CONFLICT,
                    "Violação de integridade dos dados.",
                    request.getRequestURI(),
                    List.of()
            );
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleGenericException(
                Exception exception,
                HttpServletRequest request
        ) {
            log.error("Erro interno inesperado em {}", request.getRequestURI(), exception);

            return buildResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno inesperado.",
                    request.getRequestURI(),
                    List.of()
            );
        }

        private ResponseEntity<ApiErrorResponse> buildResponse(
                HttpStatus status,
                String message,
                String path,
                List<String> details
        ) {
            ApiErrorResponse response = new ApiErrorResponse(
                    LocalDateTime.now(),
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    path,
                    details
            );

            return ResponseEntity.status(status).body(response);
        }
}
