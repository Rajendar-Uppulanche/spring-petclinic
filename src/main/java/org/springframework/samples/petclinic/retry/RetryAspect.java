package org.springframework.samples.petclinic.retry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class RetryAspect {

    private static final Logger log = LoggerFactory.getLogger(RetryAspect.class);

    @Around("@annotation(org.springframework.samples.petclinic.retry.RetryOnTransientFailure)")
    public Object retryOnTransientFailure(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RetryOnTransientFailure retryAnnotation = method.getAnnotation(RetryOnTransientFailure.class);

        int maxAttempts = retryAnnotation.maxAttempts();
        long delayMillis = retryAnnotation.delayMillis();
        Class<? extends Throwable>[] retryableExceptions = retryAnnotation.retryableExceptions();

        Throwable lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                boolean isRetryable = false;
                for (Class<? extends Throwable> retryableException : retryableExceptions) {
                    if (retryableException.isInstance(ex)) {
                        isRetryable = true;
                        break;
                    }
                }

                if (isRetryable) {
                    lastException = ex;
                    if (attempt < maxAttempts) {
                        log.warn("Attempt {} of {} for method {}.{}() failed with transient error: {}. Retrying in {}ms...",
                                attempt, maxAttempts, method.getDeclaringClass().getSimpleName(), method.getName(),
                                ex.getMessage(), delayMillis);
                        Thread.sleep(delayMillis);
                    } else {
                        log.error("Attempt {} of {} for method {}.{}() failed with transient error: {}. Max retries exhausted.",
                                attempt, maxAttempts, method.getDeclaringClass().getSimpleName(), method.getName(),
                                ex.getMessage());
                    }
                } else {
                    // Not a retryable exception, re-throw immediately
                    throw ex;
                }
            }
        }
        // If we reach here, all retries failed
        throw lastException;
    }
}
