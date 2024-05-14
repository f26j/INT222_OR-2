<script setup>
import { useToastStore } from "@/stores/toast.js";
const toastStore = useToastStore();
</script>

<template>
  <div
    class="addStatusToast active toast toast-top toast-end"
    :class="{ 'bg-red-50': !toastStore.addSuccess, 'bg-green-50': toastStore.addSuccess }"
  >
    <div class="addStatusToast-content">
      <div class="icon">
        <svg
          v-if="toastStore.addSuccess"
          xmlns="http://www.w3.org/2000/svg"
          width="2em"
          height="2em"
          viewBox="0 0 16 16"
        >
          <path
            fill="currentColor"
            d="M8 16A8 8 0 1 1 8 0a8 8 0 0 1 0 16m3.78-9.72a.75.75 0 0 0-.018-1.042a.75.75 0 0 0-1.042-.018L6.75 9.19L5.28 7.72a.75.75 0 0 0-1.042.018a.75.75 0 0 0-.018 1.042l2 2a.75.75 0 0 0 1.06 0Z"
          ></path>
        </svg>
        <svg
          v-else
          xmlns="http://www.w3.org/2000/svg"
          width="32"
          height="32"
          viewBox="0 0 16 16"
        >
          <path
            fill="#ef4444"
            d="M6.457 1.047c.659-1.234 2.427-1.234 3.086 0l6.082 11.378A1.75 1.75 0 0 1 14.082 15H1.918a1.75 1.75 0 0 1-1.543-2.575Zm1.763.707a.25.25 0 0 0-.44 0L1.698 13.132a.25.25 0 0 0 .22.368h12.164a.25.25 0 0 0 .22-.368Zm.53 3.996v2.5a.75.75 0 0 1-1.5 0v-2.5a.75.75 0 0 1 1.5 0M9 11a1 1 0 1 1-2 0a1 1 0 0 1 2 0"
          />
        </svg>
      </div>
      <div class="message">
        <span class="text-1">{{ toastStore.addSuccess ? "Success" : "Error" }}</span>
        <span class="itbkk-message text-2">{{
          toastStore.addSuccess
            ? "The status has been added"
            : "An error has occurred, the status could not be added."
        }}</span>
      </div>
      <i @click="toastStore.closeToast" class="close"></i>
    </div>
    <div v-if="toastStore.addSuccess" class="progress active"></div>
    <div v-else class="progress inactive"></div>
  </div>
</template>

<style>
.addStatusToast {
  position: fixed;
  height: 5rem;
  width: 25rem;
  top: 45px;
  right: 30px;
  border-radius: 12px;
  padding: 20px 35px 20px 25px;
  box-shadow: 0 6px 20px -5px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 4;
}
.addStatusToast .addStatusToast-content {
  display: flex;
  align-items: center;
}

.icon {
  color: #91d694;
  padding-left: 15px;
}

.addStatusToast-content .check {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 35px;
  min-width: 35px;
  background-color: #91d694;
  color: #fff;
  font-size: 20px;
  border-radius: 50%;
}

.addStatusToast-content .message {
  display: flex;
  flex-direction: column;
  margin: 0 20px;
}

.message {
  padding: 10px;
  padding-top: 13px;
}

.message .itbkk-message {
  font-size: 16px;
  font-weight: 400;
  color: #666666;
}

.message .text-1 {
  font-weight: 600;
  color: #333;
}

.addStatusToast .progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  width: 100%;
}

.addStatusToast .progress:before {
  content: "";
  position: absolute;
  bottom: 0;
  right: 0;
  height: 100%;
  width: 100%;
}

.addStatusToast .progress.active:before {
  background-color: #5cbb60;
  animation: progress 5s linear forwards;
}

.addStatusToast .progress.inactive:before {
  background-color: #ef4444;
  animation: progress 5s linear forwards;
}

@keyframes progress {
  100% {
    right: 100%;
  }
}
</style>
