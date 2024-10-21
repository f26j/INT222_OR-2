<script setup>
import { useToastStore } from "@/stores/ToastStore.js";
import { storeToRefs } from "pinia";
import { ref } from "vue";

const toastStore = useToastStore();
const { toasts } = storeToRefs(toastStore);
</script>

<template>
  <div class="toast-container">
    <transition-group name="fade" tag="div">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="toast-message flex w-full p-4 mb-4 text-white rounded-lg shadow-lg"
        :class="toast.type === 'error' ? 'bg-red-500' : 'bg-green-500'"
        role="alert"
      >
        <!-- Icon -->
        <div class="icon">
          <svg
            v-if="toast.type === 'error'"
            class="w-7 h-7"
            aria-hidden="true"
            xmlns="http://www.w3.org/2000/svg"
            fill="white"
            viewBox="0 0 20 20"
          >
            <path
              d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5Zm3.707 11.793a1 1 0 1 1-1.414 1.414L10 11.414l-2.293 2.293a1 1 0 0 1-1.414-1.414L8.586 10 6.293 7.707a1 1 0 0 1 1.414-1.414L10 8.586l2.293-2.293a1 1 0 0 1 1.414 1.414L11.414 10l2.293 2.293Z"
            />
          </svg>
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            fill="white"
            width="2em"
            height="2em"
            viewBox="0 0 16 16"
          >
            <path
              d="M8 16A8 8 0 1 1 8 0a8 8 0 0 1 0 16m3.78-9.72a.75.75 0 0 0-.018-1.042a.75.75 0 0 0-1.042-.018L6.75 9.19L5.28 7.72a.75.75 0 0 0-1.042.018a.75.75 0 0 0-.018 1.042l2 2a.75.75 0 0 0 1.06 0Z"
            ></path>
          </svg>
        </div>

        <!-- Message -->
        <div class="ms-4 text-sm font-normal">
          <span class="font-semibold">{{
            toast.type === "error" ? "Error" : "Success"
          }}</span>
          <div>{{ toast.message }}</div>
        </div>

        <!-- Close Button -->
        <button
          @click="toastStore.removeToast(toast.id)"
          class="close-button"
          aria-label="Close"
        >
          ✖
        </button>

        <!-- Progress Bar -->
        <div class="progress" :key="toast.id"></div>
      </div>
    </transition-group>
  </div>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 80px;
  right: 30px;
  z-index: 9999;
}

.toast-message {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  width: 30rem; /* ปรับความกว้าง */
  padding: 15px;
  margin-bottom: 10px;
  position: relative;
}

.icon {
  display: flex;
  justify-content: center;
  align-items: center;
}

.close-button {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  margin-left: auto;
}

.progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 4px; /* ขนาดความสูงของ Progress Bar */
  width: 100%;
  background-color: rgba(255, 255, 255, 0.5);
  animation: progress 5s linear forwards;
}

@keyframes progress {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease-in-out;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
