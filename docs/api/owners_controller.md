# OwnersController API

## Search Owners

**Endpoint:** `GET /owners`
**Description:** Retrieves a list of owners based on search criteria.
**Parameters:**
*   `lastName` (string, optional): Filter by owner's last name.
**Return Type:** paginated Page<Owner>