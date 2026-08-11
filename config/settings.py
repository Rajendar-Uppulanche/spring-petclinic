from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    DATABASE_URL: str = "sqlite:///./petclinic.db"
    SECRET_KEY: str = "a_very_secret_key_for_development"
    PAYMENT_RETRY_LIMIT: int = 3
