package ua.com.zmike.userservice.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExceptionDto {
    @Getter
    private final String name;
    @Getter
    private final String message;
}
