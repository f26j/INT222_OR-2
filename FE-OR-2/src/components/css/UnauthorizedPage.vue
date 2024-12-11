<script setup>
</script>

<template>
  <div class="not-found-page h-screen flex flex-col justify-center items-center p-2 text-gray-600">
    <div>
      <svg xmlns="http://www.w3.org/2000/svg" width="60" height="60" viewBox="0 0 24 24">
        <path fill="currentColor" d="M12 2C6.49 2 2 6.49 2 12s4.49 10 10 10 10-4.49 10-10S17.51 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"/><path fill="currentColor" d="M12 7c-1.1 0-2 .9-2 2h2v5h2v-5h2c0-1.1-.9-2-2-2zm-1 10h2v-2h-2z"/>
      </svg>
    </div>
    <div class="text-center font-bold pt-4">
      <h1 class="text-6xl">401</h1>
      <h1 class="text-3xl">Unauthorized</h1>
    </div>
    <p class="text-xl p-6">You are not authorized to access this page. Please log in or contact support.</p>
    <router-link to="/login" class="text-2xl text-blue-500 hover:text-blue-600 hover:scale-105 transition-transform duration-300 ease-in-out">Go to Login Page</router-link>
  </div>
</template>

<style scoped></style>