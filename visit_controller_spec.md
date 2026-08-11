## Visit Controller Specification

### Version

Specification Version: 0.0.52
Specification Guidelines Version: v0.0.52
Review & Acceptance Checklist Version: v0.0.52

### Functional Requirements

*   **FR-001:** The system MUST allow owners to add visits to their pets.
*   **FR-002:** The system MUST display a list of visits for a given pet.
*   **FR-003:** The system MUST allow editing of visit details.
*   **FR-004:** The system MUST allow deletion of visits.
*   **FR-005:** The system MUST validate visit dates to ensure they are in the future.
*   **FR-006:** The system MUST provide a form for adding new visits.
*   **FR-007:** The system MUST associate visits with a specific pet and owner.
*   **FR-008:** The system MUST display a success message upon successful visit booking.
*   **FR-009:** The system MUST display an error message if visit booking fails due to validation errors.
*   **FR-010:** The system MUST handle invalid ownerId or petId by displaying an appropriate error message.

### Scenarios

#### Scenario 1: Add a New Visit

1.  An owner navigates to the "Add Visit" page for one of their pets.
2.  The owner fills in the visit details (date, description).
3.  The owner submits the form.
4.  The system validates the input.
5.  If valid, the system saves the visit and redirects to the owner's details page with a success message.
6.  If invalid, the system displays an error message on the visit form.

#### Scenario 2: View Visits for a Pet

1.  An owner navigates to their details page.
2.  The owner clicks on a pet's name.
3.  The system displays the pet's details, including a list of their visits.

#### Scenario 3: Error Handling - Pet Not Found

1.  A user attempts to access the "Add Visit" page for a `petId` that does not exist for the given `ownerId`.
2.  The system should detect that the `petId` is invalid for the specified `ownerId`.
3.  The system should return an appropriate error message (e.g., "Pet with id [petId] not found for owner with id [ownerId].") and prevent further action.

### Methodology

This specification is developed using a behavior-driven development (BDD) approach, focusing on clear, concise descriptions of functional requirements and user scenarios. The specification version is 0.0.52.
