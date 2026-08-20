import unittest
from unittest.mock import patch, MagicMock

class TestNFR067OwnerRecords(unittest.TestCase):

    def setUp(self):
        self.mock_owner_service = MagicMock()
        # Assume an owner search service or controller is being tested

    def test_search_owner_by_name(self):
        """Verify that owners can be searched by name."""
        search_term = "John Doe"
        mock_owners = [{"id": "owner1", "name": "John Doe", "email": "john@example.com"}]
        self.mock_owner_service.search_owners.return_value = mock_owners
        results = self.mock_owner_service.search_owners(name=search_term)
        self.assertEqual(len(results), 1)
        self.assertEqual(results[0]["name"], search_term)
        self.mock_owner_service.search_owners.assert_called_once_with(name=search_term)

    def test_retrieve_owner_details(self):
        """Verify that detailed owner information can be retrieved."""
        owner_id = "owner1"
        mock_owner_details = {"id": owner_id, "name": "John Doe", "address": "123 Main St"}
        self.mock_owner_service.get_owner_details.return_value = mock_owner_details
        details = self.mock_owner_service.get_owner_details(owner_id)
        self.assertEqual(details["id"], owner_id)
        self.assertIn("address", details)

    def test_search_owner_by_pet_name(self):
        """Verify that owners can be searched by their pet's name."""
        pet_name = "Buddy"
        mock_owners = [{"id": "owner2", "name": "Jane Smith", "pets": ["Buddy"]}]
        self.mock_owner_service.search_owners_by_pet_name.return_value = mock_owners
        results = self.mock_owner_service.search_owners_by_pet_name(pet_name)
        self.assertEqual(len(results), 1)
        self.assertEqual(results[0]["name"], "Jane Smith")

if __name__ == '__main__':
    unittest.main()