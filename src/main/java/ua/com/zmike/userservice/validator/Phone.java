package ua.com.zmike.userservice.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {

    String message() default "Phone number size exception";

    int min() default 8;

    int max() default 15;

    String pattern() default ".";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
