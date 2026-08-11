from app import db
from datetime import datetime


class Visit(db.Model):
    __tablename__ = "visits"

    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.Date, nullable=False)
    description = db.Column(db.String(200), nullable=False)
    pet_id = db.Column(db.Integer, db.ForeignKey("pets.id"), nullable=False)
    veterinarian_id = db.Column(db.Integer, db.ForeignKey("veterinarians.id"), nullable=True)

    pet = db.relationship("Pet", back_populates="visits")
    veterinarian = db.relationship("Veterinarian", back_populates="visits")

    def __repr__(self):
        return f"<Visit {self.date} for Pet ID {self.pet_id}>"
