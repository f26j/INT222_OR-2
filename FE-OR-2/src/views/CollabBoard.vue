<script setup>
import { onMounted, ref, watch, computed } from "vue"
import { useRouter, useRoute } from "vue-router"
import { useBoardStore } from "@/stores/BoardStore.js"
import { storeToRefs } from "pinia"
import { useToastStore } from "@/stores/ToastStore.js"
import { useUserStore } from "@/stores/AuthUserStore.js"
import Navbar from "../components/css/Navbar.vue"
import Copyright from "../components/css/Copyright.vue"
import ModalAddBoard from "../components/modal/ModalAddBoard.vue"

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
const boardName = ref("");
const boardOwnerOid = ref("");
const visibility = ref("");
const deleteSuccess = ref(false);
const showDelete = ref(false);
const tooltipText = "You need to be board owner to perform this action.";

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

async function addCollaborator() {

  // try {
  //   const newBoard = {
  //     name: boardName.value.trim(),
  //     owner: fullname.value,
  //     visibility: visibility.value,
  //   };

  //   const createdBoard = await boardStore.createBoard(newBoard);
  //   toastStore.addToast("The board has been successfully created.", "success");
  //   console.log("Board created successfully:", createdBoard);
  //   router.push(`/board/${createdBoard.id}`);
  // } catch (error) {
  //   if (error.response && error.response.status === 401) {
  //     // ถ้าเป็น 401 ให้ไปหน้า login
  //     router.push(`/login`);
  //   } else {
  //     toastStore.addToast("Error creating board", "error");
  //     console.error("Error creating board:", error);
  //     errorMessage.value = error.message || "An unexpected error occurred.";
  //   }
  // }
}

const openHome = () => {
  router.push({ name: "board-tasks" });
};

// เปิด Modal สำหรับการเพิ่ม Status
const openModalAdd = () => {
  // router.push({ name: "status-add" });
};

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

// onMounted(() => {
//   initializeBoards()
// })

onMounted(async () => {
  await fetchBoardData();
  // await taskStore.fetchTasks(boardId);
});
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
            <button @click="addCollaborator" :disabled="boardOwnerOid !== userStore.oid" :class="{
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
      <div class="relative flex justify-center items-center pb-6 mx-auto max-w-[900px]">
        <!-- <h2 class="text-purple text-center text-3xl font-bold">
          Collaborator Management
        </h2> -->
        <!-- <div class="absolute right-0">
          <button @click="router.push('/board/add')"
            class="itbkk-button-create flex items-center cursor-pointer bg-color-purple text-white px-4 py-2 shadow hover:shadow-md hover:scale-105 transition-transform duration-300 rounded-full">
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="mr-2"
              viewBox="0 0 16 16">
              <path d="M8 1a1 1 0 0 0-2 0v5H1a1 1 0 0 0 0 2h5v5a1 1 0 1 0 2 0V8h5a1 1 0 1 0 0-2H8z" />
            </svg>
            Create new board
          </button>
        </div> -->
      </div>
      <table class="w-full text-left table-fixed">
        <thead class="bg-violet-500">
          <tr>
            <th class="px-4 py-3 w-1/12 text-center font-semibold">No</th>
            <th class="px-4 py-3 w-4/12 text-left font-semibold">Name</th>
            <th class="px-4 py-3 w-3/12 text-left font-semibold">E-mail</th>
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
          <tr v-for="(board, index) in boards.personal_boards" :key="board.id"
            class="itbkk-item border-b last:border-0">
            <td class="px-4 py-3 text-center text-black font-bold">
              {{ index + 1 }}
            </td>
            <td class="itbkk-name px-4 py-3 cursor-pointer text-black font-bold hover:text-[#47467b] break-words"
              @click="router.push(`/board/${board.id}`)">
              {{ board.name }}
            </td>
            <td class="itbkk-email px-4 py-3 text-[#717171] font-bold text-center">
              {{ board.email }}
            </td>
            <td class="itbkk-access-right px-4 py-3 text-[#717171] font-bold text-center">
              {{ board.email }}
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

    <!-- Modal Add Collaborator -->
    <div class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div class="itbkk-modal-alert bg-white p-6 rounded-3xl text-center shadow-xlp-6 w-1/3">
        <h2 class="text-lg font-semibold mb-4">Add Collaborator</h2>
        <div class="text-left">
          <p>Collaborator e-mail</p>
          <input v-model="boardName" type="text" class="itbkk-board-name border p-2 w-full mb-4"
            placeholder="Enter board name (max 120 characters)" maxlength="120" />
          <p>Access Right</p>
          <select name="" id=""></select>
        </div>

        <div class="button-addboard flex justify-center">
          <button @click="createBoard" :disabled="!boardName" :class="{
            'bg-gray-300 text-gray-500 cursor-not-allowed': !boardName,
            'bg-emerald-500 hover:bg-emerald-600': boardName
          }" class="itbkk-button-ok text-white px-4 py-2">
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

button > .itbkk-modal-alert {
  border-radius: 20px;
  transition: transform 0.3s ease-in-out;
}

button:hover:not(:disabled) {
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
