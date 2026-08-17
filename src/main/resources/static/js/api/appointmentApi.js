// Assuming this file exists and handles API calls for appointments/visits

const API_BASE_URL = '/api/visits';

export const fetchVisits = async (filters = {}) => {
    const params = new URLSearchParams();
    if (filters.petId) params.append('petId', filters.petId);
    if (filters.ownerId) params.append('ownerId', filters.ownerId);
    if (filters.veterinarianId) params.append('veterinarianId', filters.veterinarianId);
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);
    if (filters.description) params.append('description', filters.description);

    const url = `${API_BASE_URL}?${params.toString()}`;
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
};

export const fetchPets = async () => {
    const response = await fetch(`${API_BASE_URL}/pets`);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
};

export const fetchOwners = async () => {
    const response = await fetch(`${API_BASE_URL}/owners`);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
};

export const fetchVeterinarians = async () => {
    const response = await fetch(`${API_BASE_URL}/veterinarians`);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
};

// Other existing API calls for visits (create, update, delete, etc.) would remain here.
// For brevity, only showing the relevant changes.
export const createVisit = async (visit) => {
    const response = await fetch(API_BASE_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(visit),
    });
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
};

export const updateVisit = async (id, visit) => {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(visit),
    });
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
};

export const deleteVisit = async (id) => {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: 'DELETE',
    });
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
};
