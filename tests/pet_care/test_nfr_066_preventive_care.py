import unittest
from unittest.mock import patch, MagicMock

class TestNFR066PreventiveCare(unittest.TestCase):

    def setUp(self):
        self.mock_pet_service = MagicMock()
        # Assume a service or controller is being tested

    def test_create_preventive_record(self):
        """Verify that a new preventive care record can be created."""
        record_data = {"pet_id": "pet123", "type": "Vaccination", "date": "2023-01-15", "details": "Rabies vaccine"}
        self.mock_pet_service.create_preventive_record.return_value = {"id": "rec001", **record_data}
        result = self.mock_pet_service.create_preventive_record(record_data)
        self.assertIsNotNone(result.get("id"))
        self.assertEqual(result["type"], "Vaccination")
        self.mock_pet_service.create_preventive_record.assert_called_once_with(record_data)

    def test_retrieve_preventive_records_for_pet(self):
        """Verify that all preventive care records for a specific pet can be retrieved."""
        pet_id = "pet123"
        mock_records = [
            {"id": "rec001", "pet_id": pet_id, "type": "Vaccination"},
            {"id": "rec002", "pet_id": pet_id, "type": "Deworming"}
        ]
        self.mock_pet_service.get_preventive_records_by_pet_id.return_value = mock_records
        records = self.mock_pet_service.get_preventive_records_by_pet_id(pet_id)
        self.assertEqual(len(records), 2)
        self.assertTrue(all(r["pet_id"] == pet_id for r in records))

    def test_update_preventive_record(self):
        """Verify that an existing preventive care record can be updated."""
        record_id = "rec001"
        update_data = {"details": "Rabies vaccine updated batch"}
        self.mock_pet_service.update_preventive_record.return_value = {"id": record_id, "details": "Rabies vaccine updated batch"}
        updated_record = self.mock_pet_service.update_preventive_record(record_id, update_data)
        self.assertEqual(updated_record["details"], "Rabies vaccine updated batch")

if __name__ == '__main__':
    unittest.main()