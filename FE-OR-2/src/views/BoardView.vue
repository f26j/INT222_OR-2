<script setup>
import { onMounted, ref, watch, computed } from "vue"
import { useRouter, useRoute } from "vue-router"
import { useBoardStore } from "@/stores/BoardStore.js"
import { storeToRefs } from "pinia"
import { useToastStore } from "@/stores/ToastStore.js"
// import { useUserStore } from "@/stores/AuthUserStore.js"
import Navbar from "../components/css/Navbar.vue"
import Copyright from "../components/css/Copyright.vue"
import ModalAddBoard from "../components/modal/ModalAddBoard.vue"

const boardStore = useBoardStore()
const { boards } = storeToRefs(boardStore)
const toastStore = useToastStore()
// const userStore = useUserStore()
const loading = ref(true)
const error = ref(false)
const isModalOpen = ref(false)
// const fullname = computed(() => userStore.fullname)
// const accessRight = ref("Read")

const router = useRouter()
const route = useRoute()

async function initializeBoards() {
  try {
    loading.value = true
    await boardStore.fetchBoards()
  } catch (err) {
    console.error("Error fetching boards:", err)

    if (err.response && err.response.status === 401) {
      // ถ้าเป็น 401 ให้ไปหน้า login
      router.push({ name: "login" })
    } else {
      error.value = true
    }
  } finally {
    loading.value = false
  }
}

const deleteBoard = async (boardId) => {
  try {
    await boardStore.deleteBoard(boardId)
    toastStore.addToast("Board deleted successfully.", "success")
  } catch (error) {
    console.error("Error deleting board:", error)

    if (error.response && error.response.status === 401) {
      // ถ้าเป็น 401 ให้ไปหน้า login
      router.push({ name: "login" })
    } else {
      toastStore.addToast(
        "You do not have permission to delete this board.",
        "error"
      )
      alert("You do not have permission to delete this board.")
    }
  }
}

const closeModal = () => {
  isModalOpen.value = false
  router.push("/board")
}

watch(
  () => route.path,
  (newPath) => {
    isModalOpen.value = newPath === "/board/add"
  },
  { immediate: true }
)

onMounted(() => {
  initializeBoards()
})
</script>

<template>
  <Navbar />
  <div class="pt-24">
    <header class="mb-2 max-w-6xl mx-auto px-4 sm:px-6">
      <h1 class="text-left text-3xl font-bold">IT-Bangmod Kradan Kanban</h1>
    </header>
  </div>

  <main>
    <!-- Personal Boards -->
    <div class="p-4 bg-gradient-to-r to-pink-100 rounded-lg">
      <div class="relative flex justify-center items-center pb-6 mx-auto max-w-[900px]">
        <h2 class="itbkk-personal-board text-purple text-center text-2xl font-bold">
          Personal Boards
        </h2>
        <div class="absolute right-0">
          <button @click="router.push('/board/add')"
            class="itbkk-button-create flex items-center cursor-pointer bg-color-purple text-white px-4 py-2 shadow hover:shadow-md hover:scale-105 transition-transform duration-300 rounded-full">
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="mr-2"
              viewBox="0 0 16 16">
              <path d="M8 1a1 1 0 0 0-2 0v5H1a1 1 0 0 0 0 2h5v5a1 1 0 1 0 2 0V8h5a1 1 0 1 0 0-2H8z" />
            </svg>
            Create new board
          </button>
        </div>
      </div>
      <table class="w-full text-left table-fixed">
        <thead class="bg-violet-500">
          <tr>
            <th class="px-4 py-3 w-1/12 text-center font-semibold">No</th>
            <th class="px-4 py-3 w-5/12 text-left font-semibold">Name</th>
            <th class="px-4 py-3 w-4/12 text-left font-semibold">Visibility</th>
            <th class="px-4 py-3 w-2/12 text-center font-semibold">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="4" class="px-4 py-4 text-center text-black font-bold">
              Loading...
            </td>
          </tr>
          <tr v-else-if="error">
            <td colspan="4" class="px-4 py-4 text-center text-red-600">
              Error loading boards.
            </td>
          </tr>
          <tr v-if="boards.length === 0 && !loading && !error">
            <td colspan="4" class="px-4 py-4 text-center">No Board Added</td>
          </tr>
          <!-- <tr v-for="(board, index) in boards" :key="board.id" -->
          <tr v-for="(board, index) in boards.personal_boards" :key="board.id"
            class="itbkk-personal-item border-b last:border-0">
            <td class="px-4 py-3 text-center text-black font-bold">
              {{ index + 1 }}
            </td>
            <td class="itbkk-board-name px-4 py-3 cursor-pointer text-black font-bold hover:text-[#47467b] break-words"
              @click="router.push(`/board/${board.id}`)">
              {{ board.name }}
            </td>
            <td class="itbkk-board-visibility px-4 py-3 text-[#717171] font-bold text-center">
              {{ board.visibility }}
            </td>
            <td class="px-4 py-3 space-x-4">
              <div class="flex justify-center">
                <button class="itbkk-button-delete bg-[#8e5bb1] hover:bg-[#6a4581]" @click="deleteBoard(board.id)">
                  Delete
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Collab Boards -->
    <div class="mt-12 p-4">
      <h2 class="itbkk-collab-board text-purple text-center text-2xl font-bold pb-6">
        Collab Boards
      </h2>
      <table class="w-full text-left table-fixed">
        <thead class="bg-violet-500">
          <tr>
            <th class="px-4 py-3 w-1/12 text-center font-semibold">No</th>
            <th class="px-4 py-3 w-5/12 text-left font-semibold">Name</th>
            <th class="px-4 py-3 w-3/12 text-left font-semibold">Owner</th>
            <th class="px-4 py-3 w-2/12 text-center font-semibold">
              Access Right
            </th>
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
          <tr v-for="(board, index) in boards.collaborative_boards" :key="board.id"
            class="itbkk-collab-item border-b last:border-0">
            <td class="px-4 py-3 text-center text-black font-bold">
              {{ index + 1 }}
            </td>
            <td class="itbkk-board-name px-4 py-3 cursor-pointer text-black font-bold hover:text-[#47467b] break-words"
              @click="router.push(`/board/${board.id}`)">
              {{ board.name }}
            </td>
            <td class="itbkk-owner-name px-4 py-3 text-[#717171] font-bold text-center">
              {{ fullname }}
              <!-- board.owner.name -->
            </td>
            <td class="itbkk-access-right px-4 py-3 text-[#717171] font-bold text-center">
              {{ accessRight }}
              <!-- access.right -->
            </td>
            <td class="px-4 py-3 space-x-4">
              <div class="flex justify-center">
                <button class="itbkk-leave-board bg-[#ad386f] hover:bg-[#a31a5a]" @click="deleteBoard(board.id)">
                  Leave
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </main>
  <footer>
    <Copyright />
  </footer>
  <ModalAddBoard v-if="isModalOpen" @close="closeModal" />
</template>

<style scoped>
h1 {
  font-family: "Ubuntu", sans-serif;
  color: black;
}

h2 {
  color: black;
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
