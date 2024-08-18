<script setup>
import { useToastStore } from "@/stores/toast.js"
const toastStore = useToastStore()
</script>

<template>
    <div class="itbkk-message flex w-full max-w-xs p-4 mb-4 text-white bg-red-100 rounded-lg shadow" role="alert">
        <div class="inline-flex items-center justify-center flex-shrink-0 w-8 h-8 text-red-500 mt-2">
            <svg class="w-7 h-7" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5Zm3.707 11.793a1 1 0 1 1-1.414 1.414L10 11.414l-2.293 2.293a1 1 0 0 1-1.414-1.414L8.586 10 6.293 7.707a1 1 0 0 1 1.414-1.414L10 8.586l2.293-2.293a1 1 0 0 1 1.414 1.414L11.414 10l2.293 2.293Z"/>
            </svg>
        </div>
        <div class="ms-3 text-sm font-normal">
            <span class="mb-1 text-sm font-semibold text-red-500">Error</span>
            <div class="mb-2 text-sm font-normal text-red-500">Username or Password is incorrect.</div>   
        </div>
        <button type="button" @click="toastStore.closeToast" class="ms-auto -mx-1.5 -my-1.5 bg-white text-gray-400 hover:text-white rounded-lg p-1.5 hover:bg-red-500 inline-flex items-center justify-center h-5 w-5" aria-label="Close">
            <span class="sr-only">Close</span>
            <svg class="w-2 h-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
            </svg>  
        </button>
        <div class="progress active"></div>
    </div>

</template>

<style>
.itbkk-message {
  position: fixed;
  height: 5rem;
  width: 25rem;
  top: 45px;
  right: 30px;
  overflow: hidden;
}

.progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  width: 100%;
}

.progress:before {
  content: "";
  position: absolute;
  bottom: 0;
  right: 0;
  height: 100%;
  width: 100%;
}
.progress:before {
  background-color: #ff8d8d;
  animation: progress 5s linear forwards;
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