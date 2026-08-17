<template>
  <div class="appointment-filter card p-3 mb-3">
    <h5 class="card-title">Filter Appointments</h5>
    <form @submit.prevent="applyFilters">
      <div class="row g-3">
        <div class="col-md-6 col-lg-3">
          <label for="petId" class="form-label">Pet ID</label>
          <input type="number" class="form-control" id="petId" v-model.number="filters.petId">
        </div>
        <div class="col-md-6 col-lg-3">
          <label for="ownerId" class="form-label">Owner ID</label>
          <input type="number" class="form-control" id="ownerId" v-model.number="filters.ownerId">
        </div>
        <div class="col-md-6 col-lg-3">
          <label for="veterinarianId" class="form-label">Veterinarian ID</label>
          <input type="number" class="form-control" id="veterinarianId" v-model.number="filters.veterinarianId">
        </div>
        <div class="col-md-6 col-lg-3">
          <label for="descriptionKeyword" class="form-label">Description Keyword</label>
          <input type="text" class="form-control" id="descriptionKeyword" v-model="filters.descriptionKeyword">
        </div>
        <div class="col-md-6 col-lg-3">
          <label for="startDate" class="form-label">Start Date</label>
          <input type="date" class="form-control" id="startDate" v-model="filters.startDate">
        </div>
        <div class="col-md-6 col-lg-3">
          <label for="endDate" class="form-label">End Date</label>
          <input type="date" class="form-control" id="endDate" v-model="filters.endDate">
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
        descriptionKeyword: null,
      }
    };
  },
  methods: {
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
        descriptionKeyword: null,
      };
      this.$emit('clear-filters');
    }
  }
};
</script>

<style scoped>
.appointment-filter {
  background-color: #f8f9fa;
  border: 1px solid #e0e0e0;
  border-radius: 0.25rem;
}
</style>