<script setup>
import { ref, watch } from "vue";

const props = defineProps({
  isOpen: Boolean,
  title: String,
  message: String,
});

const emit = defineEmits(["confirm", "close"]);

const closeModal = () => {
  emit("close");
};

const confirmAction = () => {
  emit("confirm");
};
</script>
<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50 z-50"
  >
    <div class="bg-white p-6 rounded-lg shadow-lg w-96">
      <h2 class="text-xl font-bold mb-4">{{ title }}</h2>
      <p class="text-gray-600 mb-4">{{ message }}</p>
      <div class="flex justify-end space-x-4">
        <button @click="closeModal" class="px-4 py-2 bg-gray-300 rounded-md">
          Cancel
        </button>
        <button
          @click="confirmAction"
          class="px-4 py-2 bg-green-500 text-white rounded-md"
        >
          Confirm
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Add modal styling as needed */
</style>
