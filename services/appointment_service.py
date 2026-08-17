from datetime import datetime
from typing import List, Optional
from repositories.appointment_repository import AppointmentRepository
from models.appointment import Appointment, AppointmentStatus
from pydantic import ValidationError, BaseModel, Field # Assuming Pydantic for validation

class AppointmentCreate(BaseModel):
    pet_name: str = Field(min_length=1, max_length=100)
    owner_name: str = Field(min_length=1, max_length=100)
    appointment_date: datetime
    reason: str = Field(min_length=1, max_length=500)
    veterinarian_id: Optional[int] = None
    status: Optional[AppointmentStatus] = AppointmentStatus.SCHEDULED

class AppointmentUpdate(BaseModel):
    pet_name: Optional[str] = Field(None, min_length=1, max_length=100)
    owner_name: Optional[str] = Field(None, min_length=1, max_length=100)
    appointment_date: Optional[datetime] = None
    reason: Optional[str] = Field(None, min_length=1, max_length=500)
    veterinarian_id: Optional[int] = None
    status: Optional[AppointmentStatus] = None

class AppointmentFilterParams(BaseModel):
    start_date: Optional[datetime] = None
    end_date: Optional[datetime] = None
    pet_name: Optional[str] = Field(None, min_length=1, max_length=100)
    owner_name: Optional[str] = Field(None, min_length=1, max_length=100)
    veterinarian_id: Optional[int] = None
    status: Optional[AppointmentStatus] = None

    # Custom validation for date range
    def model_post_init(self, __context):
        if self.start_date and self.end_date and self.start_date > self.end_date:
            raise ValueError("start_date cannot be after end_date")

class AppointmentService:
    def __init__(self, repository: AppointmentRepository):
        self.repository = repository

    def create_appointment(self, appointment_data: AppointmentCreate) -> Appointment:
        # Basic validation (more complex business logic can go here)
        if appointment_data.appointment_date < datetime.now():
            raise ValueError("Appointment date cannot be in the past.")
        
        appointment = Appointment(**appointment_data.model_dump())
        return self.repository.create_appointment(appointment)

    def get_appointment(self, appointment_id: int) -> Appointment | None:
        return self.repository.get_appointment_by_id(appointment_id)

    def get_all_appointments(self) -> List[Appointment]:
        return self.repository.get_all_appointments()

    def update_appointment(self, appointment_id: int, appointment_data: AppointmentUpdate) -> Appointment | None:
        appointment = self.repository.get_appointment_by_id(appointment_id)
        if not appointment:
            return None
        
        for field, value in appointment_data.model_dump(exclude_unset=True).items():
            setattr(appointment, field, value)
        
        return self.repository.update_appointment(appointment)

    def delete_appointment(self, appointment_id: int) -> bool:
        return self.repository.delete_appointment(appointment_id)

    def get_filtered_appointments(self, filters: AppointmentFilterParams) -> List[Appointment]:
        """
        Retrieves appointments based on validated filter parameters.
        """
        try:
            # Pydantic model handles initial validation
            # Additional business logic validation can be added here if needed
            pass
        except ValidationError as e:
            raise ValueError(f"Invalid filter input: {e.errors()}")
        except ValueError as e:
            raise e # Re-raise custom validation errors from model_post_init

        return self.repository.find_by_filters(
            start_date=filters.start_date,
            end_date=filters.end_date,
            pet_name=filters.pet_name,
            owner_name=filters.owner_name,
            veterinarian_id=filters.veterinarian_id,
            status=filters.status
        )
