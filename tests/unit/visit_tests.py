import unittest
from datetime import date, timedelta
from unittest.mock import patch, MagicMock

from app import db
from app.models.visit import Visit
from app.models.pet import Pet
from app.models.owner import Owner
from app.models.veterinarian import Veterinarian
from app.forms.visit_form import VisitForm
from app.forms.edit_visit_form import EditVisitForm
from services.payment_service import PaymentService


class TestVisitController(unittest.TestCase):

    def setUp(self):
        # Setup a mock Flask app and database context
        self.app = MagicMock()
        self.app.config = {'SECRET_KEY': 'test_secret_key'}
        self.db = MagicMock()
        self.db.session = MagicMock()
        self.visit_bp = MagicMock()
        self.visit_bp.route = MagicMock(return_value=lambda f: f)
        self.visit_bp.before_request = MagicMock(return_value=lambda f: f)

        # Mocking necessary components
        self.mock_pet = MagicMock(spec=Pet)
        self.mock_pet.id = 1
        self.mock_pet.owner_id = 1
        self.mock_pet.name = "Buddy"
        self.mock_pet.visits = []

        self.mock_owner = MagicMock(spec=Owner)
        self.mock_owner.id = 1
        self.mock_owner.pets = [self.mock_pet]

        self.mock_veterinarian = MagicMock(spec=Veterinarian)
        self.mock_veterinarian.id = 1
        self.mock_veterinarian.name = "Dr. Smith"

        # Mocking forms
        self.mock_visit_form = MagicMock(spec=VisitForm)
        self.mock_visit_form.date = MagicMock()
        self.mock_visit_form.description = MagicMock()
        self.mock_visit_form.veterinarian = MagicMock()
        self.mock_visit_form.veterinarian.choices = [(1, 'Dr. Smith')]
        self.mock_visit_form.validate_on_submit = MagicMock(return_value=True)
        self.mock_visit_form.errors = {}

        self.mock_edit_visit_form = MagicMock(spec=EditVisitForm)
        self.mock_edit_visit_form.description = MagicMock()
        self.mock_edit_visit_form.validate_on_submit = MagicMock(return_value=True)
        self.mock_edit_visit_form.errors = {}

        # Mocking database queries
        self.mock_pet_query = MagicMock()
        self.mock_pet_query.get_or_404 = MagicMock(return_value=self.mock_pet)
        self.mock_visit_query = MagicMock()
        self.mock_visit_query.get_or_404 = MagicMock()
        self.mock_veterinarian_query = MagicMock()
        self.mock_veterinarian_query.all = MagicMock(return_value=[self.mock_veterinarian])

        # Patching modules
        patcher_db = patch('app.db', self.db)
        patcher_pet = patch('app.models.pet.Pet.query', self.mock_pet_query)
        patcher_visit = patch('app.models.visit.Visit.query', self.mock_visit_query)
        patcher_vet = patch('app.models.veterinarian.Veterinarian.query', self.mock_veterinarian_query)
        patcher_form = patch('app.forms.visit_form.VisitForm', return_value=self.mock_visit_form)
        patcher_edit_form = patch('app.forms.edit_visit_form.EditVisitForm', return_value=self.mock_edit_visit_form)
        patcher_payment = patch('services.payment_service.PaymentService', MagicMock(spec=PaymentService))
        patcher_flash = patch('flask.flash')
        patcher_redirect = patch('flask.redirect')
        patcher_url_for = patch('flask.url_for')
        patcher_abort = patch('flask.abort')
        patcher_current_user = patch('flask_login.current_user')
        patcher_login_required = patch('flask_login.login_required', return_value=lambda f: f)

        self.mock_current_user = MagicMock()
        self.mock_current_user.is_authenticated = True
        self.mock_current_user.id = 1

        self.mock_flash = patcher_flash.start()
        self.mock_redirect = patcher_redirect.start()
        self.mock_url_for = patcher_url_for.start()
        self.mock_abort = patcher_abort.start()
        self.mock_current_user_patch = patcher_current_user.start()
        self.mock_current_user_patch.return_value = self.mock_current_user

        self.addCleanup(patcher_db.stop)
        self.addCleanup(patcher_pet.stop)
        self.addCleanup(patcher_visit.stop)
        self.addCleanup(patcher_vet.stop)
        self.addCleanup(patcher_form.stop)
        self.addCleanup(patcher_edit_form.stop)
        self.addCleanup(patcher_payment.stop)
        self.addCleanup(patcher_flash.stop)
        self.addCleanup(patcher_redirect.stop)
        self.addCleanup(patcher_url_for.stop)
        self.addCleanup(patcher_abort.stop)
        self.addCleanup(patcher_login_required.stop)

        # Import the controller after patching
        from controllers.visit_controller import visit_bp as imported_visit_bp
        self.visit_bp = imported_visit_bp

    def test_new_visit_get(self):
        # Mock request context for GET
        mock_request = MagicMock()
        mock_request.method = 'GET'
        self.app.request = mock_request

        # Mock render_template
        with patch('flask.render_template', return_value='rendered_template') as mock_render:
            result = self.visit_bp.new_visit(pet_id=1)
            mock_render.assert_called_once_with('create_visit.html', form=self.mock_visit_form, pet=self.mock_pet)
            self.assertEqual(result, 'rendered_template')

    def test_new_visit_post_success(self):
        # Mock request context for POST
        mock_request = MagicMock()
        mock_request.method = 'POST'
        self.app.request = mock_request

        self.mock_visit_form.date.data = date.today() + timedelta(days=1)
        self.mock_visit_form.description.data = "Routine checkup"
        self.mock_visit_form.veterinarian.data = 1

        result = self.visit_bp.new_visit(pet_id=1)

        self.db.session.add.assert_called_once()
        self.db.session.commit.assert_called_once()
        self.mock_flash.assert_called_once_with('Visit added successfully!', 'success')
        self.mock_url_for.assert_called_once_with('owner.profile', owner_id=1)
        self.assertEqual(result, self.mock_redirect.return_value)

    def test_new_visit_post_future_date_error(self):
        # Mock request context for POST
        mock_request = MagicMock()
        mock_request.method = 'POST'
        self.app.request = mock_request

        self.mock_visit_form.date.data = date.today() + timedelta(days=1)
        self.mock_visit_form.validate_on_submit.return_value = True
        # Simulate future date validation failure in the controller
        with patch('app.forms.visit_form.VisitForm.validate_date') as mock_validate_date:
            mock_validate_date.side_effect = ValidationError("Date cannot be in the future.")
            result = self.visit_bp.new_visit(pet_id=1)

            self.mock_flash.assert_called_once_with('Visit date cannot be in the future.', 'danger')
            self.assertEqual(result, self.visit_bp.new_visit(pet_id=1))

    def test_edit_visit_get(self):
        mock_visit = MagicMock(spec=Visit)
        mock_visit.id = 1
        mock_visit.date = date.today()
        mock_visit.description = "Old description"
        mock_visit.veterinarian_id = 1
        mock_visit.veterinarian = self.mock_veterinarian
        self.mock_visit_query.get_or_404.return_value = mock_visit

        # Mock request context for GET
        mock_request = MagicMock()
        mock_request.method = 'GET'
        self.app.request = mock_request

        with patch('flask.render_template', return_value='rendered_template') as mock_render:
            result = self.visit_bp.edit_visit(visit_id=1)
            self.mock_edit_visit_form.description.data = mock_visit.description
            mock_render.assert_called_once_with('edit_visit.html', form=self.mock_edit_visit_form, visit=mock_visit, pet=self.mock_pet)
            self.assertEqual(result, 'rendered_template')

    def test_edit_visit_post_success(self):
        mock_visit = MagicMock(spec=Visit)
        mock_visit.id = 1
        mock_visit.date = date.today()
        mock_visit.description = "Old description"
        mock_visit.veterinarian_id = 1
        mock_visit.veterinarian = self.mock_veterinarian
        self.mock_visit_query.get_or_404.return_value = mock_visit

        # Mock request context for POST
        mock_request = MagicMock()
        mock_request.method = 'POST'
        self.app.request = mock_request

        self.mock_edit_visit_form.description.data = "Updated description"
        self.mock_edit_visit_form.validate_on_submit = MagicMock(return_value=True)

        result = self.visit_bp.edit_visit(visit_id=1)

        self.assertEqual(mock_visit.description, "Updated description")
        self.db.session.commit.assert_called_once()
        self.mock_flash.assert_called_once_with('Visit description updated successfully!', 'success')
        self.mock_url_for.assert_called_once_with('owner.profile', owner_id=1)
        self.assertEqual(result, self.mock_redirect.return_value)

    def test_delete_visit_success(self):
        mock_visit = MagicMock(spec=Visit)
        mock_visit.id = 1
        mock_visit.pet_id = 1
        self.mock_visit_query.get_or_404.side_effect = [mock_visit, self.mock_pet]

        result = self.visit_bp.delete_visit(visit_id=1)

        self.db.session.delete.assert_called_once_with(mock_visit)
        self.db.session.commit.assert_called_once()
        self.mock_flash.assert_called_once_with('Visit deleted successfully.', 'success')
        self.mock_url_for.assert_called_once_with('owner.profile', owner_id=1)
        self.assertEqual(result, self.mock_redirect.return_value)

    def test_process_visit_payment_success(self):
        mock_visit = MagicMock(spec=Visit)
        mock_visit.id = 1
        mock_visit.pet_id = 1
        self.mock_visit_query.get_or_404.side_effect = [mock_visit, self.mock_pet]

        # Mock PaymentService to succeed
        mock_payment_service = MagicMock(spec=PaymentService)
        mock_payment_service.process_payment.return_value = True
        with patch('services.payment_service.PaymentService', return_value=mock_payment_service):
            result = self.visit_bp.process_visit_payment(visit_id=1)

            mock_payment_service.process_payment.assert_called_once_with(visit_id=1, amount=100.00)
            self.mock_flash.assert_called_once_with('Payment processed successfully.', 'success')
            self.mock_url_for.assert_called_once_with('owner.profile', owner_id=1)
            self.assertEqual(result, self.mock_redirect.return_value)

    def test_process_visit_payment_failure(self):
        mock_visit = MagicMock(spec=Visit)
        mock_visit.id = 1
        mock_visit.pet_id = 1
        self.mock_visit_query.get_or_404.side_effect = [mock_visit, self.mock_pet]

        # Mock PaymentService to fail
        mock_payment_service = MagicMock(spec=PaymentService)
        mock_payment_service.process_payment.side_effect = Exception('Payment gateway error')
        with patch('services.payment_service.PaymentService', return_value=mock_payment_service):
            result = self.visit_bp.process_visit_payment(visit_id=1)

            mock_payment_service.process_payment.assert_called_once_with(visit_id=1, amount=100.00)
            self.mock_flash.assert_called_once_with('Payment failed: Payment gateway error', 'danger')
            self.mock_url_for.assert_called_once_with('owner.profile', owner_id=1)
            self.assertEqual(result, self.mock_redirect.return_value)


if __name__ == '__main__':
    unittest.main()
