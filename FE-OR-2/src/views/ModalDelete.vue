<script setup>

const props = defineProps({
  showModal: Boolean,
  taskId: String,
  title: String,
})

defineEmits(["closeModal", "confirmDeleteModal"])
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
      <div
        class="itbkk-button-delete rounded-lg text-center shadow-xlp-6 w-min"
        role="dialog"
        aria-modal="true"
      >
        <div class="confirmModal-content flex flex-col justify-center items-center">
          <svg class="mb-4" xmlns="http://www.w3.org/2000/svg" width="50" height="50" viewBox="0 0 24 24">
            <path fill="#e05555" d="M12 21a9 9 0 1 1 9-9a9 9 0 0 1-9 9m0-16.5a7.5 7.5 0 1 0 7.5 7.5A7.5 7.5 0 0 0 12 4.5"/><path fill="#e05555" d="M12 13a.76.76 0 0 1-.75-.75v-3.5a.75.75 0 0 1 1.5 0v3.5A.76.76 0 0 1 12 13m0 3a.76.76 0 0 1-.75-.75v-.5a.75.75 0 0 1 1.5 0v.5A.76.76 0 0 1 12 16"/>
          </svg>
          <div class="w-11/12 break-words text-ellipsis overflow-hidden">Do you want to delete the task number {{ taskId }} - {{ title }}</div>
          <div class="Button h-32 flex justify-center items-center gap-10 rounded-full">
            <button class="cancelBtn" @click="$emit('closeModal')">Cancel</button> 
            <button class="confirmBtn" @click="$emit('confirmDeleteModal', taskId)">Confirm</button>
          </div>
        </div>
      </div>
  </div>
</template>

<style>
.itbkk-button-delete {
  background-color: #ffffff;
  color: #47467b;
  max-width: 600px;
  width: 600px;
  min-height: 200px;
  padding-top: 15px;
  border-radius: 40px;
  overflow: hidden;
}

.confirmModal-content {
  padding-top: 20px;
  font-size: large;
}

.Button button {
  font-size: 1.2rem;
  width: 110px;
  height: 50px;
  border-radius: 20px;
  transition: transform 0.3s ease-in-out;
}

button:hover {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  box-shadow: 5px 5px 5px #c6c6c646;
}

.confirmBtn {
  background-color: #e05555;
  color: #ffffff;
}

.cancelBtn {
  background-color: #e0e0e0;
  color: #808080;
}
</style>