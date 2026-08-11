
from datetime import date

from flask import Flask, render_template, flash, redirect, url_for, request, abort
from flask_login import current_user, login_required

from app import db
from app.models.user import User
from app.models.visit import Visit
from app.models.pet import Pet
from app.models.owner import Owner
from app.models.veterinarian import Veterinarian
from app.forms.visit_form import VisitForm
from app.forms.edit_visit_form import EditVisitForm
from app.services.payment_service import PaymentService


visit_bp = Flask(__name__)


@visit_bp.route("/visits/new/<int:pet_id>", methods=["GET", "POST"])
@login_required
def new_visit(pet_id):
    pet = Pet.query.get_or_404(pet_id)
    if pet.owner_id != current_user.id:
        abort(403)
    form = VisitForm()
    form.veterinarian.choices = [(v.id, v.name) for v in Veterinarian.query.all()]
    if form.validate_on_submit():
        # Check for future dates
        if form.date.data > date.today():
            flash("Visit date cannot be in the future.", "danger")
            return render_template("create_visit.html", form=form, pet=pet)

        visit = Visit(
            date=form.date.data,
            description=form.description.data,
            pet_id=pet_id,
            veterinarian_id=form.veterinarian.data
        )
        db.session.add(visit)
        db.session.commit()
        flash("Visit added successfully!", "success")
        return redirect(url_for("owner.profile", owner_id=pet.owner_id))
    return render_template("create_visit.html", form=form, pet=pet)


@visit_bp.route("/visits/<int:visit_id>/edit", methods=["GET", "POST"])
@login_required
def edit_visit(visit_id):
    visit = Visit.query.get_or_404(visit_id)
    pet = Pet.query.get_or_404(visit.pet_id)
    if pet.owner_id != current_user.id:
        abort(403)

    form = EditVisitForm(obj=visit)
    # Keep original date and vet if not editing them
    original_date = visit.date
    original_veterinarian_id = visit.veterinarian_id

    if form.validate_on_submit():
        # Only allow editing description
        visit.description = form.description.data
        # Ensure date and veterinarian are not changed if they were not meant to be
        visit.date = original_date
        visit.veterinarian_id = original_veterinarian_id

        db.session.commit()
        flash("Visit description updated successfully!", "success")
        return redirect(url_for("owner.profile", owner_id=pet.owner_id))

    # Pre-populate form with existing data
    form.description.data = visit.description
    return render_template("edit_visit.html", form=form, visit=visit, pet=pet)


@visit_bp.route("/visits/<int:visit_id>/delete", methods=["POST"])
@login_required
def delete_visit(visit_id):
    visit = Visit.query.get_or_404(visit_id)
    pet = Pet.query.get_or_404(visit.pet_id)
    if pet.owner_id != current_user.id:
        abort(403)
    db.session.delete(visit)
    db.session.commit()
    flash("Visit deleted successfully.", "success")
    return redirect(url_for("owner.profile", owner_id=pet.owner_id))


@visit_bp.route("/visits/<int:visit_id>/process_payment", methods=["POST"])
@login_required
def process_visit_payment(visit_id):
    visit = Visit.query.get_or_404(visit_id)
    pet = Pet.query.get_or_404(visit.pet_id)
    if pet.owner_id != current_user.id:
        abort(403)

    payment_service = PaymentService()
    try:
        payment_service.process_payment(visit_id=visit.id, amount=100.00) # Example amount
        flash("Payment processed successfully.", "success")
    except Exception as e:
        flash(f"Payment failed: {e}", "danger")

    return redirect(url_for("owner.profile", owner_id=pet.owner_id))
