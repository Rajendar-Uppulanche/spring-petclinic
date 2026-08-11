## Visit Controller Specification

**Specification Version:** 0.0.52

**Specification Guidelines:**
*   [GitHub Spec-Kit](https://github.com/spring-petclinic/spec-kit/blob/main/README.md) (Version 0.0.52)

**Review & Acceptance Checklist:**
*   [ ] All functional requirements are met.
*   [ ] All non-functional requirements are met.
*   [ ] All scenarios are covered.
*   [ ] All tests are passing.
*   [ ] Code is reviewed and approved.

**Methodology description:**
This specification is developed using the [GitHub Spec-Kit](https://github.com/spring-petclinic/spec-kit) methodology, version 0.0.52.

---

### Functional Requirements

#### FR-010: Error Handling for Invalid IDs

*   **Description:** The `VisitController` must validate `ownerId` and `petId` to ensure they are valid. If an invalid ID is provided, the controller should return an appropriate error message and halt processing.
*   **Status:** Implemented

---

### Scenarios

#### Scenario 1: Successful Visit Creation

*   **Description:** A user successfully creates a new visit for an existing pet.
*   **Steps:**
    1.  Navigate to the new visit form for a specific owner and pet.
    2.  Fill in the visit details (date, description).
    3.  Submit the form.
*   **Expected Outcome:** The visit is created, and the user is redirected to the owner's details page with a success message.

#### Scenario 2: Visit Creation with Validation Errors

*   **Description:** A user attempts to create a visit with invalid data (e.g., past date).
*   **Steps:**
    1.  Navigate to the new visit form.
    2.  Submit the form with invalid data.
*   **Expected Outcome:** The form is redisplayed with validation errors, and no visit is created.

#### Scenario 3: Error Handling - Invalid Owner or Pet ID

*   **Description:** A user attempts to create a visit with an invalid `ownerId` or `petId`.
*   **Steps:**
    1.  Attempt to access the new visit form with a non-existent `ownerId` or `petId`.
    2.  Alternatively, attempt to submit a visit form with an invalid `ownerId` or `petId`.
*   **Expected Outcome:** The controller should detect the invalid ID and return an appropriate error message (e.g., "Owner not found", "Pet not found") and halt processing. The user should not be able to proceed with creating a visit.

---

### API Endpoints

*   `GET /owners/{ownerId}/pets/{petId}/visits/new`
*   `POST /owners/{ownerId}/pets/{petId}/visits/new`

---

### Data Models

*   `Owner`
*   `Pet`
*   `Visit`

---

### Acceptance Criteria

*   [ ] The `VisitController` correctly handles requests for creating new visits.
*   [ ] Validation errors for visit data are displayed to the user.
*   [ ] FR-010 is implemented: Invalid `ownerId` or `petId` result in appropriate error responses.
