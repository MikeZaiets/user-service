package ua.com.zmike.userservice.exception.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.com.zmike.userservice.exception.dto.ExceptionDto;

@RequiredArgsConstructor
public enum ExceptionDetails {

    TARGET_NOT_FOUND_BY("Target: %s not found by: %s = %s"),
    TARGET_NOT_FOUND("Target: %s not found. "),
    INCORRECT_VALUE("Incorrect value: %s"),
    CONSTRAINT_VIOLATION("Unacceptable data: %s"),
    GENERIC_EXCEPTION("Exception: %s");

    @Getter
    private final String message;

    ExceptionDto buildExceptionDto() {
        return new ExceptionDto(this.name(), this.message);
    }

    ExceptionDto buildExceptionDto(Object... extra) {
        return new ExceptionDto(this.name(), String.format(this.message, extra));
    }
}
