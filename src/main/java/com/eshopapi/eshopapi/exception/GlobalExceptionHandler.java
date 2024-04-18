package com.eshopapi.eshopapi.exception;

import com.eshopapi.eshopapi.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JsonConversionException.class)
  public ResponseEntity<ErrorResponseDTO> handleJsonConversionException(
      JsonConversionException ex, WebRequest request) {
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "JSON Conversion Error",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(InvalidProductLabelException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidProductLabelException(
      InvalidProductLabelException ex, WebRequest request) {
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Label not valid",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ProductNameAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDTO> handleProductNameAlreadyExistsException(
      ProductNameAlreadyExistsException ex, WebRequest request) {
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Product name already exists",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleProductNotFoundException(
      ProductNotFoundException ex, WebRequest request) {
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Product could not be found",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CartNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleCartNotFoundExceptions(
      CartNotFoundException ex, WebRequest request) {
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Cart could not be found",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CartAlreadyCheckedOutException.class)
  public ResponseEntity<ErrorResponseDTO> handleCartAlreadyCheckedOutExceptions(
      CartAlreadyCheckedOutException ex, WebRequest request) {
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Cart already checked out",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Bad argument",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {
    // Collect all errors
    Map<String, String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    DefaultMessageSourceResolvable::getDefaultMessage,
                    (existing, replacement) ->
                        existing)); // In case there are multiple errors for the same field

    // Create a detailed message string from the errors
    String detailedMessage =
        errors.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining(", "));

    // Prepare the error response
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            detailedMessage,
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableExceptions(
      HttpMessageNotReadableException ex, WebRequest request) {

    // Prepare the error response
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Parse Error",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
      ConstraintViolationException ex, WebRequest request) {
    // Extracting the message from ConstraintViolationException
    String errors =
        ex.getConstraintViolations().stream()
            .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
            .collect(Collectors.joining(", "));

    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            errors,
            request.getDescription(false));

    return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidDataAccessApiUsageException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidDataAccessApiUsageExceptions(
      InvalidDataAccessApiUsageException ex, WebRequest request) {

    // Prepare the error response
    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Bad argument",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
  }
}
