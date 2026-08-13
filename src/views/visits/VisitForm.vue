<template>
  <div class="visit-form">
    <h2>Add Visit</h2>
    <form @submit.prevent="submitForm">
      <!-- form fields -->
      <button type="submit" class="btn btn-primary">Save Visit</button>
      <router-link to="/owners" class="cancel-link-plain">Cancel</router-link>
    </form>
  </div>
</template>

<script>
export default {
  name: 'VisitForm',
  // ...
}
</script>

<style lang="scss">
.visit-form {
  .cancel-link-plain {
    color: #6C757D;
    text-decoration: none;
    margin-left: 15px;
  }
}
</style>
