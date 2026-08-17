<template>
  <div class="appointment-list">
    <h2>Appointments</h2>
    <AppointmentFilter @apply-filters="handleApplyFilters" @clear-filters="handleClearFilters" />

    <div v-if="loading" class="text-center">Loading appointments...</div>
    <div v-else-if="error" class="alert alert-danger">Error loading appointments: {{ error.message }}</div>
    <div v-else-if="appointments.length === 0" class="alert alert-info">No appointments found matching your criteria.</div>
    <div v-else>
      <table class="table table-striped">
        <thead>
          <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Pet</th>
            <th>Owner</th>
            <th>Veterinarian</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="appointment in appointments" :key="appointment.id">
            <td>{{ appointment.date }}</td>
            <td>{{ appointment.description }}</td>
            <td>{{ appointment.pet ? appointment.pet.name : 'N/A' }}</td>
            <td>{{ appointment.pet && appointment.pet.owner ? appointment.pet.owner.firstName + ' ' + appointment.pet.owner.lastName : 'N/A' }}</td>
            <td>{{ appointment.vet ? appointment.vet.firstName + ' ' + appointment.vet.lastName : 'N/A' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import AppointmentFilter from '../components/AppointmentFilter.vue';
import appointmentApi from '../api/appointmentApi.js';

export default {
  name: 'AppointmentList',
  components: {
    AppointmentFilter
  },
  data() {
    return {
      appointments: [],
      loading: false,
      error: null,
      currentFilters: {}
    };
  },
  created() {
    this.fetchAppointments();
  },
  methods: {
    async fetchAppointments() {
      this.loading = true;
      this.error = null;
      try {
        this.appointments = await appointmentApi.getFilteredAppointments(this.currentFilters);
      } catch (err) {
        this.error = err;
      } finally {
        this.loading = false;
      }
    },
    handleApplyFilters(filters) {
      this.currentFilters = filters;
      this.fetchAppointments();
    },
    handleClearFilters() {
      this.currentFilters = {}; // Reset filters
      this.fetchAppointments();
    }
  }
};
</script>

<style scoped>
/* Add some basic styling if necessary */
</style>
