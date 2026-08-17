import axios from 'axios';

const API_BASE_URL = '/api/visits';

export default {
  getAllVisits(filters = {}) {
    return axios.get(API_BASE_URL, { params: filters });
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