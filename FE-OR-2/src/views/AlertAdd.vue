<script setup>
import { defineProps } from "vue";
const { showAlertAdd } = defineProps();
</script>
<template>
  <div v-if="showAlertAdd" class="toast active">
    <div class="toast-content">
      <i class="icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="2em" height="2em" viewBox="0 0 16 16"><path fill="currentColor" d="M8 16A8 8 0 1 1 8 0a8 8 0 0 1 0 16m3.78-9.72a.75.75 0 0 0-.018-1.042a.75.75 0 0 0-1.042-.018L6.75 9.19L5.28 7.72a.75.75 0 0 0-1.042.018a.75.75 0 0 0-.018 1.042l2 2a.75.75 0 0 0 1.06 0Z"></path></svg>
      </i>
      <div class="message">
        <span class="text text-1">Success</span>
        <span class="text text-2">The task has been successfully added</span></div>
    </div>
    <i class="close"></i>
    <div class="progress active"></div>
  </div>

</template>

<style>

.toast {
  position: fixed;
  height: 5rem;
  width: 25rem;
  top: 25px;
  right: 30px;
  border-radius: 12px;
  background: #e7ffe7;
  padding: 20px 35px 20px 25px;
  box-shadow: 0 6px 20px -5px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transform: translateX(calc(100% + 30px));
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.35);
  z-index: 4;
}

.toast.active {
  transform: translateX(0%);
}

.toast .toast-content {
  display: flex;
  align-items: center;
}

.icon {
  color: #91d694;
  padding-left: 15px;
}

.toast-content .check {
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

.toast-content .message {
  display: flex;
  flex-direction: column;
  margin: 0 20px;
}

.message {
  padding: 10px;
  padding-top: 13px;
}

.message .text {
  font-size: 16px;
  font-weight: 400;
  color: #666666;
}

.message .text.text-1 {
  font-weight: 600;
  color: #333;
}

.toast .close {
  position: absolute;
  width: 20px;
  top: 10px;
  right: 15px;
  padding: 5px;
  cursor: pointer;
  opacity: 0.7;
}

.toast .close:hover {
  opacity: 1;
}

.toast .progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  width: 100%;

}

.toast .progress:before {
  content: "";
  position: absolute;
  bottom: 0;
  right: 0;
  height: 100%;
  width: 100%;
  background-color: #5cbb60;
}

.progress.active:before {
  animation: progress 5s linear forwards;
}

@keyframes progress {
  100% {
    right: 100%;
  }
}

.toast.active ~ button {
  pointer-events: none;
}
</style>