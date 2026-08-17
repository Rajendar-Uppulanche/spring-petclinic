import pytest
from datetime import datetime, timedelta
from unittest.mock import MagicMock
from models.appointment import Appointment, AppointmentStatus, Veterinarian
from repositories.appointment_repository import AppointmentRepository
from services.appointment_service import AppointmentService, AppointmentFilterParams
from pydantic import ValidationError

# Mock database session for repository tests
@pytest.fixture
def mock_db_session():
    return MagicMock()

# Fixture for AppointmentRepository
@pytest.fixture
def appointment_repository(mock_db_session):
    return AppointmentRepository(mock_db_session)

# Fixture for AppointmentService
@pytest.fixture
def appointment_service(appointment_repository):
    return AppointmentService(appointment_repository)

# Sample data
@pytest.fixture
def sample_appointments():
    vet1 = Veterinarian(id=1, name="Dr. Smith")
    vet2 = Veterinarian(id=2, name="Dr. Jones")
    return [
        Appointment(id=1, pet_name="Buddy", owner_name="Alice", appointment_date=datetime(2023, 10, 26, 10, 0), reason="Checkup", veterinarian=vet1, status=AppointmentStatus.SCHEDULED),
        Appointment(id=2, pet_name="Lucy", owner_name="Bob", appointment_date=datetime(2023, 10, 27, 11, 0), reason="Vaccination", veterinarian=vet1, status=AppointmentStatus.COMPLETED),
        Appointment(id=3, pet_name="Max", owner_name="Alice", appointment_date=datetime(2023, 10, 28, 14, 0), reason="Surgery", veterinarian=vet2, status=AppointmentStatus.SCHEDULED),
        Appointment(id=4, pet_name="Buddy", owner_name="Charlie", appointment_date=datetime(2023, 11, 1, 9, 0), reason="Follow-up", veterinarian=vet1, status=AppointmentStatus.PENDING),
        Appointment(id=5, pet_name="Daisy", owner_name="Bob", appointment_date=datetime(2023, 11, 2, 16, 0), reason="Dental", veterinarian=vet2, status=AppointmentStatus.CANCELLED),
    ]

# --- Repository Tests ---
def test_repository_find_by_filters_no_filters(appointment_repository, mock_db_session, sample_appointments):
    mock_db_session.query.return_value.all.return_value = sample_appointments
    
    result = appointment_repository.find_by_filters()
    assert len(result) == len(sample_appointments)
    mock_db_session.query.assert_called_once_with(Appointment)

def test_repository_find_by_filters_pet_name(appointment_repository, mock_db_session, sample_appointments):
    mock_db_session.query.return_value.filter.return_value.all.return_value = [sample_appointments[0], sample_appointments[3]]
    
    result = appointment_repository.find_by_filters(pet_name="Buddy")
    assert len(result) == 2
    assert all(app.pet_name == "Buddy" for app in result)
    mock_db_session.query.return_value.filter.assert_called_with(Appointment.pet_name.ilike("%Buddy%"))

def test_repository_find_by_filters_owner_name(appointment_repository, mock_db_session, sample_appointments):
    mock_db_session.query.return_value.filter.return_value.all.return_value = [sample_appointments[0], sample_appointments[2]]
    
    result = appointment_repository.find_by_filters(owner_name="Alice")
    assert len(result) == 2
    assert all(app.owner_name == "Alice" for app in result)
    mock_db_session.query.return_value.filter.assert_called_with(Appointment.owner_name.ilike("%Alice%"))

def test_repository_find_by_filters_date_range(appointment_repository, mock_db_session, sample_appointments):
    start_date = datetime(2023, 10, 27)
    end_date = datetime(2023, 10, 28, 23, 59, 59)
    mock_db_session.query.return_value.filter.return_value.filter.return_value.all.return_value = [sample_appointments[1], sample_appointments[2]]
    
    result = appointment_repository.find_by_filters(start_date=start_date, end_date=end_date)
    assert len(result) == 2
    assert all(start_date <= app.appointment_date <= end_date for app in result)
    # Check that filter was called twice for start and end date
    assert mock_db_session.query.return_value.filter.call_count == 2

def test_repository_find_by_filters_veterinarian_id(appointment_repository, mock_db_session, sample_appointments):
    mock_db_session.query.return_value.filter.return_value.all.return_value = [sample_appointments[0], sample_appointments[1], sample_appointments[3]]
    
    result = appointment_repository.find_by_filters(veterinarian_id=1)
    assert len(result) == 3
    assert all(app.veterinarian.id == 1 for app in result)

def test_repository_find_by_filters_status(appointment_repository, mock_db_session, sample_appointments):
    mock_db_session.query.return_value.filter.return_value.all.return_value = [sample_appointments[0], sample_appointments[2]]
    
    result = appointment_repository.find_by_filters(status=AppointmentStatus.SCHEDULED)
    assert len(result) == 2
    assert all(app.status == AppointmentStatus.SCHEDULED for app in result)

def test_repository_find_by_filters_combined(appointment_repository, mock_db_session, sample_appointments):
    start_date = datetime(2023, 10, 26)
    end_date = datetime(2023, 10, 27, 23, 59, 59)
    mock_db_session.query.return_value.filter.return_value.filter.return_value.filter.return_value.all.return_value = [sample_appointments[0]]
    
    result = appointment_repository.find_by_filters(
        start_date=start_date,
        end_date=end_date,
        pet_name="Buddy",
        owner_name="Alice",
        status=AppointmentStatus.SCHEDULED
    )
    assert len(result) == 1
    assert result[0].id == 1

def test_repository_find_by_filters_no_results(appointment_repository, mock_db_session):
    mock_db_session.query.return_value.filter.return_value.all.return_value = []
    
    result = appointment_repository.find_by_filters(pet_name="NonExistent")
    assert len(result) == 0

# --- Service Tests ---
def test_service_get_filtered_appointments_valid_filters(appointment_service, appointment_repository, sample_appointments):
    # Mock the repository's find_by_filters method
    appointment_repository.find_by_filters.return_value = [sample_appointments[0]]

    filters = AppointmentFilterParams(
        start_date=datetime(2023, 10, 26),
        end_date=datetime(2023, 10, 26, 23, 59, 59),
        pet_name="Buddy",
        owner_name="Alice",
        status=AppointmentStatus.SCHEDULED
    )
    result = appointment_service.get_filtered_appointments(filters)
    assert len(result) == 1
    assert result[0].id == 1
    appointment_repository.find_by_filters.assert_called_once_with(
        start_date=filters.start_date,
        end_date=filters.end_date,
        pet_name=filters.pet_name,
        owner_name=filters.owner_name,
        veterinarian_id=filters.veterinarian_id,
        status=filters.status
    )

def test_service_get_filtered_appointments_invalid_date_range(appointment_service):
    filters = AppointmentFilterParams(
        start_date=datetime(2023, 10, 28),
        end_date=datetime(2023, 10, 27)
    )
    with pytest.raises(ValueError, match="start_date cannot be after end_date"):
        appointment_service.get_filtered_appointments(filters)

def test_service_get_filtered_appointments_invalid_status_enum(appointment_service):
    # This test checks Pydantic validation for the enum
    with pytest.raises(ValidationError):
        AppointmentFilterParams(status="INVALID_STATUS")

def test_service_get_filtered_appointments_no_results(appointment_service, appointment_repository):
    appointment_repository.find_by_filters.return_value = []
    filters = AppointmentFilterParams(pet_name="NonExistent")
    result = appointment_service.get_filtered_appointments(filters)
    assert len(result) == 0
    appointment_repository.find_by_filters.assert_called_once()

def test_service_get_filtered_appointments_empty_filters(appointment_service, appointment_repository, sample_appointments):
    appointment_repository.find_by_filters.return_value = sample_appointments
    filters = AppointmentFilterParams()
    result = appointment_service.get_filtered_appointments(filters)
    assert len(result) == len(sample_appointments)
    appointment_repository.find_by_filters.assert_called_once_with(
        start_date=None, end_date=None, pet_name=None, owner_name=None, veterinarian_id=None, status=None
    )
