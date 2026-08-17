<template>
  <div class="appointment-list container mt-4">
    <h2>Appointments</h2>

    <AppointmentFilter @apply-filters="fetchAppointments" @clear-filters="clearAndFetchAppointments" />

    <div v-if="loading" class="text-center">Loading appointments...</div>
    <div v-else-if="error" class="alert alert-danger">Error: {{ error }}</div>
    <div v-else-if="visits.length === 0" class="alert alert-info">No appointments found.</div>
    <div v-else>
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Date</th>
            <th>Description</th>
            <th>Pet</th>
            <th>Owner</th>
            <th>Veterinarian</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="visit in visits" :key="visit.id">
            <td>{{ visit.id }}</td>
            <td>{{ visit.visitDate }}</td>
            <td>{{ visit.description }}</td>
            <td>{{ visit.pet ? visit.pet.name : 'N/A' }}</td>
            <td>{{ visit.pet && visit.pet.owner ? visit.pet.owner.firstName + ' ' + visit.pet.owner.lastName : 'N/A' }}</td>
            <td>{{ visit.veterinarian ? visit.veterinarian.firstName + ' ' + visit.veterinarian.lastName : 'N/A' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import AppointmentFilter from '../components/AppointmentFilter.vue';
import { fetchVisits } from '../api/appointmentApi';

export default {
  name: 'AppointmentList',
  components: {
    AppointmentFilter
  },
  data() {
    return {
      visits: [],
      loading: false,
      error: null,
      currentFilters: {}
    };
  },
  created() {
    this.fetchAppointments({});
  },
  methods: {
    async fetchAppointments(filters) {
      this.loading = true;
      this.error = null;
      this.currentFilters = filters;
      try {
        this.visits = await fetchVisits(filters);
      } catch (err) {
        this.error = err.message || 'Failed to fetch appointments.';
        console.error('Error fetching appointments:', err);
      } finally {
        this.loading = false;
      }
    },
    clearAndFetchAppointments() {
      this.fetchAppointments({});
    }
  }
};
</script>

<style scoped>
/* Add some basic styling if needed */
</style>
