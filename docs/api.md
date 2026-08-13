# API Documentation

## Veterinarian Management

### Get All Veterinarians

`GET /api/vets`

Retrieves a list of all veterinarians, optionally filtered by specialty.

#### Query Parameters

*   `specialty` (optional, string): The name of the specialty to filter veterinarians by. If omitted or "all", all veterinarians are returned.

#### Responses

*   `200 OK`: A list of veterinarian objects.
    ```json
    [
      {
        "id": 1,
        "firstName": "James",
        "lastName": "Carter",
        "specialties": [
          {"id": 101, "name": "Radiology"},
          {"id": 102, "name": "Surgery"}
        ]
      },
      {
        "id": 2,
        "firstName": "Helen",
        "lastName": "Leary",
        "specialties": [
          {"id": 103, "name": "Dentistry"}
        ]
      }
    ]
    ```

### Get All Distinct Specialties

`GET /api/specialties`

Retrieves a list of all distinct specialties currently associated with any veterinarian. This endpoint is primarily used for populating filter dropdowns in the UI.

#### Responses

*   `200 OK`: A list of specialty objects.
    ```json
    [
      {"id": 101, "name": "Radiology"},
      {"id": 102, "name": "Surgery"},
      {"id": 103, "name": "Dentistry"}
    ]
    ```

### Get Veterinarian by ID

`GET /api/vets/{id}`

Retrieves a single veterinarian by their ID.

#### Path Parameters

*   `id` (required, long): The ID of the veterinarian.

#### Responses

*   `200 OK`: A veterinarian object.
*   `404 Not Found`: If no veterinarian with the given ID exists.

### Create New Veterinarian

`POST /api/vets`

Creates a new veterinarian.

#### Request Body

A veterinarian object (ID should be null or omitted).

#### Responses

*   `201 Created`: The newly created veterinarian object.
*   `400 Bad Request`: If the request body is invalid.

### Update Existing Veterinarian

`PUT /api/vets/{id}`

Updates an existing veterinarian.

#### Path Parameters

*   `id` (required, long): The ID of the veterinarian to update.

#### Request Body

A veterinarian object with updated fields.

#### Responses

*   `200 OK`: The updated veterinarian object.
*   `404 Not Found`: If no veterinarian with the given ID exists.
*   `400 Bad Request`: If the request body is invalid.

### Delete Veterinarian

`DELETE /api/vets/{id}`

Deletes a veterinarian by their ID.

#### Path Parameters

*   `id` (required, long): The ID of the veterinarian to delete.

#### Responses

*   `204 No Content`: If the veterinarian was successfully deleted.
*   `404 Not Found`: If no veterinarian with the given ID exists.
