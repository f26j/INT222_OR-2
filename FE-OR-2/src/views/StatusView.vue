<script setup>
// import zone
import { onMounted, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useStatusStore } from "@/stores/apiController.js";
import { useToastStore } from "@/stores/toast.js";
import Bg_effect from "../components/css/Background.vue";
import Copyright from "../components/css/Copyright.vue";
import ToastAddStatus from "../components/toast/ToastAddStatus.vue";
import ToastEditStatus from "../components/toast/ToastEditStatus.vue";
import ToastDeleteStatus from "../components/toast/ToastDeleteStatus.vue";
import ModalDeleteStatus from "../components/ModalDeleteStatus.vue";
import Navbar from "../components/css/Navbar.vue";

// Store, API zone
const baseUrl = import.meta.env.VITE_API_BOARDS;
const router = useRouter();
const route = useRoute();
const store = useStatusStore();
const toastStore = useToastStore();
const {
  showToastAddStatus,
  showToastEditStatus,
  showToastDeleteStatus,
  closeToast,
} = storeToRefs(toastStore);

// Variable zone
const showDelete = ref(false);
const selectDeleteStatusId = ref("");
const selectDeleteStatusName = ref("");
const boardId = route.params.boardId;
const boardName = ref("");
// ฟังก์ชันสำหรับดึง Token
function getToken() {
  const token = localStorage.getItem("jwtToken");
  if (!token) {
    console.error("No token available");

    return null;
  }
  return token;
}

// ฟังก์ชันสำหรับดึงข้อมูลพร้อม Token
async function fetchWithToken(url, options = {}) {
  const token = getToken();
  if (!token) return null;
  try {
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
      console.error("Unauthorized access - Redirecting to login");

      return null;
    } else if (response.status === 403) {
    router.push({ name: "AccessDenied" })
    return null
  }

    return response;
  } catch (error) {
    console.error("Error fetching data:", error);
    return null;
  }
}

async function fetchBoard() {
  const response = await fetchWithToken(`${baseUrl}/v3/boards/${boardId}`);
  if (!response) return; 
  if (response.status === 404) {
    router.push({ name: "NotFound" }); // เปลี่ยนเส้นทางไปที่หน้า NotFound ถ้าพบ 404
    return;
  }

  if (!response.ok) {
    throw new Error(`Error fetching board: ${response.statusText}`);
  }

  const data = await response.json();
  boardName.value = data.name; // เก็บชื่อบอร์ดใน boardName
}

// ดึงข้อมูล Status ของ Board
async function fetchData() {
  const response = await fetchWithToken(
    `${baseUrl}/v3/boards/${boardId}/statuses`
  ); // เรียกใช้ fetchWithToken
  if (!response) return; // ถ้า response เป็น null ให้ return

  if (response.status === 404) {
    router.push({ name: "NotFound" }); // เปลี่ยนเส้นทางไปที่หน้า NotFound ถ้าพบ 404
    return;
  }

  if (!response.ok) {
    throw new Error(`Error fetching status: ${response.statusText}`);
  }

  const data = await response.json();

  if (!Array.isArray(data)) {
    throw new TypeError("Fetched data is not an array");
  }
  return data;
}

// เมื่อ component ถูก mounted ให้ดึงข้อมูล Status
onMounted(async () => {
  await fetchBoard();
  const data = await fetchData();
  store.statuses.value = data;
});

// เปิดหน้า Home
const openHome = () => {
  router.push({ name: "board-tasks" });
};

// เปิด Modal สำหรับการเพิ่ม Status
const openModalAdd = async () => {
  router.push({ name: "status-add" });
};

// เปิด Modal สำหรับการแก้ไข Status
const openModalEdit = (id) => {
  if (id) {
    router.push({ name: "status-edit", params: { statusId: id } });
  } else {
    console.error("ID parameter is required to open the edit modal");
  }
};

// เปิด Modal สำหรับการลบ Status
const openModalDelete = (id, name) => {
  showDelete.value = true;
  selectDeleteStatusId.value = id;
  selectDeleteStatusName.value = name;
};

// ฟังก์ชันสำหรับการลบ Status
async function deleteStatus(id) {
  const response = await fetchWithToken(
    `${baseUrl}/v3/boards/${boardId}/statuses/${id}`,
    {
      method: "DELETE",
    }
  );

  if (!response) return; // ถ้า response เป็น null ให้ return

  if (response.status === 404) {
    router.push({ name: "NotFound" }); // เปลี่ยนเส้นทางไปที่หน้า NotFound ถ้าพบ 404
    return;
  }

  if (response.ok) {
    store.statuses.value = store.statuses.value.filter(
      (status) => status.id !== id
    );
    toastStore.handleStatusDeleted(); // แสดงผลว่า status ถูกลบสำเร็จ
  } else {
    toastStore.handleStatusDeletedF(); // แสดงผลว่าการลบล้มเหลว
    console.error("Failed to delete status");
  }

  confirmDeleteStatus(); // เรียกใช้งานหลังจากการลบเสร็จสิ้น
  showDelete.value = false; // ปิด modal
}

async function deleteTransfer(id, newId) {
  const token = getToken();
  if (!token) return; // ถ้าไม่มี token ให้หยุดการทำงาน

  try {
    const response = await fetch(
      `${baseUrl}/v3/boards/${boardId}/statuses/${id}/${newId}`,
      {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`, // ส่ง JWT Token
          "Content-Type": "application/json",
        },
      }
    );

    if (response.ok) {
      store.statuses.value = store.statuses.value.filter(
        (status) => status.id !== id
      );
      toastStore.handleStatusDeleted(); // แสดงผลว่า status ถูกลบสำเร็จ
    } else {
      toastStore.handleStatusDeletedF(); // แสดงผลว่าการลบล้มเหลว
      console.error("Failed to delete status");
    }
  } catch (error) {
    toastStore.handleStatusDeletedF(); // แสดงผลว่ามีข้อผิดพลาดในการลบ
    console.error("Error deleting status:", error.message);
  }

  confirmDeleteStatus(); // เรียกใช้งานหลังจากการลบเสร็จสิ้น
  showDelete.value = false; // ปิด modal
}

// ฟังก์ชันสำหรับยืนยันการลบ Status
const confirmDeleteStatus = async () => {
  try {
    return;
  } catch (error) {
    toastStore.handleStatusDeletedF();
    console.error("Error deleting status:", error);
  }
  return {
    closeModal,
    confirmDeleteStatus,
  };
};

// ปิด Modal
const closeModal = () => {
  showDelete.value = false;
  router.push({ name: "statusView" });
};
</script>

<template>
  <Navbar class="itbkk-fullname" />
  <div class="pt-24">
    <header class="mb-2 max-w-6xl mx-auto">
      <h1 class="text-left text-3xl font-bold">IT-Bangmod Kradan Kanban</h1>
      <div class="flex justify-between items-center mt-2">
        <h2 class="text-lg font-semibold text-gray-800">
          <span
            class="itbkk-boards cursor-pointer"
            @click="router.push({ name: 'board' })"
            >Board</span
          >&nbsp;>&nbsp;
          <span
            class="itbkk-tasks cursor-pointer"
            @click="router.push({ name: 'board-tasks' })"
            >{{ boardName }}</span
          >&nbsp;>&nbsp;<span class="textPurple">Status</span>
        </h2>
        <div class="flex space-x-4">
          <button
            @click="openHome"
            class="itbkk-button-home flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="32"
              height="32"
              fill="currentColor"
              class="mr-2"
              viewBox="0 4 16 16"
            >
              <path
                fill="currentColor"
                d="m16 8.5l1.53 1.53l-1.06 1.06L10 4.62l-6.47 6.47l-1.06-1.06L10 2.5l4 4v-2h2zm-6-2.46l6 5.99V18H4v-5.97zM12 17v-5H8v5z"
              ></path>
            </svg>
            Back
          </button>

          <button
            @click="openModalAdd"
            class="itbkk-button-add flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="32"
              height="32"
              fill="currentColor"
              class="mr-2"
              viewBox="0 0 16 16"
            >
              <path
                fill="currentColor"
                fillRule="evenodd"
                d="M8 1a1 1 0 0 0-2 0v5H1a1 1 0 0 0 0 2h5v5a1 1 0 1 0 2 0V8h5a1 1 0 1 0 0-2H8z"
                clipRule="evenodd"
              />
            </svg>
            Add Status
          </button>
        </div>
      </div>
    </header>
  </div>
  <div class="body">
    <main class="flex flex-col items-center">
      <div class="tb">
        <table>
          <thead>
            <tr>
              <th></th>
              <th>Name</th>
              <th>Description</th>
              <th class="flex justify-center">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(status, index) in store.statuses.value"
              class="itbkk-item"
            >
              <td class="bgd">
                <span class="bgi">id : </span>
                <div class="idkb flex">
                  {{ index + 1 }}
                </div>
              </td>
              <td class="itbkk-status-name">
                <p>{{ status.name }}</p>
              </td>
              <td
                class="itbkk-status-description"
                :class="{ 'italic-gray': !status.description?.trim() }"
              >
                <p v-if="!status.description?.trim()">
                  No description is provided.
                </p>
                <p v-else>{{ status.description }}</p>
              </td>
              <td class="itbkk-button-action flex">
                <div class="h-[30px]"></div>
                <button
                  class="itbkk-button-edit flex items-center space-x-2 w-20"
                  @click="openModalEdit(status.id)"
                  v-if="status.name !== 'No Status' && status.name !== 'Done'"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="30"
                    height="30"
                    viewBox="0 0 24 24"
                  >
                    <path
                      fill="currentColor"
                      fill-rule="evenodd"
                      d="M17.907 4.93c.104-.105.294-.186.539-.18a.848.848 0 0 1 .572.216c.13.13.222.34.231.575c.01.234-.065.413-.167.515l-7.49 7.5l-1.273.116l.1-1.245zm2.173-1.024c-.437-.438-1.037-.643-1.6-.656c-.564-.013-1.182.167-1.635.62l-7.682 7.692a.75.75 0 0 0-.217.47l-.194 2.407a.75.75 0 0 0 .816.807l2.43-.22a.75.75 0 0 0 .463-.218l7.682-7.692c.456-.456.627-1.073.605-1.635a2.373 2.373 0 0 0-.668-1.575M5.812 4.75a2.568 2.568 0 0 0-2.563 2.562v10.875a2.568 2.568 0 0 0 2.563 2.563h10.875a2.568 2.568 0 0 0 2.563-2.562V12.75a.75.75 0 0 0-1.5 0v5.438c0 .582-.48 1.062-1.063 1.062H5.812c-.583 0-1.063-.48-1.063-1.062V7.312c0-.582.48-1.062 1.063-1.062h5.437a.75.75 0 0 0 0-1.5z"
                      clip-rule="evenodd"
                    />
                  </svg>
                  <span>Edit</span>
                </button>

                <button
                  class="itbkk-button-delete flex items-center space-x-2 w-24"
                  @click="openModalDelete(status.id, status.name)"
                  v-if="status.name !== 'No Status' && status.name !== 'Done'"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="30"
                    height="30"
                    viewBox="0 0 24 24"
                  >
                    <path
                      fill="currentColor"
                      d="M20 8.7H4a.75.75 0 1 1 0-1.5h16a.75.75 0 0 1 0 1.5"
                    />
                    <path
                      fill="currentColor"
                      d="M16.44 20.75H7.56A2.4 2.4 0 0 1 5 18.49V8a.75.75 0 0 1 1.5 0v10.49c0 .41.47.76 1 .76h8.88c.56 0 1-.35 1-.76V8A.75.75 0 1 1 19 8v10.49a2.4 2.4 0 0 1-2.56 2.26m.12-13a.74.74 0 0 1-.75-.75V5.51c0-.41-.48-.76-1-.76H9.22c-.55 0-1 .35-1 .76V7a.75.75 0 1 1-1.5 0V5.51a2.41 2.41 0 0 1 2.5-2.26h5.56a2.41 2.41 0 0 1 2.53 2.26V7a.75.75 0 0 1-.75.76Z"
                    />
                    <path
                      fill="currentColor"
                      d="M10.22 17a.76.76 0 0 1-.75-.75v-4.53a.75.75 0 0 1 1.5 0v4.52a.75.75 0 0 1-.75.76m3.56 0a.75.75 0 0 1-.75-.75v-4.53a.75.75 0 0 1 1.5 0v4.52a.76.76 0 0 1-.75.76"
                    />
                  </svg>
                  <span>Delete</span>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <ToastAddStatus v-if="showToastAddStatus" @close="closeToast">
      </ToastAddStatus>
      <ToastEditStatus v-if="showToastEditStatus" @close="closeToast">
      </ToastEditStatus>
      <ToastDeleteStatus v-if="showToastDeleteStatus" @close="closeToast">
      </ToastDeleteStatus>
      <ModalDeleteStatus
        v-if="showDelete"
        :statusId="selectDeleteStatusId"
        :name="selectDeleteStatusName"
        @closeModal="closeModal"
        @confirmDeleteStatus="deleteStatus"
        @deleteTransfer="deleteTransfer"
      >
      </ModalDeleteStatus>
    </main>
    <footer>
      <Copyright />
    </footer>
    <Bg_effect />
  </div>
  <router-view></router-view>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");

h1 {
  font-family: "Ubuntu", sans-serif;
  color: black;
}

h2 {
  color: black;
}

h3 {
  font-family: "Ubuntu", sans-serif;
  color: #737292;
}

.a {
  color: rgb(250, 133, 188);
}

.textGray {
  color: #717171;
}

.statusBtn {
  display: flex;
  height: 40px;
  width: 100%;
  max-width: 900px;
  padding-left: 2rem;
}

.btn-stt {
  display: flex;
  justify-content: center;
  font-style: italic;
  padding: 5px;
  border-radius: 50px;
  width: 120px;
  height: 50px;
  transition: transform 0.3s ease;
}

.itbkk-tasks:hover {
  color: #a31a5a;
}

.itbkk-boards:hover {
  color: #47467b;
}


.itbkk-button-home {
  font-style: italic;
  background-color: rgba(255, 255, 255, 0);
  color: #683c86;
  border: 2px solid #683c86;
  border-color: #683c86;
  transition: background-color 0.5s ease, transform 150ms cubic-bezier(0.4, 0, 0.2, 1);
}
.itbkk-button-home:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.itbkk-button-add {
  background-color: #683c86;
  color: white;
}

.hometext,
.addtext {
  color: #ffe6fc;
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 20px;
  padding-top: 5px;
  padding-right: 10px;
}

.homeicon,
.addicon {
  color: #ffedfd;
  width: 40px;
  height: 40px;
  padding-right: 10px;
  padding-left: 5px;
  cursor: pointer;
}
.itbkk-button-add:hover {
  background-color: #9b65be;
}

.itbkk-button-action {
  padding: 15px 5px 10px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.itbkk-button-action button {
  background-color: #905cb334;
  border-radius: 10px;
  height: 40px;
  justify-content: center;
  align-items: center;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.itbkk-button-action button:hover {
  transform: scale(1.06);
  transition: transform 0.3s ease;
  color: #683c86;
}

.itbkk-status-name p,
.itbkk-status-description p {
  width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  /* word-break: break-all; */
}

.itbkk-status-name p {
  color: black;
}

.italic-gray {
  font-style: italic;
  color: gray;
}

.textPurple {
  color: #683c86;
}

.textPurple:hover {
  color: #5e1f87;
}

header {
  margin-bottom: 20px;
}

.idkb {
  display: inline;
}

.tb {
  padding: 2rem;
  width: 100%;
  overflow: auto;
}

table {
  border-collapse: collapse;
  width: 100%;
  max-width: 900px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.432);
  margin: 0 auto;
  border-radius: 25px;
}

th,
td {
  padding: 15px;
  background-color: rgba(255, 254, 255, 0.329);
}

th {
  text-align: left;
  color: rgba(255, 255, 255, 0.748);
}

td {
  color: black;
  font-family: "Ubuntu", sans-serif;
  font-weight: 600;
  font-style: normal;
}

td.bgd {
  padding-left: 30px;
}

thead th {
  background-color: #683c86;
}

tbody tr:hover {
  background-color: rgba(255, 255, 255, 0.63);
}

.bgi {
  display: none;
}

tbody {
  td {
    position: relative;
  }
  &:hover {
    &:before {
      content: "";
      position: absolute;
      left: 0;
      right: 0;
      background-color: rgba(255, 255, 255, 0.726);
      z-index: -1;
    }
  }
}

footer {
  padding-bottom: 5rem;
}

@media screen and (max-width: 745px) {
  table,
  thead,
  tbody,
  th,
  td,
  tr {
    display: block;
  }

  thead tr {
    position: absolute;
    top: -9999px;
    left: -9999px;
  }

  .bgd {
    background-color: #8e5bb1;
    color: rgba(255, 255, 255, 0.774);
  }

  .bgi {
    display: inline;
    margin-left: -1rem;
  }

  .button {
    width: 60px;
    height: 60px;
  }

  tr {
    border: 1px solid #ccc;
  }

  td {
    border: none;
    border-bottom: 1px solid #eee;
    position: relative;
    padding-left: 3rem;
  }

  td:before {
    position: absolute;
    top: 6px;
    left: 6px;
    width: 45%;
    padding-right: 10px;
    white-space: nowrap;
  }

  .btn-a {
    position: fixed;
  }

  .tb {
    z-index: 1;
    position: relative;
  }

  .itbkk-button-action {
    justify-content: left;
    padding-left: 3rem;
    gap: 0;
  }

  .itbkk-status-name p,
  .itbkk-status-description p {
    width: 100%;
  }
}
</style>
