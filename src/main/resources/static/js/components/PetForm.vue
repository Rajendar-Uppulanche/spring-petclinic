<template>
  <form @submit.prevent="submitForm">
    <div>
      <label for="name">Pet Name:</label>
      <input type="text" id="name" v-model="pet.name" />
      <span v-if="errors.name" class="error">{{ errors.name }}</span>
    </div>
    <div>
      <label for="birthDate">Birth Date:</label>
      <input type="date" id="birthDate" v-model="pet.birthDate" />
      <!-- This is where the new error message for birthDate would be displayed -->
      <span v-if="errors.birthDate" class="error">{{ errors.birthDate }}</span>
    </div>
    <button type="submit">Save Pet</button>
  </form>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      pet: {
        name: '',
        birthDate: ''
      },
      errors: {}
    };
  },
  methods: {
    async submitForm() {
      this.errors = {}; // Clear previous errors
      try {
        // Assuming an API endpoint for saving pets
        const response = await axios.post('/api/pets', this.pet);
        console.log('Pet saved:', response.data);
        // Handle success, e.g., redirect or clear form
      } catch (error) {
        if (error.response && error.response.status === 400) {
          // Assuming the backend returns validation errors in a specific format
          // e.g., { "name": "Pet name cannot be empty.", "birthDate": "Pet birth date cannot be in the future." }
          this.errors = error.response.data;
          console.error('Validation errors:', this.errors);
        } else {
          console.error('Error saving pet:', error);
        }
      }
    }
  }
};
</script>

<style scoped>
.error {
  color: red;
  font-size: 0.8em;
}
</style>