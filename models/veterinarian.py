from app import db


class Veterinarian(db.Model):
    __tablename__ = "veterinarians"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    specialty = db.Column(db.String(100))

    visits = db.relationship("Visit", back_populates="veterinarian")

    def __repr__(self):
        return f"<Veterinarian {self.name}>"
