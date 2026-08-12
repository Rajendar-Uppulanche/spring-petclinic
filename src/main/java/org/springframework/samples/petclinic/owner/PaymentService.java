/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * A service class for handling payment processing.
 */
@Service
public class PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	private final int maxRetryAttempts;

	public PaymentService(@Value("${payment.retry.maxAttempts:3}") int maxRetryAttempts) {
		this.maxRetryAttempts = maxRetryAttempts;
	}

	/**
	 * Processes a payment for a given visit.
	 *
	 * @param visitId The ID of the visit.
	 * @param description The description of the payment.
	 * @throws PaymentProcessingException if the payment fails after all retries.
	 * @throws TransientPaymentFailureException if a transient failure occurs during payment processing.
	 */
	public void processPayment(int visitId, String description) throws PaymentProcessingException, TransientPaymentFailureException {
		int attempt = 0;
	
		while (attempt < maxRetryAttempts) {
			try {
				// Simulate payment processing logic
				logger.info("Attempting to process payment for visit ID: {} (Attempt {}/{})", visitId, attempt + 1, maxRetryAttempts);
			
				// Simulate transient failure (e.g., network issue, temporary service unavailability)
				if (Math.random() < 0.6) { // 60% chance of transient failure
					throw new TransientPaymentFailureException("Simulated transient payment failure.");
				}
				
				// Simulate successful payment
				logger.info("Payment processed successfully for visit ID: {}", visitId);
				return; // Success
			
			} catch (TransientPaymentFailureException e) {
				attempt++;
				logger.warn("Transient payment failure for visit ID: {}. Retrying... (Attempt {}/{})", visitId, attempt, maxRetryAttempts, e);
				
				if (attempt >= maxRetryAttempts) {
					logger.error("Payment processing failed for visit ID: {} after {} retries.", visitId, maxRetryAttempts);
					throw new PaymentProcessingException("Payment processing failed after multiple retries.", e);
				}
				
				// Optional: Add a delay before retrying
			try {
				Thread.sleep(1000 * attempt); // Exponential backoff or fixed delay
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				throw new PaymentProcessingException("Payment processing interrupted during retry delay.", ie);
			}
			}
		}
		
		// This part should ideally not be reached if maxRetryAttempts > 0, but as a fallback:
		throw new PaymentProcessingException("Payment processing failed due to an unexpected error.");
	}

}

class PaymentProcessingException extends Exception {
	public PaymentProcessingException(String message) {
		super(message);
	}

	public PaymentProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}

class TransientPaymentFailureException extends Exception {
	public TransientPaymentFailureException(String message) {
		super(message);
	}

	public TransientPaymentFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
