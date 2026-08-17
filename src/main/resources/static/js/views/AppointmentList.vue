<template>
  <div class="container-fluid">
    <h2 class="text-center">Appointments</h2>
    <AppointmentFilter @apply-filters="fetchFilteredAppointments" @clear-filters="fetchAllAppointments" />

    <div v-if="loading" class="text-center">Loading appointments...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else-if="appointments.length === 0" class="alert alert-info">No appointments found.</div>
    <div v-else>
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
          <tr v-for="appointment in appointments" :key="appointment.id">
            <td>{{ appointment.id }}</td>
            <td>{{ appointment.date }}</td>
            <td>{{ appointment.description }}</td>
            <td>{{ appointment.pet ? appointment.pet.name : 'N/A' }}</td>
            <td>{{ appointment.pet && appointment.pet.owner ? appointment.pet.owner.firstName + ' ' + appointment.pet.owner.lastName : 'N/A' }}</td>
            <td>{{ appointment.veterinarian ? appointment.veterinarian.name : 'N/A' }}</td>
            <td>
              <router-link :to="{ name: 'AppointmentDetails', params: { id: appointment.id } }" class="btn btn-info btn-sm me-2">View</router-link>
              <button @click="deleteAppointment(appointment.id)" class="btn btn-danger btn-sm">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import AppointmentFilter from '../components/AppointmentFilter.vue';
import appointmentApi from '../api/appointmentApi';

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
    };
  },
  created() {
    this.fetchAllAppointments();
  },
  methods: {
    async fetchAllAppointments() {
      this.loading = true;
      this.error = null;
      try {
        const response = await appointmentApi.getAllVisits();
        this.appointments = response.data;
      } catch (err) {
        console.error("Error fetching all appointments:", err);
        this.error = "Failed to load appointments.";
        this.appointments = [];
      } finally {
        this.loading = false;
      }
    },
    async fetchFilteredAppointments(filters) {
      this.loading = true;
      this.error = null;
      try {
        const response = await appointmentApi.getFilteredVisits(filters);
        this.appointments = response.data;
      } catch (err) {
        console.error("Error fetching filtered appointments:", err);
        this.error = "Failed to load filtered appointments.";
        this.appointments = [];
      } finally {
        this.loading = false;
      }
    },
    async deleteAppointment(id) {
      if (confirm('Are you sure you want to delete this appointment?')) {
        try {
          await appointmentApi.deleteVisit(id);
          this.appointments = this.appointments.filter(app => app.id !== id);
        } catch (err) {
          console.error("Error deleting appointment:", err);
          alert("Failed to delete appointment.");
        }
      }
    }
  }
};
</script>

<style scoped>
/* Add specific styles for this view if necessary */
</style>