<template>
  <div class="container-fluid">
    <h2 class="text-center">Appointments</h2>

    <AppointmentFilter @apply-filters="handleApplyFilters" @clear-filters="handleClearFilters" />

    <div class="row">
      <div class="col-md-12">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>ID</th>
              <th>Date</th>
              <th>Description</th>
              <th>Pet Name</th>
              <th>Owner Name</th>
              <th>Veterinarian</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="visit in visits" :key="visit.id">
              <td>{{ visit.id }}</td>
              <td>{{ visit.visitDate }}</td>
              <td>{{ visit.description }}</td>
              <td>{{ visit.pet ? visit.pet.name : 'N/A' }}</td>
              <td>{{ visit.pet && visit.pet.owner ? visit.pet.owner.firstName + ' ' + visit.pet.owner.lastName : 'N/A' }}</td>
              <td>{{ visit.veterinarian ? visit.veterinarian.name : 'N/A' }}</td>
              <td>
                <router-link :to="{ name: 'VisitDetails', params: { id: visit.id }}" class="btn btn-info btn-sm me-2">View</router-link>
                <router-link :to="{ name: 'EditVisit', params: { id: visit.id }}" class="btn btn-warning btn-sm me-2">Edit</router-link>
                <button @click="deleteVisit(visit.id)" class="btn btn-danger btn-sm">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import appointmentApi from '../api/appointmentApi';
import AppointmentFilter from '../components/AppointmentFilter.vue';

export default {
  name: 'AppointmentList',
  components: {
    AppointmentFilter
  },
  data() {
    return {
      visits: [],
      currentFilters: {}
    };
  },
  created() {
    this.fetchVisits();
  },
  methods: {
    fetchVisits() {
      appointmentApi.getAllVisits(this.currentFilters)
        .then(response => {
          this.visits = response.data;
        })
        .catch(error => {
          console.error("Error fetching visits:", error);
        });
    },
    deleteVisit(id) {
      if (confirm('Are you sure you want to delete this visit?')) {
        appointmentApi.deleteVisit(id)
          .then(() => {
            this.fetchVisits();
          })
          .catch(error => {
            console.error("Error deleting visit:", error);
          });
      }
    },
    handleApplyFilters(filters) {
      const cleanedFilters = Object.fromEntries(
        Object.entries(filters).map(([key, value]) => [key, value === '' ? null : value])
      );
      this.currentFilters = cleanedFilters;
      this.fetchVisits();
    },
    handleClearFilters() {
      this.currentFilters = {};
      this.fetchVisits();
    }
  }
};
</script>

<style scoped>
/* Add any specific styles for AppointmentList here */
</style>