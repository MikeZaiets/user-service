package ua.com.zmike.userservice.exception.config;


import ua.com.zmike.userservice.exception.dto.ExceptionDto;

public abstract class AbstractMessagingException extends RuntimeException {

    private final ExceptionDetails errorType;
    private final Object[] extra;

    protected AbstractMessagingException(Throwable cause, ExceptionDetails errorType) {
        super(cause);
        this.errorType = errorType;
        this.extra = null;
    }

    protected AbstractMessagingException(ExceptionDetails errorType) {
        this.errorType = errorType;
        this.extra = null;
    }

    protected AbstractMessagingException(ExceptionDetails errorType, Object... extra) {
        this.errorType = errorType;
        this.extra = extra;
    }

    @Override
    public String getMessage() {
        return getExceptionDto().getMessage();
    }

    public ExceptionDto getExceptionDto() {
        return errorType.buildExceptionDto(extra);
    }
}
