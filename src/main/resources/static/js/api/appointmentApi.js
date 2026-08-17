// src/main/resources/static/js/api/appointmentApi.js

const API_BASE_URL = '/api';

export async function fetchVisits(filters = {}) {
  const params = new URLSearchParams();
  if (filters.startDate) params.append('startDate', filters.startDate);
  if (filters.endDate) params.append('endDate', filters.endDate);
  if (filters.petId) params.append('petId', filters.petId);
  if (filters.ownerId) params.append('ownerId', filters.ownerId);
  if (filters.veterinarianId) params.append('veterinarianId', filters.veterinarianId);
  if (filters.descriptionKeyword) params.append('descriptionKeyword', filters.descriptionKeyword);

  const url = `${API_BASE_URL}/visits?${params.toString()}`;

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  return await response.json();
}
