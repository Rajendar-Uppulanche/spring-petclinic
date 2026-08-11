from datetime import date
from typing import Optional, List

from flask import Flask, render_template, request, redirect, url_for, flash

from data_model.visit import Visit, VisitType
from data_model.veterinarian import Veterinarian
from services.visit_service import VisitService
from services.owner_service import OwnerService


def visit_routes(app: Flask, visit_service: VisitService, owner_service: OwnerService):

    @app.route('/owners/<int:owner_id>/pets/<int:pet_id>/visits/new', methods=['GET', 'POST'])
    def new_visit(owner_id: int, pet_id: int):
        if request.method == 'POST':
            visit_date_str = request.form.get('visit_date')
            description = request.form.get('description')
            visit_type_str = request.form.get('visit_type')
            veterinarian_id_str = request.form.get('veterinarian_id')

            try:
                visit_date = date.fromisoformat(visit_date_str)
                visit_type = VisitType(visit_type_str)
                veterinarian_id = int(veterinarian_id_str) if veterinarian_id_str else None

                visit = visit_service.add_visit_to_pet(owner_id, pet_id, visit_date, description, visit_type, veterinarian_id)
                flash('Visit added successfully!', 'success')
                return redirect(url_for('show_owner', owner_id=owner_id))
            except ValueError as e:
                flash(f'Error: {e}', 'danger')
                # Re-render form with existing data
                owner = owner_service.get_owner_by_id(owner_id)
                pet = owner.get_pet(pet_id) if owner else None
                return render_template('pets/createOrUpdateVisitForm.html', 
                                       owner=owner, 
                                       pet=pet, 
                                       visit_date=visit_date_str, 
                                       description=description, 
                                       visit_type=visit_type_str, 
                                       veterinarian_id=veterinarian_id_str,
                                       visit_types=list(VisitType),
                                       veterinarians=visit_service.get_all_veterinarians())
        else:
            owner = owner_service.get_owner_by_id(owner_id)
            pet = owner.get_pet(pet_id) if owner else None
            if not owner or not pet:
                flash('Owner or Pet not found.', 'danger')
                return redirect(url_for('index'))
            
            # Set minimum date for the date picker to today
            min_date = date.today().isoformat()
            return render_template('pets/createOrUpdateVisitForm.html', 
                                   owner=owner, 
                                   pet=pet, 
                                   visit_types=list(VisitType),
                                   veterinarians=visit_service.get_all_veterinarians(),
                                   min_visit_date=min_date)

    @app.route('/owners/<int:owner_id>/pets/<int:pet_id>/visits/<int:visit_id>/edit', methods=['GET', 'POST'])
    def edit_visit(owner_id: int, pet_id: int, visit_id: int):
        owner = owner_service.get_owner_by_id(owner_id)
        pet = owner.get_pet(pet_id) if owner else None
        if not owner or not pet:
            flash('Owner or Pet not found.', 'danger')
            return redirect(url_for('index'))

        visit_to_edit = next((v for v in pet.visits if v.id == visit_id), None)
        if not visit_to_edit:
            flash('Visit not found.', 'danger')
            return redirect(url_for('show_owner', owner_id=owner_id))

        if request.method == 'POST':
            new_description = request.form.get('description')
            try:
                updated_visit = visit_service.update_visit_description(owner_id, pet_id, visit_id, new_description)
                flash('Visit description updated successfully!', 'success')
                return redirect(url_for('show_owner', owner_id=owner_id))
            except ValueError as e:
                flash(f'Error updating visit: {e}', 'danger')
                # Re-render form with existing data
                return render_template('pets/editVisitForm.html', 
                                       owner=owner, 
                                       pet=pet, 
                                       visit=visit_to_edit, 
                                       description=new_description,
                                       visit_types=list(VisitType),
                                       veterinarians=visit_service.get_all_veterinarians())
        else:
            return render_template('pets/editVisitForm.html', 
                                   owner=owner, 
                                   pet=pet, 
                                   visit=visit_to_edit,
                                   visit_types=list(VisitType),
                                   veterinarians=visit_service.get_all_veterinarians())

    @app.route('/owners/<int:owner_id>')
    def show_owner(owner_id: int):
        owner = owner_service.get_owner_by_id(owner_id)
        if not owner:
            flash('Owner not found.', 'danger')
            return redirect(url_for('index'))
        return render_template('owners/ownerDetails.html', owner=owner)

    @app.route('/')
    def index():
        owners = owner_service.get_all_owners()
        return render_template('owners/ownerList.html', owners=owners)

