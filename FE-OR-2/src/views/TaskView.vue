<script setup>
// Import zone
import { onMounted, ref, watch, computed } from "vue"
import { useRouter, useRoute } from "vue-router"
import { storeToRefs } from "pinia"
import { useBoardStore, useTaskStore } from "@/stores/apiController.js"
import { useToastStore } from "@/stores/toast.js"
import { useUserStore } from "@/stores/authUser.js"
import Bg_effect from "../components/css/Background.vue"
import Badges from "../components/css/Badge.vue"
import Copyright from "../components/css/Copyright.vue"
import ToastAdd from "../components/toast/ToastAdd.vue"
import ToastEdit from "../components/toast/ToastEdit.vue"
import ModalDelete from "../components/ModalDeleteTask.vue"
import ToastDelete from "../components/toast/ToastDelete.vue"
import Navbar from "../components/css/Navbar.vue"
import ModalVisibilityBoard from "../components/ModalVisibilityBoard.vue"

// Store,Api zone
const baseUrl = import.meta.env.VITE_API_BOARDS
const router = useRouter()
const route = useRoute()
const store = useTaskStore()
const toastStore = useToastStore()
const { showToastAdd, showToastEdit, closeToast } = storeToRefs(toastStore)

// Variable zone
const boardId = route.params.boardId // Get boardId from the URL
const taskId = route.params.id
const selectDeleteTaskId = ref("")
const selectDeleteTaskTitle = ref("")
const selectDeleteTaskIndex = ref("")
const originalTasks = ref([])
const userStore = useUserStore()
const boardStore = useBoardStore()
const boardName = ref("")
const boardOwnerOid = ref("")

const visibility = ref("PRIVATE")
const isModalOpen = ref(false)
const modalTitle = ref("Board Visibility Changed!")
const modalMessage = ref(
  "Do you want to change board visibility to public?"
)

const openModal = () => {
  modalMessage.value =
    visibility.value === "PRIVATE"
      ? "Do you want to change board visibility to public?"
      : "Do you want to change board visibility to private?"
  isModalOpen.value = true
}

const confirmChange = async () => {
  const newVisibility = visibility.value === "PRIVATE" ? "PUBLIC" : "PRIVATE"
  await boardStore.updateVisibility(boardId, newVisibility)
  visibility.value = newVisibility
  isModalOpen.value = false
}



// Function zone
onMounted(async () => {
  try {
    await fetchBoard()
    const data = await fetchData()
    const datastatus = await fetchDataStatus() // Fetch status data
    originalTasks.value = [...data]
    store.tasks.value = [...data]
    store.status.value = datastatus
  } catch (error) {
    console.error("Error during data fetch:", error)
  }
})

async function fetchWithToken(url, options = {}) {
  const token = localStorage.getItem("jwtToken");
  if (!token) {
    console.warn("No token found.");
    router.push("/login");
    return null;
  }

  const headers = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`, // เพิ่ม Token ใน Header
    ...options.headers,
  };

  const response = await fetch(url, { ...options, headers });

  if (response.status === 401) {
    console.warn("Unauthorized. Token expired or invalid.");
    userStore.logout();
    router.push("/login");
    return null;
  }

  if (response.status === 403) {
    console.warn("Access Denied (403).");
    router.push({ name: "AccessDenied" });
    return null;
  }

  return response;
}


async function fetchData() {
  // Fetch tasks for the specified board
  const response = await fetchWithToken(`${baseUrl}/v3/boards/${boardId}/tasks`)
  if (!response) return // If response is null, return

  if (response.status === 404) {
    router.push({ name: "NotFound" })
    return
  }

  if (!response.ok) {
    throw new Error(`Error fetching tasks: ${response.statusText}`)
  }

  const data = await response.json()
  if (!Array.isArray(data)) {
    throw new TypeError("Fetched data is not an array")
  }

  return data
}

async function fetchDataStatus() {
  // Fetch statuses for the specified board
  const response = await fetchWithToken(
    `${baseUrl}/v3/boards/${boardId}/statuses`
  )
  if (!response) return // If response is null, return
  
  

  if (response.status === 404) {
    router.push({ name: "NotFound" }) // เปลี่ยนเส้นทางไปที่หน้า NotFound ถ้าพบ 404
    return
  }

  if (!response.ok) {
    throw new Error(`Error fetching status: ${response.statusText}`)
  }

  const dataStatus = await response.json()
  if (!Array.isArray(dataStatus)) {
    throw new TypeError("Fetched status data is not an array")
  }
  
  return dataStatus
}

async function fetchBoard() {
  try {
    const response = await fetch(`${baseUrl}/v3/boards/${boardId}`);

    if (response.status === 403) {
      console.warn("Access denied to this board.");
      router.push({ name: "AccessDenied" }); // ไปหน้า Access Denied (403)
      return;
    }

    if (response.status === 404) {
      console.warn("Board not found.");
      router.push({ name: "NotFound" }); // ไปหน้า Not Found (404)
      return;
    }

    if (!response.ok) {
      throw new Error(`Error fetching board: ${response.statusText}`);
    }

    const data = await response.json();
    boardName.value = data.name;
    boardOwnerOid.value = data.owner.oid;
    visibility.value = data.visibility;

    // หากบอร์ดเป็น public ไม่ต้องใช้ token
    if (visibility.value === "PUBLIC") {
      console.log("Board is public.");
      return;
    }

  } catch (error) {
    console.error("Error fetching board:", error);
    router.push({ name: "NotFound" }); // เปลี่ยนไปหน้า NotFound ในกรณี Error อื่น
  }
}



// Functions for managing tasks
const openModalAdd = async () => {
  router.push({ name: "task-add" })
}
const openModalDetail = async (id) => {
  if (id) {
    router.push({ name: "task-detail", params: { taskId: id, boardId } })
  } else {
    console.error("Task ID is missing")
  }
}

const openModalEdit = async (id) => {
  if (id) {
    router.push({ name: "task-edit", params: { taskId: id, boardId } })
  } else {
    console.error("Task ID is missing")
  }
}
if (window.location.pathname === `/task/${taskId}`) {
  openModalDetail(taskId)
} else if (window.location.pathname === `/task/${taskId}/edit`) {
  openModalEdit(taskId)
}

const showToastDelete = ref(false)
const deleteSuccess = ref(false)
const showDelete = ref(false)
const toastSuccess = ref(false)

const openModalDelete = (taskId, title, index) => {
  showDelete.value = true
  selectDeleteTaskId.value = taskId
  selectDeleteTaskTitle.value = title
  selectDeleteTaskIndex.value = index
}

async function deleteTask(taskId) {
  const response = await fetchWithToken(
    `${baseUrl}/v3/boards/${boardId}/tasks/${taskId}`,
    {
      method: "DELETE",
    }
  )

  if (!response) return
  if (response.status === 404) {
    router.push({ name: "NotFound" })
    return
  }

  if (response.ok) {
    originalTasks.value = originalTasks.value.filter(
      (task) => task.id !== taskId
    )
    store.tasks.value = [...originalTasks.value]
    deleteSuccess.value = true
  } else {
    deleteSuccess.value = false
    console.error("Failed to delete task")
  }

  confirmDelete()
  showDelete.value = false
}

const confirmDelete = async () => {
  try {
    showToastDelete.value = true
    toastSuccess.value = true
    setTimeout(() => {
      showToastDelete.value = false
    }, 5000)
  } catch (error) {
    toastSuccess.value = false
    setTimeout(() => {
      showToastDelete.value = false
    }, 5000)
    console.error("Error deleting task:", error)
  }
  return {
    showToastDelete,
    closeModal,
    confirmDelete,
  }
}
const closeToastDelete = () => {
  showToastDelete.value = false
}
const closeModal = () => {
  showDelete.value = false
  isModalOpen.value = false
  router.push({ path: `/board/${boardId}` })
}

// Sorting function
const sortAscending = ref(undefined)

const toggleSortOrder = () => {
  if (sortAscending.value === undefined) {
    sortAscending.value = true
  } else if (sortAscending.value === true) {
    sortAscending.value = false
  } else {
    sortAscending.value = undefined
  }
}

const sortedTasks = computed(() => {
  if (sortAscending.value === undefined) {
    return originalTasks.value
  }
  return sortTasks([...originalTasks.value], sortAscending.value)
})

function sortTasks(tasks, ascending) {
  if (ascending === undefined) return tasks // No sorting if sort order is undefined
  return tasks.sort((a, b) => {
    const statusA = a.status.name.toLowerCase()
    const statusB = b.status.name.toLowerCase()
    if (statusA < statusB) return ascending ? -1 : 1
    if (statusA > statusB) return ascending ? 1 : -1
    return 0
  })
}

watch(
  () => sortedTasks.value,
  (newVal) => {
    store.tasks.value = newVal
  }
)
</script>

<template>
  <Navbar class="itbkk-fullname" />
  <div class="pt-24">
    <header class="mb-2 max-w-6xl mx-auto">
      <h1 class="text-left text-3xl font-bold">IT-Bangmod Kradan Kanban</h1>
      <div class="flex justify-between items-center mt-2">
        <h2 class="text-lg font-semibold pr-20">
          <span
            class="itbkk-boards cursor-pointer"
            @click="router.push({ name: 'board' })"
            >Board</span
          >&nbsp;>&nbsp;
          <span class="itbkk-board-name textPink">{{ boardName }}</span>
        </h2>
        <div class="flex space-x-4">
          <!-- ปุ่ม Manage Status -->
          <button
            @click="router.push({ name: 'status-manage' })"
            class="itbkk-manage-status flex items-center px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="currentColor"
              class="mr-2"
              width="32"
              height="32"
              viewBox="0 0 512 512"
            >
              >

              <path
                fill="currentColor"
                d="M256 176a80 80 0 1 0 80 80a80.24 80.24 0 0 0-80-80m172.72 80a165.53 165.53 0 0 1-1.64 22.34l48.69 38.12a11.59 11.59 0 0 1 2.63 14.78l-46.06 79.52a11.64 11.64 0 0 1-14.14 4.93l-57.25-23a176.56 176.56 0 0 1-38.82 22.67l-8.56 60.78a11.93 11.93 0 0 1-11.51 9.86h-92.12a12 12 0 0 1-11.51-9.53l-8.56-60.78A169.3 169.3 0 0 1 151.05 393L93.8 416a11.64 11.64 0 0 1-14.14-4.92L33.6 331.57a11.59 11.59 0 0 1 2.63-14.78l48.69-38.12A174.58 174.58 0 0 1 83.28 256a165.53 165.53 0 0 1 1.64-22.34l-48.69-38.12a11.59 11.59 0 0 1-2.63-14.78l46.06-79.52a11.64 11.64 0 0 1 14.14-4.93l57.25 23a176.56 176.56 0 0 1 38.82-22.67l8.56-60.78A11.93 11.93 0 0 1 209.94 26h92.12a12 12 0 0 1 11.51 9.53l8.56 60.78A169.3 169.3 0 0 1 361 119l57.2-23a11.64 11.64 0 0 1 14.14 4.92l46.06 79.52a11.59 11.59 0 0 1-2.63 14.78l-48.69 38.12a174.58 174.58 0 0 1 1.64 22.66"
              />
            </svg>
            Manage Status
          </button>

          <button
            v-if="boardOwnerOid === userStore.oid"
            @click="openModal"
            class="itbkk-board-visibility"
            v-bind:class="{
              'itbkk-manage-privacy-private px-4 py-2 flex items-center rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300':
                visibility === 'PRIVATE',
              'itbkk-manage-privacy-public px-4 py-2 flex items-center rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300':
                visibility === 'PUBLIC',
            }"
          >
            <template v-if="visibility === 'PRIVATE'">
              <!-- Lock Icon for Private -->
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  d="M7 10.0288C7.47142 10 8.05259 10 8.8 10H15.2C15.9474 10 16.5286 10 17 10.0288M7 10.0288C6.41168 10.0647 5.99429 10.1455 5.63803 10.327C5.07354 10.6146 4.6146 11.0735 4.32698 11.638C4 12.2798 4 13.1198 4 14.8V16.2C4 17.8802 4 18.7202 4.32698 19.362C4.6146 19.9265 5.07354 20.3854 5.63803 20.673C6.27976 21 7.11984 21 8.8 21H15.2C16.8802 21 17.7202 21 18.362 20.673C18.9265 20.3854 19.3854 19.9265 19.673 19.362C20 18.7202 20 17.8802 20 16.2V14.8C20 13.1198 20 12.2798 19.673 11.638C19.3854 11.0735 18.9265 10.6146 18.362 10.327C18.0057 10.1455 17.5883 10.0647 17 10.0288M7 10.0288V8C7 5.23858 9.23858 3 12 3C14.7614 3 17 5.23858 17 8V10.0288"
                  stroke="#ad386f"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              Private
            </template>

            <template v-if="visibility === 'PUBLIC'">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  d="M3 12H21M3 12C3 16.9706 7.02944 21 12 21M3 12C3 7.02944 7.02944 3 12 3M21 12C21 16.9706 16.9706 21 12 21M21 12C21 7.02944 16.9706 3 12 3M12 21C4.75561 13.08 8.98151 5.7 12 3M12 21C19.2444 13.08 15.0185 5.7 12 3"
                  stroke="#ad386f"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              Public
            </template>
          </button>

          <!-- ปุ่ม Add Task -->
          <button
            @click="openModalAdd"
            class="itbkk-button-add flex items-center px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
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
            Add Task
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
              <th>Title</th>
              <th>Assignees</th>
              <th class="sortstt flex justify-left">
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
              <th class="text-center" style="text-align: center">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr
              class="itbkk-item"
              v-for="(task, index) in sortedTasks"
              :key="task.id"
            >
              <td>{{ index + 1 }}</td>
              <td class="itbkk-title" @click="openModalDetail(task.id)">
                {{ task.title }}
              </td>
              <td class="itbkk-assignees">
                <span :class="{ 'italic': !task.assignees }">
                  {{ task.assignees || "Unassigned" }}
                </span>
              </td>
              <td class="itbkk-status">
                <Badges style="justify-content: center" :status="task.status" />
              </td>

              <td class="itbkk-button-action px-4 py-3 flex justify-center space-x-2">
                <!-- จัดปุ่มให้ดูสมส่วน -->
                <button
                  class="flex items-center bg-white text-color-purple px-3 py-1.5 rounded-lg shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
                  @click="openModalEdit(task.id)"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    fill="currentColor"
                    class="mr-2"
                    viewBox="0 0 24 24"
                  >
                    <path
                      fill="currentColor"
                      fill-rule="evenodd"
                      d="M17.907 4.93c.104-.105.294-.186.539-.18a.848.848 0 0 1 .572.216c.13.13.222.34.231.575c.01.234-.065.413-.167.515l-7.49 7.5l-1.273.116l.1-1.245zm2.173-1.024c-.437-.438-1.037-.643-1.6-.656c-.564-.013-1.182.167-1.635.62l-7.682 7.692a.75.75 0 0 0-.217.47l-.194 2.407a.75.75 0 0 0 .816.807l2.43-.22a.75.75 0 0 0 .463-.218l7.682-7.692c.456-.456.627-1.073.605-1.635a2.373 2.373 0 0 0-.668-1.575M5.812 4.75a2.568 2.568 0 0 0-2.563 2.562v10.875a2.568 2.568 0 0 0 2.563 2.563h10.875a2.568 2.568 0 0 0 2.563-2.562V12.75a.75.75 0 0 0-1.5 0v5.438c0 .41-.48 1.062-1.063 1.062H5.812c-.583 0-1.063-.48-1.063-1.062V7.312c0-.582.48-1.062 1.063-1.062h5.437a.75.75 0 0 0 0-1.5z"
                      clip-rule="evenodd"
                    />
                  </svg>
                  <span>Edit</span>
                </button>

                <button
                  class="flex items-center bg-white text-color-purple px-3 py-1.5 rounded-lg shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
                  @click="openModalDelete(task.id, task.title, index)"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    fill="currentColor"
                    class="mr-2"
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
    <ModalVisibilityBoard
      :isOpen="isModalOpen"
      :title="modalTitle"
      :message="modalMessage"
      @confirm="confirmChange"
      @close="closeModal"
    />
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

h1 {
  font-family: "Ubuntu", sans-serif;
  color: black;
}

h2 {
  color: black;
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

.itbkk-manage-status {
  font-style: italic;
  background-color: rgba(255, 255, 255, 0);
  color: #ad386f;
  border: 2px solid #ad386f;
  transition: background-color 0.5s ease,
    transform 150ms cubic-bezier(0.4, 0, 0.2, 1);
}

.itbkk-manage-status:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.itbkk-manage-privacy-public {
  font-style: italic;
  background-color: rgba(255, 255, 255, 0);
  color: #ad386f;
  border: 2px solid #ad386f;
  border-color: #ad386f;
  transition: background-color 0.5s ease,
    transform 150ms cubic-bezier(0.4, 0, 0.2, 1);
}

.itbkk-manage-privacy-public:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.itbkk-manage-privacy-private {
  font-style: italic;
  background-color: rgba(255, 255, 255, 0);
  color: #ad386f;
  border: 2px solid #ad386f;
  border-color: #ad386f;
  transition: background-color 0.5s ease,
    transform 150ms cubic-bezier(0.4, 0, 0.2, 1);
}

.itbkk-manage-privacy-private:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.itbkk-button-add {
  font-style: italic;
  background-color: #ad386f;
  color: #f5e2ff;
  transition: transform 0.3s ease;
}

.itbkk-boards:hover {
  color: #47467b;
}

.itbkk-status-sort {
  box-shadow: none;
  padding-left: 10px;
}

.itbkk-status-sort svg {
  transition: transform 0.3s ease;
  cursor: pointer;
}

.textPink {
  color: #ad386f;
}

.textPink:hover {
  color: #a31a5a;
}

.textGray {
  color: #717171;
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

.unassigned {
  font-style: italic;
  color: gray;
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

.iconbutton {
  width: 50px;
  height: 50px;
  border-radius: 100%;
  box-shadow: 5px 5px 5px #49494946;
}

.iconbutton i {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
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

.kebab {
  color: #939393;
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
  text-align: left;
}

td.bgd {
  padding-left: 30px;
}

thead th {
  background-color: #ad386f;
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

.itbkk-board-name,
.itbkk-manage-status,
.itbkk-board-visibility {
  user-select: none;
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
