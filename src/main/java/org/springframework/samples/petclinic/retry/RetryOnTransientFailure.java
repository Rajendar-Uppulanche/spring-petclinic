package org.springframework.samples.petclinic.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should be retried upon encountering transient failures.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnTransientFailure {
    int maxAttempts() default 3;
    long delayMillis() default 100; // Delay between retries in milliseconds
    Class<? extends Throwable>[] retryableExceptions() default {
        org.springframework.samples.petclinic.exceptions.TransientDataAccessException.class
    };
}
