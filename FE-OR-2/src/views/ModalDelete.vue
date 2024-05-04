<script setup>

const props = defineProps({
  showModal: Boolean,
  taskId: String,
  title: String,
})

defineEmits(["closeDeleteModal", "confirmDeleteModal"])
</script>

<template>
    <div v-if="showModal" class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div class="flex items-start justify-center mt-24 text-center">
      <div
        class="modals rounded-lg text-center shadow-xlp-6 w-min"
        role="dialog"
        aria-modal="true"
      >
      <div class="modal-content">
        <p>Do you want to delete the task {{ taskId }} - {{ title }}</p>
        <button @click="$emit('confirmDeleteModal', taskId)">Confirm</button> <!-- ไปเรียกใช้ fn deleteTask ของ HomeView -->
        <button @click="$emit('closeDeleteModal')">Cancel</button> <!-- ไปเรียกใช้ fn closeDeleteModal ของ HomeView -->
      </div>
      </div>
    </div>
  </div>
</template>

<style>
.modal {
  display: block;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
  background-color: #fefefe;
  margin: 15% auto;
  padding: 20px;
  border: 1px solid #888;
  width: 80%;
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
</style>
