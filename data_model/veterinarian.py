from pydantic import BaseModel


class Veterinarian(BaseModel):
    id: int
    first_name: str
    last_name: str
