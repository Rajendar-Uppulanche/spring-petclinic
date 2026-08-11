from datetime import date
from enum import Enum
from typing import Optional

from pydantic import BaseModel


class VisitType(str, Enum):
    ROUTINE = "Routine"
    EMERGENCY = "Emergency"
    FOLLOW_UP = "Follow-up"


class Visit(BaseModel):
    id: Optional[int] = None
    pet_id: int
    visit_date: date
    description: str
    visit_type: VisitType
    veterinarian_id: Optional[int] = None
