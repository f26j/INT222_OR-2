<script setup>
// Import zone
import { onMounted, ref, watch, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useTaskStore } from "@/stores/api.js";
import { useToastStore } from "@/stores/toast.js";
import { useUserStore } from "@/stores/users.js";
import Bg_effect from "../components/css/Background.vue";
import Badges from "../components/css/Badge.vue";
import Copyright from "../components/css/Copyright.vue";
import ToastAdd from "../components/toast/ToastAdd.vue";
import ToastEdit from "../components/toast/ToastEdit.vue";
import ModalDelete from "../components/ModalDeleteTask.vue";
import ToastDelete from "../components/toast/ToastDelete.vue";
import Navbar from "../components/css/Navbar.vue";

// Store,Api zone
const fetchTask = import.meta.env.VITE_API_BOARD_TASK; // http://localhost:8080/api/v3/boards
const fetchStatus = import.meta.env.VITE_API_BOARD_STATUS; // http://localhost:8080/api/v3/boards/{board}/statuses
const router = useRouter();
const route = useRoute();
const store = useTaskStore();
const toastStore = useToastStore();
const { showToastAdd, showToastEdit, closeToast } = storeToRefs(toastStore);

// Variable zone
const boardId = route.params.boardId; // Get boardId from the URL
const taskId = route.params.id;
const selectDeleteTaskId = ref("");
const selectDeleteTaskTitle = ref("");
const selectDeleteTaskIndex = ref("");
const originalTasks = ref([]);
const userStore = useUserStore();

// Function zone
onMounted(async () => {
  try {
    const data = await fetchData(); // Fetch task data
    const datastatus = await fetchDataStatus(); // Fetch status data
    originalTasks.value = [...data];
    store.tasks.value = [...data];
    store.status.value = datastatus;
  } catch (error) {
    console.error("Error during data fetch:", error);
  }
});

async function fetchWithToken(url, options = {}) {
  const token = localStorage.getItem("jwtToken");
  if (!token) {
    console.error("No token available");
    router.push("/login"); // Redirect to login if no token
    return null;
  }

  const response = await fetch(url, {
    ...options,
    headers: {
      Authorization: `Bearer ${token}`, // Add JWT token
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  // Check for 401 Unauthorized
  if (response.status === 401) {
    userStore.logout(); // Log out user
    router.push("/login"); // Redirect to login
    return null;
  }

  return response; // Return the response
}

async function fetchData() {
  // Fetch tasks for the specified board
  const response = await fetchWithToken(fetchTask);
  if (!response) return; // If response is null, return

  if (!response.ok) {
    throw new Error(`Error fetching tasks: ${response.statusText}`);
  }

  const data = await response.json();
  if (!Array.isArray(data)) {
    throw new TypeError("Fetched data is not an array");
  }

  return data;
}

async function fetchDataStatus() {
  // Fetch statuses for the specified board
  const response = await fetchWithToken(
    `${fetchStatus.replace("{board}", boardId)}`
  );
  if (!response) return; // If response is null, return

  if (!response.ok) {
    throw new Error(`Error fetching status: ${response.statusText}`);
  }

  const dataStatus = await response.json();
  if (!Array.isArray(dataStatus)) {
    throw new TypeError("Fetched status data is not an array");
  }

  return dataStatus;
}

// Functions for managing tasks
const openModalAdd = async () => {
  router.push({ name: "task-add" });
};
const openModalDetail = async (id) => {
  if (id) {
    router.push({ name: "task-detail", params: { taskId: id, boardId } });
  } else {
    console.error("Task ID is missing");
  }
};

const openModalEdit = async (id) => {
  if (id) {
    router.push({ name: "task-edit", params: { taskId: id, boardId } });
  } else {
    console.error("Task ID is missing");
  }
};
if (window.location.pathname === `/task/${taskId}`) {
  openModalDetail(taskId);
} else if (window.location.pathname === `/task/${taskId}/edit`) {
  openModalEdit(taskId);
}

const showToastDelete = ref(false);
const deleteSuccess = ref(false);
const showDelete = ref(false);
const toastSuccess = ref(false);

const openModalDelete = (taskId, title, index) => {
  showDelete.value = true;
  selectDeleteTaskId.value = taskId;
  selectDeleteTaskTitle.value = title;
  selectDeleteTaskIndex.value = index;
};

async function deleteTask(taskId) {
  const response = await fetchWithToken(
    `${fetchTask}/${boardId}/tasks/${taskId}`,
    {
      method: "DELETE",
    }
  );

  if (!response) return; // If response is null, return

  if (response.ok) {
    originalTasks.value = originalTasks.value.filter(
      (task) => task.id !== taskId
    );
    store.tasks.value = [...originalTasks.value];
    deleteSuccess.value = true;
  } else {
    deleteSuccess.value = false;
    console.error("Failed to delete task");
  }

  confirmDelete();
  showDelete.value = false;
}

const confirmDelete = async () => {
  try {
    showToastDelete.value = true;
    toastSuccess.value = true;
    setTimeout(() => {
      showToastDelete.value = false;
    }, 5000);
  } catch (error) {
    toastSuccess.value = false;
    setTimeout(() => {
      showToastDelete.value = false;
    }, 5000);
    console.error("Error deleting task:", error);
  }
  return {
    showToastDelete,
    closeModal,
    confirmDelete,
  };
};
const closeToastDelete = () => {
  showToastDelete.value = false;
};
const closeModal = () => {
  showDelete.value = false;
  router.push({ path: `/board/${boardId}` });
};

// Sorting function
const sortAscending = ref(undefined);

const toggleSortOrder = () => {
  if (sortAscending.value === undefined) {
    sortAscending.value = true;
  } else if (sortAscending.value === true) {
    sortAscending.value = false;
  } else {
    sortAscending.value = undefined;
  }
};

const sortedTasks = computed(() => {
  if (sortAscending.value === undefined) {
    return originalTasks.value;
  }
  return sortTasks([...originalTasks.value], sortAscending.value);
});

function sortTasks(tasks, ascending) {
  if (ascending === undefined) return tasks; // No sorting if sort order is undefined
  return tasks.sort((a, b) => {
    const statusA = a.status.name.toLowerCase();
    const statusB = b.status.name.toLowerCase();
    if (statusA < statusB) return ascending ? -1 : 1;
    if (statusA > statusB) return ascending ? 1 : -1;
    return 0;
  });
}

watch(
  () => sortedTasks.value,
  (newVal) => {
    store.tasks.value = newVal;
  }
);
</script>

<template>
  <Navbar class="itbkk-fullname" />
  <div class="body">
    <header>
      <hr class="my-6 sm:mx-auto border border-white/20 lg:my-8" />
      <h1 class="text-center">
        IT-Bangmod Kradan Kanban
        <h3>Home</h3>
      </h1>
      <hr class="my-6 sm:mx-auto border border-white/20 lg:my-8" />
    </header>
    <main class="flex flex-col items-center">
      <div class="manageStatus">
        <button
          type="button"
          class="itbkk-manage-status flex"
          @click="openStatus"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="32"
            height="32"
            viewBox="0 0 512 512"
          >
            <path
              fill="currentColor"
              d="M256 176a80 80 0 1 0 80 80a80.24 80.24 0 0 0-80-80m172.72 80a165.53 165.53 0 0 1-1.64 22.34l48.69 38.12a11.59 11.59 0 0 1 2.63 14.78l-46.06 79.52a11.64 11.64 0 0 1-14.14 4.93l-57.25-23a176.56 176.56 0 0 1-38.82 22.67l-8.56 60.78a11.93 11.93 0 0 1-11.51 9.86h-92.12a12 12 0 0 1-11.51-9.53l-8.56-60.78A169.3 169.3 0 0 1 151.05 393L93.8 416a11.64 11.64 0 0 1-14.14-4.92L33.6 331.57a11.59 11.59 0 0 1 2.63-14.78l48.69-38.12A174.58 174.58 0 0 1 83.28 256a165.53 165.53 0 0 1 1.64-22.34l-48.69-38.12a11.59 11.59 0 0 1-2.63-14.78l46.06-79.52a11.64 11.64 0 0 1 14.14-4.93l57.25 23a176.56 176.56 0 0 1 38.82-22.67l8.56-60.78A11.93 11.93 0 0 1 209.94 26h92.12a12 12 0 0 1 11.51 9.53l8.56 60.78A169.3 169.3 0 0 1 361 119l57.2-23a11.64 11.64 0 0 1 14.14 4.92l46.06 79.52a11.59 11.59 0 0 1-2.63 14.78l-48.69 38.12a174.58 174.58 0 0 1 1.64 22.66"
            />
          </svg>
          <span class="mntext">Manage Status</span>
        </button>
      </div>

      <div class="btn-a">
        <button
          class="flex items-center bg-white text-gray-800 px-4 py-2 rounded-lg shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
          @click="openModalAdd"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            fill="currentColor"
            class="mr-2"
            viewBox="0 0 16 16"
          >
            <path
              d="M8 0a8 8 0 1 1 0 16A8 8 0 0 1 8 0Zm3.5 7.5H8.5v-3a.5.5 0 1 0-1 0v3h-3a.5.5 0 1 0 0 1h3v3a.5.5 0 1 0 1 0v-3h3a.5.5 0 1 0 0-1Z"
            />
          </svg>
          Add Task
        </button>
      </div>

      <div class="tb">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Title</th>
              <th>Assignees</th>
              <th class="sortstt flex justify-center">
                Status
                <button class="itbkk-status-sort" @click="toggleSortOrder">
                  <svg
                    v-if="sortAscending"
                    xmlns="http://www.w3.org/2000/svg"
                    width="1.3em"
                    height="1.3em"
                    viewBox="0 0 24 24"
                  >
                    <path
                      fill="none"
                      stroke="currentColor"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="m3 16l4 4l4-4m-4 4V4m13 4h-5m0 2V6.5a2.5 2.5 0 0 1 5 0V10m-5 4h5l-5 6h5"
                    ></path>
                  </svg>
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    width="1.3em"
                    height="1.3em"
                    viewBox="0 0 24 24"
                  >
                    <path
                      fill="none"
                      stroke="currentColor"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="m3 8l4-4l4 4M7 4v16M20 8h-5m0 2V6.5a2.5 2.5 0 0 1 5 0V10m-5 4h5l-5 6h5"
                    ></path>
                  </svg>
                </button>
              </th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(task, index) in sortedTasks" :key="task.id">
              <td>{{ index + 1 }}</td>
              <td @click="openModalDetail(task.id)">{{ task.title }}</td>
              <td>{{ task.assignees || "Unassigned" }}</td>
              <td><Badges :status="task.status" /></td>
              <td>
                <button @click="openModalEdit(task.id)">Edit</button>
                <button @click="openModalDelete(task.id, task.title, index)">
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <ModalDelete
        v-if="showDelete"
        :taskId="selectDeleteTaskId"
        :title="selectDeleteTaskTitle"
        :indexs="selectDeleteTaskIndex"
        @closeModal="closeModal"
        @confirmDeleteModal="deleteTask"
      />
      <ToastAdd v-if="showToastAdd" @close="closeToast" />
      <ToastEdit v-if="showToastEdit" @close="closeToast" />
      <ToastDelete
        v-if="showToastDelete"
        :isDeleteSuccess="deleteSuccess"
        @close="closeToastDelete"
      />
    </main>
    <footer>
      <Copyright />
    </footer>
    <Bg_effect />
  </div>
  <router-view />
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");
* {
  user-select: none;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  scroll-behavior: smooth;
}

h3 {
  font-family: "Ubuntu", sans-serif;
  font-weight: 400;
  font-style: normal;
  font-size: 30px;
  color: #737292;
  text-align: center;
  justify-content: center;
}

/* .itbkk-fullname {
  box-shadow: none;
} */
.itbkk-status-sort {
  box-shadow: none;
  padding-left: 10px;
}

.itbkk-status-sort svg {
  transition: transform 0.3s ease;
  cursor: pointer;
}
.a {
  color: rgb(250, 133, 188);
}
.b {
  color: rgb(250, 133, 188);
}
.c {
  color: rgb(209, 209, 209);
}
.b:hover,
.a:hover {
  color: rgb(214, 103, 177);
}

.c:hover {
  color: rgb(216, 216, 216);
}
.mntext {
  padding-top: 5px;
  padding-left: 5px;
  padding-right: 5px;
}
.manageStatus {
  display: flex;
  width: 100%;
  max-width: 900px;
  padding-left: 2rem;
}
.itbkk-manage-status {
  font-style: italic;
  background-color: #343365;
  color: #fff7d9;
  background-color: #6f418f;
  color: #f5e2ff;
  padding: 10px;
  border-radius: 50px;
  transition: transform 0.3s ease;
}
.itbkk-manage-status:hover {
  background-color: #6a4581;
  background-color: #8e5bb1;
}
.unassigned {
  font-style: italic;
  color: gray;
}
.addtask {
  margin-right: 45rem;
  text-align: center;
}
.plus {
  font-size: 40px;
  font-weight: 10px;
  color: #d1ffca;
}
.itbkk-button-action {
  padding: 15px 5px 10px;
  display: flex;
}
.itbkk-button-action svg {
  transition: transform 0.3s ease;
  cursor: pointer;
}
.itbkk-button-action svg:hover {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  color: #ad386f;
}
.btn-a {
  position: fixed;
  bottom: 50px;
  right: 50px;
  cursor: pointer;
  z-index: 2;
}
.iconbutton {
  width: 50px;
  height: 50px;
  border-radius: 100%;
  box-shadow: 5px 5px 5px #49494946;
}
.itbkk-button-add {
  width: 80px;
  height: 80px;
  background: #ad386f;
}
.iconbutton i {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: white;
}
.button:hover {
  background-color: #66163b;
  color: white;
}
.itbkk-assignees {
  font-style: normal;
  width: auto;
}
.itbkk-title p {
  transition: transform 0.3s ease;
  color: #ad386f;
}
.itbkk-title p:hover {
  cursor: pointer;
  transform: scale(1.01);
  color: rgb(250, 133, 188);
}
.itbkk-item p {
  width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.unassigned {
  color: gray;
  font-style: italic;
}
header {
  margin-bottom: 20px;
}

.addtask {
  margin-right: 45rem;
  text-align: center;
}
.kebab {
  color: #939393;
}
.idkb {
  display: inline;
}
h1 {
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 40px;
  color: #47467b;
  font-weight: 800;
  margin: 4rem 0 2rem;
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
  color: #47467b;
  font-family: "Ubuntu", sans-serif;
  font-weight: 600;
  font-style: normal;
}
td.bgd {
  padding-left: 30px;
}
thead th {
  background-color: #47467b;
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
@media screen and (max-width: 820px) {
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
    background-color: #47467b;
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
  }
}
</style>
