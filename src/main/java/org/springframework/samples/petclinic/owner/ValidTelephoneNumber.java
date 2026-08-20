package org.springframework.samples.petclinic.owner;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom validation annotation for telephone numbers.
 * Ensures the number contains only digits, dashes, and parentheses,
 * and that after stripping non-digit characters, the length is between 10 and 15 digits.
 */
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = TelephoneNumberValidator.class)
@Documented
public @interface ValidTelephoneNumber {
    String message() default "{telephone.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
