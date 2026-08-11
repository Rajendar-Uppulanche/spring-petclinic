import time
import logging
from config.settings import PAYMENT_RETRY_LIMIT


logger = logging.getLogger(__name__)


class PaymentService:
    def __init__(self):
        self.max_retries = PAYMENT_RETRY_LIMIT

    def process_payment(self, visit_id, amount):
        retries = 0
        while retries <= self.max_retries:
            try:
                # Simulate payment processing
                if time.time() % 5 < 2:  # Simulate transient failure
                    raise ConnectionError("Simulated network issue")

                logger.info(f"Payment successful for visit {visit_id} with amount {amount}")
                return True
            except ConnectionError as e:
                retries += 1
                logger.warning(f"Attempt {retries}/{self.max_retries} failed for visit {visit_id}: {e}. Retrying...")
                time.sleep(2)  # Wait before retrying
            except Exception as e:
                logger.error(f"An unexpected error occurred during payment for visit {visit_id}: {e}")
                raise  # Re-raise unexpected errors

        raise ConnectionError(f"Payment failed for visit {visit_id} after {self.max_retries} retries.")
