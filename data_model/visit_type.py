from enum import Enum


class VisitType(str, Enum):
    ROUTINE = "Routine"
    EMERGENCY = "Emergency"
    FOLLOW_UP = "Follow-up"
