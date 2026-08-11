from datetime import date
from typing import List, Optional

from data_model.visit import Visit, VisitType
from data_model.veterinarian import Veterinarian
from repositories.owner_repository import OwnerRepository


class VisitService:
    def __init__(self, owner_repository: OwnerRepository):
        self.owner_repository = owner_repository

    def get_visits_for_pet(self, owner_id: int, pet_id: int) -> List[Visit]:
        owner = self.owner_repository.get_owner_by_id(owner_id)
        if not owner:
            return []
        pet = owner.get_pet(pet_id)
        if not pet:
            return []
        return pet.visits

    def add_visit_to_pet(self, owner_id: int, pet_id: int, visit_date: date, description: str, visit_type: VisitType, veterinarian_id: Optional[int] = None) -> Visit:
        if visit_date > date.today():
            raise ValueError("Visit date cannot be in the future.")

        owner = self.owner_repository.get_owner_by_id(owner_id)
        if not owner:
            raise ValueError(f"Owner with id {owner_id} not found.")

        pet = owner.get_pet(pet_id)
        if not pet:
            raise ValueError(f"Pet with id {pet_id} not found for owner {owner_id}.")

        new_visit = Visit(
            pet_id=pet_id,
            visit_date=visit_date,
            description=description,
            visit_type=visit_type,
            veterinarian_id=veterinarian_id
        )
        pet.visits.append(new_visit)
        self.owner_repository.save(owner)
        return new_visit

    def update_visit_description(self, owner_id: int, pet_id: int, visit_id: int, new_description: str) -> Visit:
        owner = self.owner_repository.get_owner_by_id(owner_id)
        if not owner:
            raise ValueError(f"Owner with id {owner_id} not found.")

        pet = owner.get_pet(pet_id)
        if not pet:
            raise ValueError(f"Pet with id {pet_id} not found for owner {owner_id}.")

        visit_to_update = next((v for v in pet.visits if v.id == visit_id), None)
        if not visit_to_update:
            raise ValueError(f"Visit with id {visit_id} not found for pet {pet_id}.")

        # Ensure visit date, type, and veterinarian are immutable during description update
        visit_to_update.description = new_description
        self.owner_repository.save(owner)
        return visit_to_update

    def get_all_veterinarians(self) -> List[Veterinarian]:
        # In a real application, this would fetch from a veterinarian repository
        return [
            Veterinarian(id=1, first_name="Dr. Smith", last_name="Smith"),
            Veterinarian(id=2, first_name="Dr. Jones", last_name="Jones")
        ]
