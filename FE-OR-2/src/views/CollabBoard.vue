<script setup>
import { onMounted, ref, watch, computed } from "vue"
import { useRouter, useRoute } from "vue-router"
import { useBoardStore } from "@/stores/BoardStore.js"
import { storeToRefs } from "pinia"
import { useToastStore } from "@/stores/ToastStore.js"
import { useUserStore } from "@/stores/AuthUserStore.js"
import Navbar from "../components/css/Navbar.vue"
import Copyright from "../components/css/Copyright.vue"

const boardStore = useBoardStore()
const { boards } = storeToRefs(boardStore)
const toastStore = useToastStore()
const userStore = useUserStore()
const loading = ref(true)
const error = ref(false)
const isModalOpen = ref(false)
const fullname = computed(() => userStore.fullname)
const accessRight = ref("Read")

const router = useRouter()
const route = useRoute()
const boardId = route.params.boardId;
const boardName = ref("")
const boardOwnerOid = ref("")
const visibility = ref("")
const collaboratorEmail = ref("")
const deleteSuccess = ref(false)
const tooltipText = "You need to be board owner to perform this action."

const fetchBoardData = async () => {
  try {
    const boardData = await boardStore.getBoard(boardId);
    if (boardData) {
      boardName.value = boardData.name;
      boardOwnerOid.value = boardData.owner.oid;
      visibility.value = boardData.visibility || "PRIVATE";
    } else {
      console.error("Board data not found");
    }
  } catch (error) {
    console.error("Error fetching board data:", error);
  }
};

// const isValidEmail = computed(() => {
//   const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
//   return emailRegex.test(collaboratorEmail.value)
// })

const openHome = () => {
  router.push({ name: "board-tasks" })
}

const openModal = () => {
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
  router.push({ name: "collaborator-manage"})
}

watch(
  () => route.path,
  (newPath) => {
    isModalOpen.value = newPath === "/board/add"
  },
  { immediate: true }
)

// onMounted(() => {
//   initializeBoards()
// })

onMounted(async () => {
  await fetchBoardData();
  // await taskStore.fetchTasks(boardId);
})
</script>

<template>
  <Navbar />
  <div class="pt-24">
    <header class="mb-2 max-w-6xl mx-auto px-4 sm:px-6">
      <h1 class="text-left text-3xl font-bold">IT-Bangmod Kradan Kanban</h1>
      <div class="flex justify-between items-center mt-2">
        <h2 class="text-lg font-semibold pr-20">
          <span class="itbkk-boards hover:text-[#47467b] cursor-pointer" @click="router.push({ name: 'board' })">
            Board
          </span>&nbsp;>&nbsp;
          <span class="itbkk-board-name hover:text-[#5e1f87] cursor-pointer"
            @click="router.push({ name: 'board-tasks' })">
            {{ boardName }}
          </span>&nbsp;>&nbsp;
          <span class=" text-[#ad386f] hover:text-[#a31a5a]">Collaborator</span>
        </h2>
        <div class="flex space-x-4">
          <button @click="openHome"
            class="itbkk-button-home flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300">
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="mr-2"
              viewBox="0 4 16 16">
              <path fill="currentColor"
                d="m16 8.5l1.53 1.53l-1.06 1.06L10 4.62l-6.47 6.47l-1.06-1.06L10 2.5l4 4v-2h2zm-6-2.46l6 5.99V18H4v-5.97zM12 17v-5H8v5z">
              </path>
            </svg>
            Back
          </button>

          <div class="tooltip-container">
            <button @click="openModal" :disabled="boardOwnerOid !== userStore.oid" :class="{
              'itbkk-collaborator-add flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow transition-transform duration-300': true,
              'hover:shadow-md hover:scale-105':
                boardOwnerOid === userStore.oid,
              'opacity-50 cursor-not-allowed':
                boardOwnerOid !== userStore.oid,
            }">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="mr-2"
                viewBox="0 0 16 16">
                <path fill="currentColor" fillRule="evenodd"
                  d="M8 1a1 1 0 0 0-2 0v5H1a1 1 0 0 0 0 2h5v5a1 1 0 1 0 2 0V8h5a1 1 0 1 0 0-2H8z" clipRule="evenodd" />
              </svg>
              Add collaborator
            </button>
            <span v-if="boardOwnerOid !== userStore.oid" class="tooltip-text">
              {{ tooltipText }}
            </span>
          </div>
        </div>
      </div>
    </header>
  </div>

  <main>
    <div class="p-4 bg-gradient-to-r to-pink-100 rounded-lg">
      <table class="w-full text-left table-fixed">
        <thead class="bg-violet-500">
          <tr>
            <th class="px-4 py-3 w-1/12 text-center font-semibold">No</th>
            <th class="px-4 py-3 w-2/12 text-left font-semibold">Name</th>
            <th class="px-4 py-3 w-5/12 text-left font-semibold">E-mail</th>
            <th class="px-4 py-3 w-2/12 text-left font-semibold">Access Right</th>
            <th class="px-4 py-3 w-2/12 text-center font-semibold">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="px-4 py-4 text-center text-black font-bold">
              Loading...
            </td>
          </tr>
          <tr v-else-if="error">
            <td colspan="5" class="px-4 py-4 text-center text-red-600">
              Error loading boards.
            </td>
          </tr>
          <tr v-if="boards.length === 0 && !loading && !error">
            <td colspan="5" class="px-4 py-4 text-center">No Board Added</td>
          </tr>
          <!-- <tr v-for="(board, index) in boards.personal_boards" :key="board.id" -->
          <tr v-for="(board, index) in boards" :key="board.id" class="itbkk-item border-b last:border-0">
            <td class="px-4 py-3 text-center text-black font-bold">
              {{ index + 1 }}
            </td>
            <td class="itbkk-name px-4 py-3 cursor-pointer text-black font-bold hover:text-[#47467b] break-words"
              @click="router.push(`/board/${board.id}`)">
              ITBKK DUMMY
            </td>
            <td class="itbkk-email px-4 py-3 text-[#717171] font-bold text-center truncate">
              <!-- {{ board.email }} -->
              itbkk.dummy@ad.sit.kmutt.ac.th
            </td>
            <td class="itbkk-access-right px-4 py-3 text-[#717171] font-bold text-center">
              <select
                class="itbkk-status select select-bordered w-full max-w-xs block bg-white border border-gray-200 py-3 px-4 pr-8 rounded-lg leading-tight focus:outline-none focus:bg-white">
                <option disabled selected>READ</option>
                <option>READ</option>
                <option>WRITE</option>
              </select>
            </td>
            <td class="px-4 py-3 space-x-4">
              <div class="flex justify-center">
                <button class="itbkk-button-delete bg-[#8e5bb1] hover:bg-[#6a4581]" @click="deleteBoard(board.id)">
                  Remove
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Add Collaborator Modal -->
    <div v-if="isModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div class="itbkk-modal-alert bg-white px-10 py-6 rounded-3xl text-center shadow-xlp-6 w-1/3">
        <h2 class="text-xl font-bold mb-4">Add Collaborator</h2>
        <div class="flex mb-6 w-full">
          <div class="text-left flex-grow">
            <p class="pb-1 font-medium">Collaborator e-mail</p>
            <!-- This input is always enabled -->
            <!-- <input v-model="collaboratorEmail" type="text" maxlength="50" -->
            <input type="text" maxlength="50"
              class="itbkk-collaborator-email border p-2 w-full h-12 rounded-md"
              placeholder="Enter collaborator e-mail" />

            <!-- This input is disabled based on email validity -->
            <!-- <input type="text" :disabled="!isValidEmail" class="border p-2 w-full h-12 rounded-md mt-4"
              placeholder="Disabled until a valid email is entered" /> -->
          </div>
          <div class="text-left pl-8">
            <p class="pb-1 font-medium">Access Right</p>
            <select
              class="itbkk-access-right select select-bordered w-32 bg-white border border-gray-200 rounded-md focus:outline-none focus:bg-white">
              <option disabled selected>READ</option>
              <option>READ</option>
              <option>WRITE</option>
            </select>
          </div>
        </div>

        <div class="button-addboard flex justify-center gap-4">
          <button @click="addCollab" :disabled="!boardName" :class="{
            'bg-gray-300 text-gray-500 cursor-not-allowed': !boardName,
            'bg-emerald-500 hover:bg-emerald-600': boardName
          }" class="itbkk-button-confirm text-white px-4 py-2">
            Add
          </button>
          <button @click="closeModal"
            class="itbkk-button-cancel bg-gray-300 hover:bg-gray-400 text-gray-500 px-4 py-2 ml-4">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </main>
  <footer>
    <Copyright />
  </footer>
</template>

<style scoped>
h1 {
  font-family: "Ubuntu", sans-serif;
  color: black;
}

h2 {
  color: black;
}

.tooltip-container {
  position: relative;
}

.tooltip-text {
  width: 190px;
  background-color: #9ca3af;
  color: #fff;
  font-size: 0.875rem;
  line-height: 1.25rem;
  font-style: italic;
  font-weight: 400;
  text-align: center;
  border-radius: 6px;
  padding: 5px 5px;
  position: absolute;
  z-index: 1;
  bottom: 115%;
  left: 50%;
  margin-left: -95px;
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.tooltip-container:hover .tooltip-text {
  opacity: 1;
}

.itbkk-button-home {
  font-style: italic;
  background-color: rgba(255, 255, 255, 0);
  color: #47467b;
  border: 2px solid #47467b;
  border-color: #47467b;
  transition: background-color 0.5s ease,
    transform 150ms cubic-bezier(0.4, 0, 0.2, 1);
}
.itbkk-button-home:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.itbkk-collaborator-add {
  background-color: #47467b;
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

.itbkk-modal-alert button {
  border-radius: 20px;
  transition: transform 0.3s ease-in-out;
}

.itbkk-modal-alert button:hover:not(:disabled) {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  box-shadow: 5px 5px 5px #c6c6c646;
}

.text-purple {
  color: #47467b;
}

.bg-color-purple {
  background-color: #47467b;
}

.bg-color-purple:hover {
  background-color: #636296;
}

.itbkk-boards:hover {
  color: #47467b;
}
table {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  border-collapse: separate; /* ป้องกันมุมโค้งที่ทับกัน */
  border-spacing: 0;
  background-color: rgba(255, 254, 255, 0.329);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); /* เพิ่มเงา */
  border-radius: 25px;
  overflow: hidden;
}

th,
td {
  padding: 15px; /* เพิ่ม padding เล็กน้อยให้ดูโล่งขึ้น */
  background-color: rgba(255, 254, 255, 0.364); /* ให้พื้นหลังดูสว่าง */
}

tbody tr:hover {
  background-color: rgba(255, 255, 255, 0.63);
}

th {
  background-color: #47467b; /* พื้นหลังหัวข้อสีโทนหรู */
  color: #ffff;
  font-weight: 700;
  text-align: center;
}

tbody tr {
  transition: background-color 0.3s ease;
}

tbody tr:last-child td {
  border-bottom: none;
}

/* มุมโค้งสำหรับหัวตาราง */
thead th:first-child {
  border-top-left-radius: 15px;
}

thead th:last-child {
  border-top-right-radius: 15px;
}

/* มุมโค้งสำหรับท้ายตาราง */
tbody tr:last-child td:first-child {
  border-bottom-left-radius: 15px;
}

tbody tr:last-child td:last-child {
  border-bottom-right-radius: 15px;
}

.itbkk-button-delete,
.itbkk-leave-board {
  padding: 10px;
  color: white;
  border-radius: 8px;
  transition: transform 0.3s ease, background-color 0.3s ease;
}

.itbkk-button-delete:hover,
.itbkk-leave-board:hover {
  transform: scale(1.05); /* ขยายปุ่มเล็กน้อยเมื่อ hover */
}

.itbkk-button-delete svg,
.itbkk-leave-board svg {
  margin-right: 8px; /* เพิ่มระยะห่างระหว่างไอคอนกับข้อความ */
}

.itbkk-button-delete span,
.itbkk-leave-board span {
  font-size: 14px;
  font-weight: 500;
}
</style>
