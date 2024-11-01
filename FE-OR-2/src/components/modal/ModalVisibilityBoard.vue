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
    class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
  >
    <div class="itbkk-modal-alert bg-white p-6 rounded-3xl text-center shadow-xlp-6 w-1/3">
      <h2 class="text-xl font-bold mb-4">{{ title }}</h2>
      <p class="itbkk-message text-gray-600 mb-4">{{ message }}</p>
      <div class="flex justify-center space-x-4">
        <button
          @click="confirmAction"
          class="itbkk-button-confirm bg-emerald-500 hover:bg-emerald-600 text-white px-4 py-2"
        >
          Confirm
        </button>
        <button @click="closeModal" class="itbkk-button-cancel bg-gray-300 hover:bg-gray-400 text-gray-600 px-4 py-2 ml-4">
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
button {
  border-radius: 20px;
  transition: transform 0.3s ease-in-out;
}

button:hover {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  box-shadow: 5px 5px 5px #c6c6c646; 
}

button:hover:not(:disabled) {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  box-shadow: 5px 5px 5px #c6c6c646;
}
</style>
