package ua.com.zmike.userservice.exception;

import ua.com.zmike.userservice.exception.config.AbstractMessagingException;
import ua.com.zmike.userservice.exception.config.ExceptionDetails;

public class TargetNotFoundException extends AbstractMessagingException {

    public TargetNotFoundException(String targetName, String searchKey, Object searchKeyValue) {
        super(ExceptionDetails.TARGET_NOT_FOUND_BY, targetName, searchKey, searchKeyValue);
    }

    public TargetNotFoundException(String targetName) {
        super(ExceptionDetails.TARGET_NOT_FOUND, targetName);
    }
}
