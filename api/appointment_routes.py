from flask import Blueprint, request, jsonify
from services.appointment_service import AppointmentService, AppointmentCreate, AppointmentUpdate, AppointmentFilterParams
from repositories.appointment_repository import AppointmentRepository
from models.appointment import AppointmentStatus
from database import SessionLocal # Assuming a way to get a DB session
from datetime import datetime
from pydantic import ValidationError

appointment_bp = Blueprint('appointments', __name__)

# Dependency to get DB session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Helper to get service instance
def get_appointment_service():
    db = next(get_db()) # This is a simplified way, in a real app use dependency injection
    repository = AppointmentRepository(db)
    return AppointmentService(repository)

@appointment_bp.route('/appointments', methods=['POST'])
def create_appointment():
    try:
        appointment_data = AppointmentCreate(**request.json)
        service = get_appointment_service()
        new_appointment = service.create_appointment(appointment_data)
        return jsonify(new_appointment.to_dict()), 201 # Assuming a .to_dict() method for serialization
    except ValidationError as e:
        return jsonify({"error": "Invalid input", "details": e.errors()}), 400
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        return jsonify({"error": "Internal server error", "details": str(e)}), 500

@appointment_bp.route('/appointments/<int:appointment_id>', methods=['GET'])
def get_appointment(appointment_id):
    service = get_appointment_service()
    appointment = service.get_appointment(appointment_id)
    if appointment:
        return jsonify(appointment.to_dict()), 200
    return jsonify({"message": "Appointment not found"}), 404

@appointment_bp.route('/appointments', methods=['GET'])
def get_appointments():
    service = get_appointment_service()
    
    # Extract filter parameters from query string
    filters = {}
    start_date_str = request.args.get('start_date')
    end_date_str = request.args.get('end_date')
    pet_name = request.args.get('pet_name')
    owner_name = request.args.get('owner_name')
    veterinarian_id_str = request.args.get('veterinarian_id')
    status_str = request.args.get('status')

    if start_date_str:
        try:
            filters['start_date'] = datetime.fromisoformat(start_date_str)
        except ValueError:
            return jsonify({"error": "Invalid start_date format. Use YYYY-MM-DDTHH:MM:SS"}), 400
    if end_date_str:
        try:
            filters['end_date'] = datetime.fromisoformat(end_date_str)
        except ValueError:
            return jsonify({"error": "Invalid end_date format. Use YYYY-MM-DDTHH:MM:SS"}), 400
    if pet_name:
        filters['pet_name'] = pet_name
    if owner_name:
        filters['owner_name'] = owner_name
    if veterinarian_id_str:
        try:
            filters['veterinarian_id'] = int(veterinarian_id_str)
        except ValueError:
            return jsonify({"error": "Invalid veterinarian_id. Must be an integer."}), 400
    if status_str:
        try:
            filters['status'] = AppointmentStatus[status_str.upper()]
        except KeyError:
            return jsonify({"error": f"Invalid status. Must be one of: {[s.value for s in AppointmentStatus]}"}), 400

    try:
        filter_params = AppointmentFilterParams(**filters)
        appointments = service.get_filtered_appointments(filter_params)
        return jsonify([app.to_dict() for app in appointments]), 200
    except ValidationError as e:
        return jsonify({"error": "Invalid filter input", "details": e.errors()}), 400
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        return jsonify({"error": "Internal server error", "details": str(e)}), 500

@appointment_bp.route('/appointments/<int:appointment_id>', methods=['PUT'])
def update_appointment(appointment_id):
    try:
        appointment_data = AppointmentUpdate(**request.json)
        service = get_appointment_service()
        updated_appointment = service.update_appointment(appointment_id, appointment_data)
        if updated_appointment:
            return jsonify(updated_appointment.to_dict()), 200
        return jsonify({"message": "Appointment not found"}), 404
    except ValidationError as e:
        return jsonify({"error": "Invalid input", "details": e.errors()}), 400
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        return jsonify({"error": "Internal server error", "details": str(e)}), 500

@appointment_bp.route('/appointments/<int:appointment_id>', methods=['DELETE'])
def delete_appointment(appointment_id):
    service = get_appointment_service()
    if service.delete_appointment(appointment_id):
        return jsonify({"message": "Appointment deleted successfully"}), 204
    return jsonify({"message": "Appointment not found"}), 404
