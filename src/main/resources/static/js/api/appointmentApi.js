const API_BASE_URL = '/api/visits';

async function getFilteredAppointments(filters = {}) {
  const params = new URLSearchParams();
  for (const key in filters) {
    if (filters[key] !== null && filters[key] !== undefined && filters[key] !== '') {
      params.append(key, filters[key]);
    }
  }

  const url = `${API_BASE_URL}?${params.toString()}`;
  const response = await fetch(url);

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to fetch appointments');
  }

  return response.json();
}

// Export other API functions as needed (e.g., create, update, delete)
// export async function createAppointment(appointmentData) { ... }

export default {
  getFilteredAppointments,
};
