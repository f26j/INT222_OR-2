<script setup>
// Import zone
import { onMounted, ref} from "vue"
import { useRouter, useRoute } from "vue-router"
import { storeToRefs } from 'pinia'
import { useCounterStore } from "@/stores/counter.js"
import { useToastStore } from "@/stores/toast.js"
import Bg_effect from "../components/css/Background.vue"
import Badges from "../components/css/Badge.vue"
import Copyright from "../components/css/Copyright.vue"
import ToastAdd from "../components/toast/ToastAdd.vue"
import ToastEdit from "../components/toast/ToastEdit.vue"
import ModalDelete from "../components/ModalDelete.vue"
import ToastDelete from "../components/toast/ToastDelete.vue"

// Store,Api zone
const fetchTask = import.meta.env.VITE_API_TASK
const fetchStatus = import.meta.env.VITE_API_STATUS
const router = useRouter()
const route = useRoute()
const store = useCounterStore()
const toastStore = useToastStore()
const { showToastAdd ,showToastEdit, closeToast } = storeToRefs(toastStore)


// Variable zone
const title = ref("")
const description = ref("")
const assignees = ref("")
const status = ref("")
const createdOn = ref("")
const updatedOn = ref("")
const taskId = route.params.id
const localTimeZone = ref(Intl.DateTimeFormat().resolvedOptions().timeZone)
const selectDeleteTaskId = ref("")
const selectDeleteTaskTitle = ref("")
const selectDeleteTaskIndex = ref("")

// Function zone
function formatDate(date) {
  const d = new Date(date)
  return d
    .toLocaleString("en-GB", { timeZone: localTimeZone.value })
    .split(",")
    .join(" ")
}

async function fetchData() {
  const response = await fetch(fetchTask)
  const data = await response.json()
  return data
}
async function fetchDataStatus() {
  const response = await fetch(fetchStatus)
  const dataStatus = await response.json()
  return dataStatus
}
onMounted(async () => {
  const data = await fetchData()
  const datastatus = await fetchDataStatus()
  store.tasks.value = data
  store.status.value = datastatus
})
const showDetail = ref(false)
const openModalDetail = async (id) => {
  const data = await fetch(`${fetchTask}/${id}`)
  const res = await data.json()
  title.value = res.title
  description.value = res.description
  assignees.value = res.assignees
  status.value = res.status
  createdOn.value = formatDate(res.createdOn)
  updatedOn.value = formatDate(res.updatedOn)
  showDetail.value = true
  router.push({ name: "task-detail", params: { id: id } })
}

const showAdd = ref(false)
const openModalAdd = async (task) => {

  router.push({ name: "task-add" })
}


const showEdit = ref(false)
const openModalEdit = async (id, edit) => {
  const data = await fetch(`${fetchTask}/${id}`)
  const res = await data.json()
  if (!data.ok) {
    router.push("/")
    setTimeout(() => {
      alert("The requested task does not exist")
    }, 500)
    return
  }
  title.value = res.title
  description.value = res.description
  assignees.value = res.assignees
  status.value = res.status
  createdOn.value = formatDate(res.createdOn)
  updatedOn.value = formatDate(res.updatedOn)
  showDetail.value = true
  router.push({ name: "task-edit", params: { id: id } })
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
  try {
    const response = await fetch(`${fetchTask}/${taskId}`, {
      method: "DELETE",
    })
    if (response.ok) {
      store.tasks.value = store.tasks.value.filter((task) => task.id !== taskId)
      deleteSuccess.value = true
    } else {
      deleteSuccess.value = false
      console.error("Failed to delete task")
    }
  } catch (error) {
    deleteSuccess.value = false
    console.error("Error deleting task:", error)
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
  showDetail.value = false;
  showAdd.value = false;
  showEdit.value = false;
  showDelete.value = false;
  router.push({ path: "/task" });
};

const openStatus = () => {
  router.push({name: "statusView"})
}
</script>
<template>
  <div class="body">
    <header>
      <hr class="my-6 sm:mx-auto border border-white/20 lg:my-8" />
      <h1 class="text-center">IT-Bangmod Kradan Kanban</h1>
      <hr class="my-6 sm:mx-auto border border-white/20 lg:my-8" />
    </header>
    <main class="flex flex-col items-center">
      <div class="manageStatus">
        <button type="button" class="itbkk-manage-status flex" @click="openStatus">
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

      <div class="btn-a" v-for="task in store.tasks.value">
        <div
          class="itbkk-button-add button iconbutton"
          @click="openModalAdd(task)"
        >
          <i class="plus">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="1em"
              height="1em"
              viewBox="0 0 24 24"
            >
              <g fill="none">
                <path
                  d="M24 0v24H0V0zM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z"
                ></path>
                <path
                  fill="currentColor"
                  d="M10.5 20a1.5 1.5 0 0 0 3 0v-6.5H20a1.5 1.5 0 0 0 0-3h-6.5V4a1.5 1.5 0 0 0-3 0v6.5H4a1.5 1.5 0 0 0 0 3h6.5z"
                ></path>
              </g>
            </svg>
          </i>
        </div>
      </div>
      <div class="tb">
        <table>
          <thead>
            <tr>
              <th></th>
              <th>Title</th>
              <th>Assignees</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(task, index) in store.tasks.value"
              :key="task.id"
              class="itbkk-item"
              :data-task-id="task.id"
            >
              <td class="bgd">
                <span class="bgi">id : </span>
                <div class="idkb flex-">
                  {{ index + 1 }}
                </div>
              </td>
              <td class="itbkk-title">
                <p @click="openModalDetail(task.id)" value="id">
                  {{ task.title }}
                </p>
              </td>
              <td class="itbkk-assignees">
                <span
                  v-if="!task.assignees?.trim()"
                  class="itbkk-assignees unassigned"
                >
                  Unassigned
                </span>
                <span v-else>{{ task.assignees }}</span>
              </td>
              <td class="itbkk-status" ><Badges :status="task.status" /></td>
              <td class="itbkk-button-action">
                <svg
                  class="itbkk-button-edit"
                  @click="openModalEdit(task.id)"
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
                <svg
                  class="itbkk-button-delete"
                  @click="openModalDelete(task.id, task.title, index + 1)"
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
      >
      </ModalDelete>
      <ToastAdd v-if="showToastAdd" @close="closeToast" />
      <ToastEdit v-if="showToastEdit" @close="closeToast"></ToastEdit>
      <ToastDelete
        v-if="showToastDelete"
        :isDeleteSuccess="deleteSuccess"
        @close="closeToastDelete"
      >
      </ToastDelete>
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
* {
  user-select: none;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  scroll-behavior: smooth;
}

.mntext {
  padding-top: 5px;
  padding-left: 5px;
  padding-right: 5px;
}
.manageStatus {
  /* justify-content: center; */
  /* text-align: center; */
  /* margin-left: 10rem; */
  /* padding-left: 10rem; */
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
/* .btna {
  background-color: #04aa6d76;
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  border-radius: 50px;
  border: 2px solid #04AA6D;
}
.btna:hover {
  background-color: #adcdc1;
  color: white;
} */
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