import axios from 'axios';

const API_BASE_URL = '/api/visits';

export default {
  getAllVisits() {
    return axios.get(API_BASE_URL);
  },

  getFilteredVisits(filters) {
    const params = {};
    if (filters.petId) params.petId = filters.petId;
    if (filters.ownerId) params.ownerId = filters.ownerId;
    if (filters.veterinarianId) params.veterinarianId = filters.veterinarianId;
    if (filters.startDate) params.startDate = filters.startDate;
    if (filters.endDate) params.endDate = filters.endDate;
    if (filters.descriptionKeyword) params.descriptionKeyword = filters.descriptionKeyword;

    return axios.get(`${API_BASE_URL}/filtered`, { params });
  },

  getVisitById(id) {
    return axios.get(`${API_BASE_URL}/${id}`);
  },

  createVisit(visit) {
    return axios.post(API_BASE_URL, visit);
  },

  updateVisit(id, visit) {
    return axios.put(`${API_BASE_URL}/${id}`, visit);
  },

  deleteVisit(id) {
    return axios.delete(`${API_BASE_URL}/${id}`);
  }
};