package org.springframework.samples.petclinic.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PastOrPresentDateValidator.class)
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
public @interface PastOrPresentDate {
    String message() default "{org.springframework.samples.petclinic.validation.PastOrPresentDate.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}