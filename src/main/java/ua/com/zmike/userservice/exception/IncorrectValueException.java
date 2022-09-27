package ua.com.zmike.userservice.exception;

import ua.com.zmike.userservice.exception.config.AbstractMessagingException;
import ua.com.zmike.userservice.exception.config.ExceptionDetails;

public class IncorrectValueException extends AbstractMessagingException {

    public IncorrectValueException(String msg) {
        super(ExceptionDetails.INCORRECT_VALUE, msg);
    }

    public IncorrectValueException(Throwable cause) {
        super(cause, ExceptionDetails.INCORRECT_VALUE);
    }
}
