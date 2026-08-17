<template>
  <div class="container mt-4">
    <h2>Appointments</h2>

    <AppointmentFilter @apply-filters="handleApplyFilters" />

    <div v-if="loading" class="text-center">Loading appointments...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else>
      <div v-if="appointments.length === 0" class="alert alert-info">No appointments found.</div>
      <table v-else class="table table-striped">
        <thead>
          <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Pet</th>
            <th>Owner</th>
            <th>Veterinarian</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="appointment in appointments" :key="appointment.id">
            <td>{{ appointment.date }}</td>
            <td>{{ appointment.description }}</td>
            <td>{{ appointment.pet ? appointment.pet.name : 'N/A' }}</td>
            <td>{{ appointment.pet && appointment.pet.owner ? appointment.pet.owner.firstName + ' ' + appointment.pet.owner.lastName : 'N/A' }}</td>
            <td>{{ appointment.veterinarian ? appointment.veterinarian.name : 'N/A' }}</td>
            <td>
              <button @click="editAppointment(appointment.id)" class="btn btn-sm btn-primary me-2">Edit</button>
              <button @click="deleteAppointment(appointment.id)" class="btn btn-sm btn-danger">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal for editing/creating appointments (assuming it exists) -->
    <div class="modal fade" id="appointmentModal" tabindex="-1" aria-labelledby="appointmentModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="appointmentModalLabel">{{ currentAppointment.id ? 'Edit Appointment' : 'Add New Appointment' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveAppointment">
              <div class="mb-3">
                <label for="appointmentDate" class="form-label">Date</label>
                <input type="date" class="form-control" id="appointmentDate" v-model="currentAppointment.date" required>
              </div>
              <div class="mb-3">
                <label for="appointmentDescription" class="form-label">Description</label>
                <input type="text" class="form-control" id="appointmentDescription" v-model="currentAppointment.description" required>
              </div>
              <div class="mb-3">
                <label for="appointmentPet" class="form-label">Pet</label>
                <select class="form-select" id="appointmentPet" v-model="currentAppointment.pet.id" required>
                  <option v-for="pet in allPets" :key="pet.id" :value="pet.id">{{ pet.name }} (Owner: {{ pet.owner.firstName }} {{ pet.owner.lastName }})</option>
                </select>
              </div>
              <div class="mb-3">
                <label for="appointmentVeterinarian" class="form-label">Veterinarian</label>
                <select class="form-select" id="appointmentVeterinarian" v-model="currentAppointment.veterinarian.id" required>
                  <option v-for="vet in allVeterinarians" :key="vet.id" :value="vet.id">{{ vet.name }}</option>
                </select>
              </div>
              <button type="submit" class="btn btn-primary">Save changes</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <button @click="addNewAppointment" class="btn btn-success mt-3">Add New Appointment</button>
  </div>
</template>

<script>
import { fetchVisits, createVisit, updateVisit, deleteVisit, fetchPets, fetchVeterinarians } from '../api/appointmentApi';
import AppointmentFilter from '../components/AppointmentFilter.vue';
import { Modal } from 'bootstrap';

export default {
  name: 'AppointmentList',
  components: {
    AppointmentFilter,
  },
  data() {
    return {
      appointments: [],
      allPets: [],
      allVeterinarians: [],
      loading: false,
      error: null,
      currentAppointment: {
        id: null,
        date: '',
        description: '',
        pet: { id: null },
        veterinarian: { id: null },
      },
      activeFilters: {},
    };
  },
  async created() {
    await this.loadAppointments();
    await this.loadModalOptions();
  },
  methods: {
    async loadAppointments() {
      this.loading = true;
      this.error = null;
      try {
        this.appointments = await fetchVisits(this.activeFilters);
      } catch (error) {
        this.error = 'Failed to load appointments: ' + error.message;
        console.error('Error loading appointments:', error);
      } finally {
        this.loading = false;
      }
    },
    async loadModalOptions() {
      try {
        this.allPets = await fetchPets();
        this.allVeterinarians = await fetchVeterinarians();
      } catch (error) {
        console.error('Error loading modal options:', error);
      }
    },
    handleApplyFilters(filters) {
      this.activeFilters = filters;
      this.loadAppointments();
    },
    addNewAppointment() {
      this.currentAppointment = {
        id: null,
        date: '',
        description: '',
        pet: { id: null },
        veterinarian: { id: null },
      };
      const modal = new Modal(document.getElementById('appointmentModal'));
      modal.show();
    },
    editAppointment(id) {
      const appointment = this.appointments.find(app => app.id === id);
      if (appointment) {
        this.currentAppointment = {
          ...appointment,
          date: appointment.date,
          pet: { id: appointment.pet ? appointment.pet.id : null },
          veterinarian: { id: appointment.veterinarian ? appointment.veterinarian.id : null },
        };
        const modal = new Modal(document.getElementById('appointmentModal'));
        modal.show();
      }
    },
    async saveAppointment() {
      try {
        const visitToSave = {
          ...this.currentAppointment,
          pet: this.currentAppointment.pet.id ? { id: this.currentAppointment.pet.id } : null,
          veterinarian: this.currentAppointment.veterinarian.id ? { id: this.currentAppointment.veterinarian.id } : null,
        };

        if (this.currentAppointment.id) {
          await updateVisit(this.currentAppointment.id, visitToSave);
        } else {
          await createVisit(visitToSave);
        }
        const modal = Modal.getInstance(document.getElementById('appointmentModal'));
        modal.hide();
        await this.loadAppointments();
      } catch (error) {
        console.error('Error saving appointment:', error);
        this.error = 'Failed to save appointment: ' + error.message;
      }
    },
    async deleteAppointment(id) {
      if (confirm('Are you sure you want to delete this appointment?')) {
        try {
          await deleteVisit(id);
          await this.loadAppointments();
        } catch (error) {
          console.error('Error deleting appointment:', error);
          this.error = 'Failed to delete appointment: ' + error.message;
        }
      }
    },
  },
};
</script>

<style scoped>
/* Add any specific styles for AppointmentList here */
</style>