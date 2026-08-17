from datetime import datetime
from enum import Enum
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Enum as SQLEnum, Index
from sqlalchemy.orm import relationship
from database import Base # Assuming a Base class for SQLAlchemy declarative models

class AppointmentStatus(Enum):
    SCHEDULED = "scheduled"
    COMPLETED = "completed"
    CANCELLED = "cancelled"
    PENDING = "pending"

class Appointment(Base):
    __tablename__ = 'appointments'

    id = Column(Integer, primary_key=True, index=True)
    pet_name = Column(String, index=True, nullable=False)
    owner_name = Column(String, index=True, nullable=False) # Added for filtering
    appointment_date = Column(DateTime, index=True, nullable=False)
    reason = Column(String, nullable=False)
    veterinarian_id = Column(Integer, ForeignKey('veterinarians.id'), index=True, nullable=True) # Added for filtering
    status = Column(SQLEnum(AppointmentStatus), index=True, default=AppointmentStatus.SCHEDULED, nullable=False) # Added for filtering

    veterinarian = relationship("Veterinarian", back_populates="appointments")

    # Add indexes for performance as per NFR-007
    __table_args__ = (
        Index('idx_appointment_date', 'appointment_date'),
        Index('idx_pet_name', 'pet_name'),
        Index('idx_owner_name', 'owner_name'),
        Index('idx_veterinarian_id', 'veterinarian_id'),
        Index('idx_status', 'status'),
    )

    def __repr__(self):
        return f"<Appointment(id={self.id}, pet_name='{self.pet_name}', owner_name='{self.owner_name}', date='{self.appointment_date}', status='{self.status.value}')>"

    def to_dict(self):
        return {
            "id": self.id,
            "pet_name": self.pet_name,
            "owner_name": self.owner_name,
            "appointment_date": self.appointment_date.isoformat(),
            "reason": self.reason,
            "veterinarian_id": self.veterinarian_id,
            "status": self.status.value
        }

# Assuming a Veterinarian model exists for the ForeignKey
class Veterinarian(Base):
    __tablename__ = 'veterinarians'
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    appointments = relationship("Appointment", back_populates="veterinarian")

    def __repr__(self):
        return f"<Veterinarian(id={self.id}, name='{self.name}')>"
