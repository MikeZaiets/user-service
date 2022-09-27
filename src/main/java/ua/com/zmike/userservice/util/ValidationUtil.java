package ua.com.zmike.userservice.util;

import lombok.experimental.UtilityClass;
import ua.com.zmike.userservice.exception.IncorrectValueException;

import java.time.LocalDate;

@UtilityClass
public class ValidationUtil {

    public void validateDateRange(LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            throw new IncorrectValueException("Date 'date from' must be less then 'date to'");
        }
    }
}
