package org.springframework.samples.petclinic.retry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.samples.petclinic.exceptions.TransientDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RetryAspectTests {

    // Configuration to enable AOP and register the aspect and a test service
    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {
        @Bean
        public RetryAspect retryAspect() {
            return new RetryAspect();
        }

        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    @Autowired
    private TestService testService;

    // Mock object to simulate method calls and exceptions
    private static final Runnable mockRunnable = mock(Runnable.class);

    @BeforeEach
    void setup() {
        reset(mockRunnable); // Reset mock before each test
    }

    @Test
    void methodRetriesAndSucceeds() throws Throwable {
        // Configure mock to throw exception twice, then succeed
        doThrow(new TransientDataAccessException("Transient failure 1"))
                .doThrow(new TransientDataAccessException("Transient failure 2"))
                .doNothing() // Succeed on the third attempt
                .when(mockRunnable).run();

        testService.performOperationWithRetry();

        // Verify that the method was called 3 times (2 failures + 1 success)
        verify(mockRunnable, times(3)).run();
    }

    @Test
    void methodFailsAfterMaxRetries() {
        // Configure mock to throw exception for all 3 attempts
        doThrow(new TransientDataAccessException("Transient failure 1"))
                .doThrow(new TransientDataAccessException("Transient failure 2"))
                .doThrow(new TransientDataAccessException("Transient failure 3"))
                .when(mockRunnable).run();

        // Expect the last exception to be re-thrown
        TransientDataAccessException thrown = assertThrows(TransientDataAccessException.class, () -> {
            testService.performOperationWithRetry();
        });

        assertEquals("Transient failure 3", thrown.getMessage());
        // Verify that the method was called 3 times (max attempts)
        verify(mockRunnable, times(3)).run();
    }

    @Test
    void methodDoesNotRetryOnNonTransientException() {
        // Configure mock to throw a non-retryable exception
        doThrow(new RuntimeException("Non-transient failure"))
                .when(mockRunnable).run();

        // Expect the non-transient exception to be re-thrown immediately
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            testService.performOperationWithRetry();
        });

        assertEquals("Non-transient failure", thrown.getMessage());
        // Verify that the method was called only once
        verify(mockRunnable, times(1)).run();
    }

    @Test
    void methodSucceedsImmediately() throws Throwable {
        doNothing().when(mockRunnable).run();

        testService.performOperationWithRetry();

        // Verify that the method was called only once
        verify(mockRunnable, times(1)).run();
    }

    // A simple service to apply the retry annotation for testing
    @Component
    static class TestService {
        @RetryOnTransientFailure(maxAttempts = 3, delayMillis = 1) // Short delay for tests
        public void performOperationWithRetry() throws Throwable {
            mockRunnable.run();
        }
    }
}
