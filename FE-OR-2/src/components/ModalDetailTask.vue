<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useTaskStore } from "@/stores/api.js";

// API Endpoint URLs
const baseUrl = import.meta.env.VITE_API_BOARDS;

// Pinia store for tasks and statuses
const store = useTaskStore();
const route = useRoute();
const router = useRouter();
const boardId = route.params.boardId; // ดึงค่า boardId จาก URL
const taskId = route.params.taskId; // ดึงค่า id ของ task
const localTimeZone = ref(Intl.DateTimeFormat().resolvedOptions().timeZone);
const modalDetail = ref({});
const status = ref({});

// ฟังก์ชันสำหรับเรียก API พร้อม JWT Token
async function fetchWithToken(url, options = {}) {
  const token = localStorage.getItem("jwtToken");
  if (!token) return null; // ถ้าไม่มี token ให้ return null

  const response = await fetch(url, {
    ...options,
    headers: {
      Authorization: `Bearer ${token}`, // ส่ง JWT Token
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  // ตรวจสอบสถานะ 401 Unauthorized
  if (response.status === 401) {
    router.push("/login"); // Redirect ไปหน้า login
    return null;
  }

  return response;
}

// ฟังก์ชันดึงข้อมูล Status ของบอร์ดปัจจุบัน
async function fetchDataStatus() {
  const response = await fetchWithToken(
    `${baseUrl}/v3/boards/${boardId}/statuses` // ใช้ fetchWithToken พร้อม boardId
  );
  if (!response) return; // ถ้า response เป็น null ให้ return

  if (response.status === 404) {
    router.push({ name: "NotFound" }); // เปลี่ยนเส้นทางไปที่หน้า NotFound ถ้าพบ 404
    return;
  }

  if (!response.ok) {
    throw new Error(`Failed to fetch status: ${response.statusText}`);
  }

  const dataStatus = await response.json();
  if (!Array.isArray(dataStatus)) {
    throw new TypeError("Fetched status data is not an array");
  }

  return dataStatus;
}

// ฟังก์ชันฟอร์แมตวันที่
function formatDate(date) {
  if (!date) {
    return "N/A"; // กรณีที่ไม่มีวันที่ส่งเข้ามา
  }

  const d = new Date(date);
  if (isNaN(d)) {
    return "Invalid Date"; // กรณีที่วันที่ไม่ถูกต้อง
  }

  return d
    .toLocaleString("en-GB", { timeZone: localTimeZone.value })
    .split(",")
    .join(" ");
}

// ฟังก์ชันเปิด Modal สำหรับแสดงรายละเอียดของ Task
async function openModalDetail() {
  const response = await fetchWithToken(
    `${baseUrl}/v3/boards/${boardId}/tasks/${taskId}` // ใช้ fetchWithToken สำหรับ task ที่เลือก
  );
  if (!response) return; // ถ้า response เป็น null ให้ return

  if (response.status === 404) {
    router.push({ name: "NotFound" }); // เปลี่ยนเส้นทางไปที่หน้า NotFound ถ้าพบ 404
    return;
  }

  const res = await response.json();
  modalDetail.value = res;
  status.value = { ...modalDetail.value.status };
  if (!response.ok) {
    router.push("/");
    setTimeout(() => {
      alert("The requested task does not exist");
    }, 500);
  }
}

// ฟังก์ชันที่เรียกเมื่อ component ถูก mounted
onMounted(async () => {
  const datastatus = await fetchDataStatus(); // ดึงข้อมูล status
  if (datastatus) {
    store.status.value = datastatus;
  }
  await openModalDetail(); // ดึงรายละเอียด task
});

// ฟังก์ชันปิด Modal
const closeModal = () => {
  router.push({ name: "board-tasks", params: { boardId: boardId } }).catch(() => {});
};
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div
      class="modals rounded-lg text-center shadow-xlp-6 w-min"
      role="dialog"
      aria-modal="true"
    >
      <div class="modal-content">
        <form @submit.prevent="openModalDetail">
          <div class="modal-header">
            <div class="itbkk-title text-left">
              <h1>{{ modalDetail.title }}</h1>
            </div>
          </div>
          <hr />

          <div class="modal-body">
            <div class="itbkk-description text-left">
              <div class="flex flex-wrap -mx-3 mb-2">
                <div class="desc w-full md:w-1/3 px-3 mb-6 md:mb-0">
                  <label
                    class="block uppercase tracking-wide  text-xs font-bold mb-2"
                    for="grid-city"
                  >
                    Description
                  </label>
                  <textarea
                    class="itbkk-description descinput appearance-none block w-full bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                    id="description"
                    type="text"
                    :class="{
                      'italic !text-gray-400': !modalDetail.description,
                    }"
                    disabled
                    >{{
                      modalDetail.description?.trim() ||
                      "No Description Provided"
                    }}</textarea
                  >
                </div>
              </div>
            </div>

            <div class="detailright flex-col">
              <div class="text-left">
                <div class="assign flex flex-wrap -mx-3 mb-2">
                  <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label
                      class="block uppercase tracking-wide text-xs font-bold mb-2"
                      for="grid-city"
                    >
                      Assignees
                    </label>
                    <textarea
                      class="itbkk-assignees ass appearance-none block w-full bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
                      id="assign"
                      type="text"
                      :class="{
                        'italic !text-gray-400': !modalDetail.assignees,
                      }"
                      disabled
                      >{{
                        modalDetail.assignees?.trim() || "Unassigned"
                      }}</textarea
                    >
                  </div>
                </div>
              </div>

              <div class="itbkk-status text-left">
                <div class="w-full md:w-1/3 mb-6 md:mb-0">
                  <label
                    class="block uppercase tracking-wide text-xs font-bold mb-2"
                    for="grid-state"
                  >
                    Status
                  </label>
                  <div class="relative itbkk-status">
                    <select
                      class="statusbx block w-60 bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                      id="grid-state"
                      v-model="status.name"
                      disabled
                    >
                      <option
                        v-for="(status, index) in store.status.value"
                        :key="index"
                       
                      >
                        {{ status.name }}
                      </option>
                    </select>
                  </div>
                </div>
              </div>

              <div class="time">
                <div class="itbkk-timezone text-left">
                  <p>Timezone : {{ localTimeZone }}</p>
                </div>
                <div class="itbkk-created-on text-left">
                  <p>CreatedOn : {{ formatDate(modalDetail.created_on) }}</p>
                </div>
                <div class="itbkk-updated-on text-left">
                  <p>UpdatedOn : {{ formatDate(modalDetail.updated_on) }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <div class="itbkk-button">
              <hr />
              <button @click="closeModal" class="close border">Close</button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>


<style scoped>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");

.modals {
  background-color: #bfb8da;
  color: black;
  width: 800px;
}

.details {
  color: black;
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 3;
}

.ok {
  width: 4rem;
  background-color: rgb(179, 236, 168);
  color: rgb(15, 82, 21);
  margin: 10px;
  float: right;
  border-radius: 50px;
}

.close {
  width: 4rem;
  background-color: rgb(248, 195, 199);
  color: rgb(82, 15, 18);
  margin: 10px;
  margin-right: 20px;
  float: right;
  border-radius: 50px;
}

.ok:hover {
  background-color: rgb(28, 126, 36);
  color: rgb(179, 236, 168);
}

.close:hover {
  background-color: rgb(163, 42, 48);
  color: rgb(248, 195, 199);
}

.modal-header {
  color: black;
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 30px;
  margin: 15px;
  overflow-x: auto;
}

.modal-header::-webkit-scrollbar {
  height: 12px;
}

h1 {
  padding-bottom: 5px;
}

.desc {
  margin: 15px;
}

.descinput {
  width: 30rem;
  height: 20rem;
}

.ass {
  width: 15rem;
  height: 10rem;
}

.time {
  margin-top: 10px;
  margin-right: 15px;
  font-size: 0.8rem;
  font-weight: 800;
}

.modal-body {
  display: flex;
  padding: 10px;
}

.detailright {
  padding: 15px;
}

::-webkit-scrollbar {
  width: 12px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: #a8a8a8;
}

::-webkit-scrollbar-thumb:hover {
  background: #9b9b9b;
}

@media screen and (max-width: 800px) {
  .modals {
    width: 400px;
    height: 500px;
    overflow-y: scroll;
    overflow-x: hidden;
    padding-bottom: 3rem;
  }

  .modal-header::-webkit-scrollbar {
    height: 12px;
  }

  h1 {
    padding-bottom: 5px;
  }

  .modal-body {
    display: block;
  }

  .itbkk-assignees {
    width: 21rem;
  }

  .descinput {
    width: 21rem;
    height: 20rem;
  }
}
</style>