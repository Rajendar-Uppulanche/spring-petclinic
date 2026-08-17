from sqlalchemy.orm import Session
from sqlalchemy import and_, or_
from models.appointment import Appointment, AppointmentStatus
from datetime import datetime

class AppointmentRepository:
    def __init__(self, db: Session):
        self.db = db

    def create_appointment(self, appointment: Appointment) -> Appointment:
        self.db.add(appointment)
        self.db.commit()
        self.db.refresh(appointment)
        return appointment

    def get_appointment_by_id(self, appointment_id: int) -> Appointment | None:
        return self.db.query(Appointment).filter(Appointment.id == appointment_id).first()

    def get_all_appointments(self) -> list[Appointment]:
        return self.db.query(Appointment).all()

    def update_appointment(self, appointment: Appointment) -> Appointment:
        self.db.commit()
        self.db.refresh(appointment)
        return appointment

    def delete_appointment(self, appointment_id: int):
        appointment = self.get_appointment_by_id(appointment_id)
        if appointment:
            self.db.delete(appointment)
            self.db.commit()
            return True
        return False

    def find_by_filters(self,
                        start_date: datetime | None = None,
                        end_date: datetime | None = None,
                        pet_name: str | None = None,
                        owner_name: str | None = None,
                        veterinarian_id: int | None = None,
                        status: AppointmentStatus | None = None) -> list[Appointment]:
        """
        Filters appointments based on provided criteria.
        Applies an 'AND' logical operator for all filter parameters.
        """
        query = self.db.query(Appointment)

        if start_date:
            query = query.filter(Appointment.appointment_date >= start_date)
        if end_date:
            query = query.filter(Appointment.appointment_date <= end_date)
        if pet_name:
            query = query.filter(Appointment.pet_name.ilike(f"%{pet_name}%"))
        if owner_name:
            query = query.filter(Appointment.owner_name.ilike(f"%{owner_name}%"))
        if veterinarian_id:
            query = query.filter(Appointment.veterinarian_id == veterinarian_id)
        if status:
            query = query.filter(Appointment.status == status)

        return query.all()
