package ua.com.zmike.userservice.exception;


import ua.com.zmike.userservice.exception.config.AbstractMessagingException;
import ua.com.zmike.userservice.exception.config.ExceptionDetails;

public class ApplicationConstraintViolationException extends AbstractMessagingException {

    public ApplicationConstraintViolationException(Object... unacceptableData) {
        super(ExceptionDetails.CONSTRAINT_VIOLATION, unacceptableData);
    }
}
