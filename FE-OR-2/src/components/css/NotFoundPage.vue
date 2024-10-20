<script setup>
</script>

<template>
  <div class="not-found-page h-screen flex flex-col justify-center items-center p-2 text-gray-600">
    <div>
      <svg xmlns="http://www.w3.org/2000/svg" width="60" height="60" viewBox="0 0 20 20">
        <path fill="currentColor" fill-rule="evenodd" d="M8 4a4 4 0 1 0 0 8a4 4 0 0 0 0-8M2 8a6 6 0 1 1 10.89 3.476l4.817 4.817a1 1 0 0 1-1.414 1.414l-4.816-4.816A6 6 0 0 1 2 8" clip-rule="evenodd"/>
      </svg>
    </div>
    <div class="text-center font-bold pt-4">
      <h1 class="text-6xl">404</h1>
      <h1 class="text-3xl">Page Not Found</h1>
    </div>
    <p class="text-xl p-6">The page you are looking for is not available.</p>
    <router-link to="/" class="text-2xl text-blue-500 hover:text-blue-600 hover:scale-105 transition-transform duration-300 ease-in-out">Go back to Homepage</router-link>
  </div>
</template>

<style scoped></style>
