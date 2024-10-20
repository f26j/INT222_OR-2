<script setup>
import { useToastStore } from "@/stores/ToastStore.js";
import { storeToRefs } from "pinia";

const toastStore = useToastStore();
const { toasts } = storeToRefs(toastStore);
</script>

<template>
  <div class="toast-container">
    <transition-group name="fade" tag="div">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        :class="['toast', toast.type]"
        role="alert"
      >
        <div class="icon">
          <svg
            v-if="toast.type === 'error'"
            class="w-7 h-7"
            xmlns="http://www.w3.org/2000/svg"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path
              d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5Zm3.707 11.793a1 1 0 1 1-1.414 1.414L10 11.414l-2.293 2.293a1 1 0 0 1-1.414-1.414L8.586 10 6.293 7.707a1 1 0 0 1 1.414-1.414L10 8.586l2.293-2.293a1 1 0 0 1 1.414 1.414L11.414 10l2.293 2.293Z"
            />
          </svg>
        </div>
        <div class="message">
          <span class="font-semibold">{{ toast.type }}</span>
          <p>{{ toast.message }}</p>
        </div>
        <button
          @click="toastStore.removeToast(toast.id)"
          class="close-button"
          aria-label="Close"
        >
          ✖
        </button>
      </div>
    </transition-group>
  </div>
</template>


<style scoped>
.toast-container {
  position: fixed;
  top: 10px;
  right: 10px;
  z-index: 9999;
  display: flex; /* บังคับให้แสดง */
  flex-direction: column;
  gap: 10px;
}

.toast {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  color: white;
  opacity: 1; /* บังคับให้ไม่โปร่งใส */
  transition: opacity 0.5s ease-in-out;
}

.toast.success {
  background-color: #4caf50;
}

.toast.error {
  background-color: #f44336;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

</style>
