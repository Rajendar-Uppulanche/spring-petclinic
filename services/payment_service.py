import logging
from typing import Optional

from config.settings import Settings


logger = logging.getLogger(__name__)


class PaymentService:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.max_retries = settings.PAYMENT_RETRY_LIMIT

    def process_payment(self, amount: float, payment_details: dict) -> bool:
        for attempt in range(1, self.max_retries + 1):
            try:
                # Simulate payment processing
                if amount <= 0:
                    raise ValueError("Payment amount must be positive.")
                if not payment_details or 'card_number' not in payment_details:
                    raise ValueError("Invalid payment details.")

                # Simulate a transient infrastructure error
                if attempt < self.max_retries and hash(str(payment_details)) % 3 == 0: # Simulate failure 1/3 of the time for retries
                    raise ConnectionError("Simulated transient network issue.")

                logger.info(f"Payment processed successfully for amount {amount}.")
                return True
            except ConnectionError as e:
                logger.warning(f"Attempt {attempt}/{self.max_retries}: Transient error processing payment: {e}")
                if attempt == self.max_retries:
                    logger.error(f"Max retries reached. Payment failed for amount {amount}.")
                    return False
            except ValueError as e:
                logger.error(f"Payment failed due to invalid input: {e}")
                return False
            except Exception as e:
                logger.error(f"An unexpected error occurred during payment processing: {e}")
                return False
        return False
