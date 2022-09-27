package ua.com.zmike.userservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ua.com.zmike.userservice.exception.ApplicationConstraintViolationException;
import ua.com.zmike.userservice.exception.GenericException;
import ua.com.zmike.userservice.exception.IncorrectValueException;
import ua.com.zmike.userservice.exception.TargetNotFoundException;
import ua.com.zmike.userservice.exception.dto.ExceptionDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j(topic = "ExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TargetNotFoundException.class)
    public ExceptionDto storageException(TargetNotFoundException ex) {
        log.error("Target not found exception, {}", ex.getMessage());
        return ex.getExceptionDto();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GenericException.class)
    public ExceptionDto customExceptionHandler(GenericException ex) {
        log.error("Application exception : {}", ex.getMessage());
        return ex.getExceptionDto();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectValueException.class)
    public ExceptionDto customExceptionHandler(IncorrectValueException ex) {
        log.error("Incorrect incoming value exception : {}", ex.getMessage());
        return ex.getExceptionDto();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ExceptionDto parseExceptionsHandler(Exception ex) {
        log.error("Failed to deserialize request {}", ex.getLocalizedMessage(), ex);
        return new ApplicationConstraintViolationException(ex.getLocalizedMessage()).getExceptionDto();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ExceptionDto parseExceptionsHandler(MissingServletRequestParameterException ex) {
        log.error("Failed to deserialize request parameter {}", ex.getParameterName(), ex);
        return new ApplicationConstraintViolationException(ex.getLocalizedMessage()).getExceptionDto();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ExceptionDto handle(MethodArgumentNotValidException ex) {
        log.error("Validation exception: {}", ex.getLocalizedMessage(), ex);
        var maybeFieldError = Optional.ofNullable(ex.getBindingResult().getFieldError());
        var detailedMessage = maybeFieldError
                .map(fieldError -> String.format("%s: %s, value = %s",
                        fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue()))
                .orElse(maybeFieldError.toString());
        return new ApplicationConstraintViolationException(detailedMessage).getExceptionDto();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ExceptionDto handle(ConstraintViolationException ex) {
        log.error("Constraint violation exception {}", ex.getLocalizedMessage(), ex);
        var message = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return new ApplicationConstraintViolationException(message).getExceptionDto();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ExceptionDto genericHandler(Exception ex) {
        log.error("Unhandled exception {}, message {}", ex.getClass().getCanonicalName(), ex.getLocalizedMessage(), ex);
        return new GenericException("Unexpected 500 error: " + ex.getLocalizedMessage()).getExceptionDto();
    }
}
