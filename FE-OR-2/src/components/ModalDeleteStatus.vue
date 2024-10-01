<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStatusStore } from "@/stores/api.js";

const baseUrl = import.meta.env.VITE_API_BOARDS;
const store = useStatusStore();
const isDeletable = ref(true);
const currentStatus = ref({});
const newStatusId = ref(1);
const router = useRouter();
const route = useRoute();
const boardId = route.params.boardId; 

function getToken() {
  const token = localStorage.getItem("jwtToken");
  if (!token) {
    console.error("No token available");
    router.push("/login"); // Redirect ไปหน้า Login เมื่อไม่มี Token
    return null;
  }
  return token;
}

async function getStatus() {
  const token = getToken(); // ดึง token จาก localStorage
  if (!token) return; // ถ้าไม่มี token ให้หยุดการทำงาน

  try {
    const response = await fetch(
      `${baseUrl}/v3/boards/${boardId}/statuses/${props.statusId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`, // ส่ง JWT Token
          "Content-Type": "application/json",
        },
      }
    );

    if (response.status === 404) {
    router.push({ name: "NotFound" }); // เปลี่ยนเส้นทางไปที่หน้า NotFound ถ้าพบ 404
    return;
  }

    // ตรวจสอบสถานะ 401 Unauthorized
    if (response.status === 401) {
      router.push("/login"); // Redirect ไปหน้า login เมื่อสถานะเป็น 401
      return;
    }

    const res = await response.json();
    console.log(res.count);

    // ตรวจสอบว่า status สามารถลบได้หรือไม่
    if (res.count > 0) {
      isDeletable.value = false;
    }
    currentStatus.value = res;
  } catch (error) {
    console.error("Error fetching status:", error);
  }
}

onMounted(() => {
  getStatus();
});

const props = defineProps({
  showModal: Boolean,
  statusId: Number,
  name: String,
});
defineEmits(["closeModal", "confirmDeleteStatus", "deleteTransfer"]);
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div
      v-if="isDeletable"
      class="modalDelete rounded-lg text-center shadow-xlp-6 w-min"
      role="dialog"
      aria-modal="true"
    >
      <div
        class="confirmModal-content flex flex-col justify-center items-center"
      >
        <svg
          class="mb-3"
          xmlns="http://www.w3.org/2000/svg"
          width="50"
          height="50"
          viewBox="0 0 24 24"
        >
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
        <div
          class="itbkk-message w-10/12 break-all text-ellipsis overflow-hidden"
        >
          Do you want to delete the
          <span :style="{ color: '#ad386f' }">{{ name }}</span> status?
        </div>
        <div
          class="Button h-32 flex justify-center items-center gap-10 rounded-full"
        >
          <button class="itbkk-button-cancel" @click="$emit('closeModal')">
            Cancel
          </button>
          <button
            class="itbkk-button-confirm"
            @click="$emit('confirmDeleteStatus', statusId)"
          >
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
      <div
        class="confirmModal-content flex flex-col justify-center items-center"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="50"
          height="50"
          viewBox="0 0 24 24"
        >
          <path
            fill="#e05555"
            d="M19.75 16c0 .41-.34.75-.75.75H6.81l1.22 1.22c.29.29.29.77 0 1.06c-.15.15-.34.22-.53.22s-.38-.07-.53-.22l-2.5-2.5a.8.8 0 0 1-.16-.24a.7.7 0 0 1 0-.57c.04-.09.09-.17.16-.24l2.5-2.5c.29-.29.77-.29 1.06 0s.29.77 0 1.06l-1.22 1.22H19c.41 0 .75.34.75.75ZM5 8.75h12.19l-1.22 1.22c-.29.29-.29.77 0 1.06c.15.15.34.22.53.22s.38-.07.53-.22l2.5-2.5c.07-.07.12-.15.16-.24c.08-.18.08-.39 0-.57a.8.8 0 0 0-.16-.24l-2.5-2.5c-.29-.29-.77-.29-1.06 0s-.29.77 0 1.06l1.22 1.22H5c-.41 0-.75.34-.75.75s.34.75.75.75Z"
          />
        </svg>
        <span class="text-2xl font-bold mt-3 mb-4">Transfer a Status</span>
        <div
          class="itbkk-message w-10/12 break-all text-ellipsis overflow-hidden"
        >
          <p class="mb-3">
            There {{ currentStatus.count === 1 ? "is" : "are" }}
            <span :style="{ color: '#ad386f' }">{{ currentStatus.count }}</span>
            {{ currentStatus.count === 1 ? "task" : "tasks" }} in
            <span :style="{ color: '#ad386f' }">{{ currentStatus.name }}</span>
            status.
          </p>
          <p class="mb-5">
            In order to delete this status, the system must transfer task in
            this status to existing status.
          </p>
          <div class="transfer-tasks-to flex justify-center items-center gap-5">
            <p>
              Transfer {{ currentStatus.count === 1 ? "a task" : "tasks" }} to
            </p>
            <select
              class="statusbx block w-60 bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
              id="grid-state"
              v-model="newStatusId"
            >
              <option
                v-for="(status, index) in store.statuses.value.filter(
                  (status) => status.id != currentStatus.id
                )"
                :value="status.id"
              >
                {{ status.name }}
              </option>
            </select>
          </div>
        </div>
        <div
          class="Button h-32 flex justify-center items-center gap-10 rounded-full"
        >
          <button class="itbkk-button-cancel" @click="$emit('closeModal')">
            Cancel
          </button>
          <button
            class="itbkk-button-transfer"
            @click="$emit('deleteTransfer', currentStatus.id, newStatusId)"
          >
            Transfer and Delete
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
.modalDelete {
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
