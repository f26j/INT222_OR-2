<script setup>
import { onMounted, ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useBoardStore } from "@/stores/BoardStore.js";
import { storeToRefs } from "pinia";
import { useToastStore } from "@/stores/ToastStore.js";
import Navbar from "../components/css/Navbar.vue";
import Copyright from "../components/css/Copyright.vue";
import ModalAddBoard from "../components/modal/ModalAddBoard.vue";

const boardStore = useBoardStore();
const { boards } = storeToRefs(boardStore);
const toastStore = useToastStore();
const loading = ref(true);
const error = ref(false);
const isModalOpen = ref(false);

const router = useRouter();
const route = useRoute();

async function initializeBoards() {
  try {
    loading.value = true;
    await boardStore.fetchBoards();
  } catch (err) {
    console.error("Error fetching boards:", err);
    error.value = true;
  } finally {
    loading.value = false;
  }
}

const deleteBoard = async (boardId) => {
  try {
    await boardStore.deleteBoard(boardId);
    toastStore.addToast("Board deleted successfully.", "success");
  } catch (error) {
    console.error("Error deleting board:", error);
    toastStore.addToast("You do not have permission to delete this board.", "error");
    alert("You do not have permission to delete this board.");
  }
};

const closeModal = () => {
  isModalOpen.value = false;
  router.push("/board");
};

watch(
  () => route.path,
  (newPath) => {
    isModalOpen.value = newPath === "/board/add";
  },
  { immediate: true }
);


onMounted(() => {
  initializeBoards();
});
</script>

<template>
  <Navbar />
  <div class="pt-24">
    <header class="mb-2 max-w-6xl mx-auto">
      <h1 class="text-left text-3xl font-bold">IT-Bangmod Kradan Kanban</h1>
      <div class="flex justify-between items-center mt-2">
        <h2 class="itbkk-boards text-lg font-semibold">Board List</h2>
        <button
          @click="router.push('/board/add')"
          class="itbkk-button-create flex items-center cursor-pointer bg-color-purple text-white px-4 py-2 shadow hover:shadow-md hover:scale-105 transition-transform duration-300 rounded-full"
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
              d="M8 1a1 1 0 0 0-2 0v5H1a1 1 0 0 0 0 2h5v5a1 1 0 1 0 2 0V8h5a1 1 0 1 0 0-2H8z"
            />
          </svg>
          Create new board
        </button>
      </div>
    </header>

    <main>
      <div class="p-4 bg-gradient-to-r to-pink-100 rounded-lg">
        <table class="w-full text-left table-fixed">
          <thead class="bg-violet-500">
            <tr>
              <th class="px-4 py-3 w-1/12 text-center font-semibold">No</th>
              <th class="px-4 py-3 w-6/12 text-left font-semibold">Name</th>
              <th class="px-4 py-3 w-5/12 text-center font-semibold">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="3" class="px-4 py-4 text-center text-black font-bold">
                Loading...
              </td>
            </tr>
            <tr v-else-if="error">
              <td colspan="3" class="px-4 py-4 text-center text-red-600">
                Error loading boards.
              </td>
            </tr>
            <tr v-if="boards.length === 0 && !loading && !error">
              <td colspan="3" class="px-4 py-4 text-center">No Board Added</td>
            </tr>
            <tr
              v-for="(board, index) in boards"
              :key="board.id"
              class="border-b last:border-0"
            >
              <td class="px-4 py-3 text-center text-black font-bold">
                {{ index + 1 }}
              </td>
              <td
                class="td-name px-4 py-3 cursor-pointer text-black font-bold break-words"
                @click="router.push(`/board/${board.id}`)"
              >
                {{ board.name }}
              </td>
              <td class="px-4 py-3 space-x-4">
                <div class="flex justify-center gap-4">
                  <button
                    class="itbkk-button-edit"
                    @click="router.push(`/board/edit/${board.id}`)"
                  >
                    Edit
                  </button>
                  <button
                    class="itbkk-button-delete"
                    @click="deleteBoard(board.id)"
                  >
                    Delete
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
  </div>
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

.text-color-purple {
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

.td-name {
  color: black;
  font-family: "Ubuntu", sans-serif;
  font-weight: 600;
  font-style: normal;
}

.td-name:hover {
  color: #47467b;
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

.itbkk-button-edit,
.itbkk-button-delete {
  padding: 10px;
  background-color: #8e5bb1; /* สีพื้นหลังเดิม */
  color: white;
  border-radius: 8px;
  transition: transform 0.3s ease, background-color 0.3s ease;
}

.itbkk-button-edit:hover,
.itbkk-button-delete:hover {
  background-color: #6a4581; /* สีพื้นหลังเมื่อ hover */
  transform: scale(1.05); /* ขยายปุ่มเล็กน้อยเมื่อ hover */
}

.itbkk-button-edit svg,
.itbkk-button-delete svg {
  margin-right: 8px; /* เพิ่มระยะห่างระหว่างไอคอนกับข้อความ */
}

.itbkk-button-edit span,
.itbkk-button-delete span {
  font-size: 14px;
  font-weight: 500;
}
</style>
