<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStatusStore } from "@/stores/StatusStore.js"; // ใช้ StatusStore

// Store และ Router
const store = useStatusStore();
const router = useRouter();
const route = useRoute();

// Props สำหรับข้อมูลที่รับจาก Component หลัก
const props = defineProps({
  showModal: Boolean,
  statusId: Number,
  name: String,
});

defineEmits(["closeModal", "confirmDeleteStatus", "deleteTransfer"]); // Emit event

// State สำหรับ modal
const isDeletable = ref(true);
const currentStatus = ref({});
const newStatusId = ref(null); // สำหรับเลือก Status ที่จะโอน task ไป

// ดึงค่า boardId จาก Route
const boardId = route.params.boardId;

// ฟังก์ชันดึงข้อมูล Status
const getStatus = async () => {
  try {
    const status = await store.getStatus(boardId, props.statusId); // ใช้ store
    if (!status) throw new Error("Status not found");

    currentStatus.value = status;
    isDeletable.value = status.count === 0; // ถ้า count > 0 จะต้องโอนย้าย tasks
    newStatusId.value = store.statuses[0]?.id; // เลือก Status ใหม่ (อันแรกใน Store)
  } catch (error) {
    console.error("Error fetching status:", error.message);
  }
};

// เมื่อ Component ถูก mounted
onMounted(getStatus);
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div
      v-if="isDeletable"
      class="modalDelete rounded-lg text-center shadow-xlp-6 w-min"
      role="dialog"
      aria-modal="true"
    >
      <div class="confirmModal-content flex flex-col justify-center items-center">
        <svg class="mb-3" xmlns="http://www.w3.org/2000/svg" width="50" height="50" viewBox="0 0 24 24">
          <path
            fill="#e05555"
            d="M12 21a9 9 0 1 1 9-9a9 9 0 0 1-9 9m0-16.5a7.5 7.5 0 1 0 7.5 7.5A7.5 7.5 0 0 0 12 4.5"
          />
          <path
            fill="#e05555"
            d="M12 13a.76.76 0 0 1-.75-.75v-3.5a.75.75 0 0 1 1.5 0v3.5A.76.76 0 0 1 12 13m0 3a.76.76 0 0 1-.75-.75v-.5a.75.75 0 0 1 1.5 0v.5A.76.76 0 0 1 12 16"
          />
        </svg>
        <span class="text-2xl font-bold mb-4">Delete a Status</span>
        <div class="itbkk-message w-10/12 break-all text-ellipsis overflow-hidden">
          Do you want to delete the <span :style="{ color: '#ad386f' }">{{ name }}</span> status?
        </div>
        <div class="Button h-32 flex justify-center items-center gap-10 rounded-full">
          <button class="itbkk-button-cancel" @click="$emit('closeModal')">
            Cancel
          </button>
          <button class="itbkk-button-confirm" @click="$emit('confirmDeleteStatus', props.statusId)">
            Confirm
          </button>
        </div>
      </div>
    </div>

    <div
      v-else
      class="modalDelete rounded-lg text-center shadow-xlp-6 w-min"
      role="dialog"
      aria-modal="true"
    >
      <div class="confirmModal-content flex flex-col justify-center items-center">
        <svg xmlns="http://www.w3.org/2000/svg" width="50" height="50" viewBox="0 0 24 24">
          <path
            fill="#e05555"
            d="M19.75 16c0 .41-.34.75-.75.75H6.81l1.22 1.22c.29.29.29.77 0 1.06c-.15.15-.34.22-.53.22s-.38-.07-.53-.22l-2.5-2.5a.8.8 0 0 1-.16-.24a.7.7 0 0 1 0-.57c.04-.09.09-.17.16-.24l2.5-2.5c.29-.29.77-.29 1.06 0s.29.77 0 1.06l-1.22 1.22H19c.41 0 .75.34.75.75ZM5 8.75h12.19l-1.22 1.22c-.29.29-.29.77 0 1.06c.15.15.34.22.53.22s.38-.07.53-.22l2.5-2.5c.07-.07.12-.15.16-.24c.08-.18.08-.39 0-.57a.8.8 0 0 0-.16-.24l-2.5-2.5c-.29-.29-.77-.29-1.06 0s-.29.77 0 1.06l1.22 1.22H5c-.41 0-.75.34-.75.75s.34.75.75.75Z"
          />
        </svg>
        <span class="text-2xl font-bold mt-3 mb-4">Transfer a Status</span>
        <div class="itbkk-message w-10/12 break-all text-ellipsis overflow-hidden">
          <p class="mb-3">
            There {{ currentStatus.count === 1 ? "is" : "are" }}
            <span :style="{ color: '#ad386f' }">{{ currentStatus.count }}</span>
            {{ currentStatus.count === 1 ? "task" : "tasks" }} in
            <span :style="{ color: '#ad386f' }">{{ currentStatus.name }}</span> status.
          </p>
          <p class="mb-5">
            To delete this status, transfer the tasks to another status.
          </p>
          <div class="transfer-tasks-to flex justify-center items-center gap-5">
            <p>Transfer tasks to</p>
            <select v-model="newStatusId" class="statusbx">
              <option
                v-for="status in store.statuses.filter(s => s.id !== currentStatus.id)"
                :key="status.id"
                :value="status.id"
              >
                {{ status.name }}
              </option>
            </select>
          </div>
        </div>
        <div class="Button h-32 flex justify-center items-center gap-10 rounded-full">
          <button class="itbkk-button-cancel" @click="$emit('closeModal')">Cancel</button>
          <button class="itbkk-button-transfer" @click="$emit('deleteTransfer', currentStatus.id, newStatusId)">
            Transfer and Delete
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modalDelete {
  background-color: #ffffff;
  color: black;
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

button.itbkk-button-transfer {
  background-color: #e05555;
  color: #ffffff;
  width: 200px;
}

.itbkk-button-confirm {
  background-color: #e05555;
  color: #ffffff;
}

.itbkk-button-cancel {
  background-color: #e0e0e0;
  color: #808080;
}
</style>
