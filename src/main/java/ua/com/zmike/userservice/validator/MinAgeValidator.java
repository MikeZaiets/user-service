package ua.com.zmike.userservice.validator;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {

    @Value(value = "${min.user.age}")
    private int min;
    private String message;

    @Override
    public void initialize(MinAge constraint) {
        this.message = constraint.message();
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        boolean result = validate(birthDate);
        if (!result) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("%sOver: %d yo", message, min))
                    .addConstraintViolation();
        }
        return result;
    }

    private boolean validate(LocalDate birthDate) {
        var now = LocalDate.now();
        var age = birthDate.until(now).getYears();
        return birthDate.isBefore(now) && age >= min;
    }
}
