package ua.com.zmike.userservice.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private int min;
    private int max;
    private String pattern;

    @Override
    public void initialize(Phone contactNumber) {
        min = contactNumber.min();
        max = contactNumber.max();
        pattern = contactNumber.pattern();
    }

    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
        return contactField != null
                && contactField.matches(pattern)
                && (contactField.length() >= min)
                && (contactField.length() <= max);
    }
}
