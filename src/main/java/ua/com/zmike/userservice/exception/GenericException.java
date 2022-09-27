package ua.com.zmike.userservice.exception;


import ua.com.zmike.userservice.exception.config.AbstractMessagingException;
import ua.com.zmike.userservice.exception.config.ExceptionDetails;

public class GenericException extends AbstractMessagingException {

    public GenericException(String msg) {
        super(ExceptionDetails.GENERIC_EXCEPTION, msg);
    }

    public GenericException(Throwable cause) {
        super(cause, ExceptionDetails.GENERIC_EXCEPTION);
    }
}
