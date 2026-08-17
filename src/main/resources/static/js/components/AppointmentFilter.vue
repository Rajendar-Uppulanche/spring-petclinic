<template>
  <div class="appointment-filter card p-3 mb-3">
    <h5 class="card-title">Filter Appointments</h5>
    <form @submit.prevent="applyFilters">
      <div class="row g-3">
        <div class="col-md-4">
          <label for="petFilter" class="form-label">Pet</label>
          <select id="petFilter" class="form-select" v-model="filters.petId">
            <option :value="null">All Pets</option>
            <option v-for="pet in pets" :key="pet.id" :value="pet.id">{{ pet.name }}</option>
          </select>
        </div>
        <div class="col-md-4">
          <label for="ownerFilter" class="form-label">Owner</label>
          <select id="ownerFilter" class="form-select" v-model="filters.ownerId">
            <option :value="null">All Owners</option>
            <option v-for="owner in owners" :key="owner.id" :value="owner.id">{{ owner.firstName }} {{ owner.lastName }}</option>
          </select>
        </div>
        <div class="col-md-4">
          <label for="veterinarianFilter" class="form-label">Veterinarian</label>
          <select id="veterinarianFilter" class="form-select" v-model="filters.veterinarianId">
            <option :value="null">All Veterinarians</option>
            <option v-for="vet in veterinarians" :key="vet.id" :value="vet.id">{{ vet.name }}</option>
          </select>
        </div>
        <div class="col-md-4">
          <label for="startDateFilter" class="form-label">Start Date</label>
          <input type="date" id="startDateFilter" class="form-control" v-model="filters.startDate">
        </div>
        <div class="col-md-4">
          <label for="endDateFilter" class="form-label">End Date</label>
          <input type="date" id="endDateFilter" class="form-control" v-model="filters.endDate">
        </div>
        <div class="col-md-4">
          <label for="descriptionFilter" class="form-label">Description Keyword</label>
          <input type="text" id="descriptionFilter" class="form-control" v-model="filters.description" placeholder="e.g., 'checkup'">
        </div>
      </div>
      <div class="mt-3 d-flex justify-content-end">
        <button type="button" class="btn btn-secondary me-2" @click="clearFilters">Clear Filters</button>
        <button type="submit" class="btn btn-primary">Apply Filters</button>
      </div>
    </form>
  </div>
</template>

<script>
import { fetchPets, fetchOwners, fetchVeterinarians } from '../api/appointmentApi';

export default {
  name: 'AppointmentFilter',
  data() {
    return {
      filters: {
        petId: null,
        ownerId: null,
        veterinarianId: null,
        startDate: null,
        endDate: null,
        description: null,
      },
      pets: [],
      owners: [],
      veterinarians: [],
    };
  },
  async created() {
    await this.loadFilterOptions();
  },
  methods: {
    async loadFilterOptions() {
      try {
        this.pets = await fetchPets();
        this.owners = await fetchOwners();
        this.veterinarians = await fetchVeterinarians();
      } catch (error) {
        console.error('Error loading filter options:', error);
        // Handle error, e.g., show a message to the user
      }
    },
    applyFilters() {
      this.$emit('apply-filters', this.filters);
    },
    clearFilters() {
      this.filters = {
        petId: null,
        ownerId: null,
        veterinarianId: null,
        startDate: null,
        endDate: null,
        description: null,
      };
      this.$emit('apply-filters', this.filters);
    },
  },
};
</script>

<style scoped>
.appointment-filter {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 0.25rem;
}
</style>