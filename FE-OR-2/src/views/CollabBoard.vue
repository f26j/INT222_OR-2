<script setup>
import { onMounted, ref, computed, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useBoardStore } from "@/stores/BoardStore.js";
import { useCollabBoardStore } from "@/stores/CollabBoardStore.js";
import { useToastStore } from "@/stores/ToastStore.js";
import { useUserStore } from "@/stores/AuthUserStore.js";
import Navbar from "../components/css/Navbar.vue";
import Copyright from "../components/css/Copyright.vue";

// Stores
const collabBoardStore = useCollabBoardStore();
const toastStore = useToastStore();
const userStore = useUserStore();
const boardStore = useBoardStore();

// State
const { collabBoards } = storeToRefs(collabBoardStore);
const loading = ref(true);
const error = ref(false);
const isModalOpen = ref(false);
const collaboratorEmail = ref("");
const accessRight = ref("READ");
const boardId = ref("");
const boardName = ref("");
const boardOwnerOid = ref("");

console.log(collabBoards)

// Router
const router = useRouter();
const route = useRoute();
boardId.value = route.params.boardId;

// Fetch Board Data
const fetchBoardData = async () => {
  try {
    loading.value = true;
    const boardData = await boardStore.getBoard(boardId.value);
    if (boardData) {
      boardName.value = boardData.name;
      boardOwnerOid.value = boardData.owner.oid || ""; // Ensure owner.oid exists
    }
  } catch (err) {
    console.error("Error fetching board data:", err);
    error.value = true;
  } finally {
    loading.value = false;
  }
};

// Fetch Collaborators Data
const fetchCollabBoardData = async () => {
  try {
    loading.value = true;
    const collaborators = await collabBoardStore.fetchCollaborators(
      boardId.value
    );
    console.log("Collaborators:", collaborators); // Debug data
  } catch (err) {
    console.error("Error fetching collaborators data:", err);
    error.value = true;
  } finally {
    loading.value = false;
  }
};

// Add Collaborator
const addCollab = async () => {
  try {
    if (!collaboratorEmail.value.trim()) {
      toastStore.addToast("Please enter a valid email.", "error");
      return;
    }
    await collabBoardStore.addCollaborator(
      boardId.value,
      collaboratorEmail.value.trim(),
      accessRight.value
    );
    toastStore.addToast("Collaborator added successfully.", "success");
    collaboratorEmail.value = "";
    accessRight.value = "READ";
    isModalOpen.value = false;
    await fetchCollabBoardData();
  } catch (err) {
    console.error("Error adding collaborator:", err);
    toastStore.addToast("Failed to add collaborator.", "error");
  }
};

// Remove Collaborator
const removeCollaborator = async (collaboratorId) => {
  try {
    await collabBoardStore.removeCollaborator(boardId.value, collaboratorId);
    toastStore.addToast("Collaborator removed successfully.", "success");
    await fetchCollabBoardData(); // Refresh data
  } catch (err) {
    console.error("Error removing collaborator:", err);
    toastStore.addToast("Failed to remove collaborator.", "error");
  }
};

// Update Collaborator Access
const updateAccessRight = async (collaboratorId, newAccessRight) => {
  try {
    await collabBoardStore.updateCollaboratorPermission(
      boardId.value,
      collaboratorId,
      newAccessRight
    );
    toastStore.addToast("Access right updated successfully.", "success");
  } catch (err) {
    console.error("Error updating access right:", err);
    toastStore.addToast("Failed to update access right.", "error");
  }
};

// Modal Controls
const openModal = async () => {
  isModalOpen.value = true;
  await nextTick(); // Wait for DOM render
  document.querySelector('input[placeholder="Collaborator Email"]').focus();
};

const closeModal = () => {
  isModalOpen.value = false;
  collaboratorEmail.value = ""; // Reset email input
  accessRight.value = "READ"; // Reset access right
};

// Fetch Data on Mount
onMounted(async () => {
  await fetchBoardData();
  await fetchCollabBoardData();
});
</script>

<template>
  <Navbar />
  <div class="pt-24">
    <header class="mb-2 max-w-6xl mx-auto px-4 sm:px-6">
      <h1 class="text-left text-3xl font-bold">IT-Bangmod Kradan Kanban</h1>
      <div class="flex justify-between items-center mt-2">
        <h2 class="text-lg font-semibold pr-20">
          <span
            class="itbkk-boards hover:text-[#47467b] cursor-pointer"
            @click="router.push({ name: 'board' })"
          >
            Board </span
          >&nbsp;>&nbsp;
          <span
            class="itbkk-board-name hover:text-[#5e1f87] cursor-pointer"
            @click="router.push({ name: 'board-tasks' })"
          >
            {{ boardName }} </span
          >&nbsp;>&nbsp;
          <span class="text-[#ad386f] hover:text-[#a31a5a]">Collaborator</span>
        </h2>
        <div class="flex space-x-4">
          <button
            @click="router.push({ name: 'board-tasks' })"
            class="itbkk-button-home flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
          >
            Back
          </button>
          <button
            @click="openModal"
            :disabled="boardOwnerOid !== userStore.oid"
            class="itbkk-collaborator-add flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
          >
            Add Collaborator
          </button>
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
            <th class="px-4 py-3 w-2/12 text-left font-semibold">
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
      Error loading data.
    </td>
  </tr>
  <tr v-if="!collabBoards || collabBoards.length === 0 && !loading && !error">
    <td colspan="5" class="px-4 py-4 text-center">No Collaborators</td>
  </tr>
  <tr
    v-for="(collaborator, index) in collabBoards"
    :key="collaborator.id"
    class="border-b"
  >
    <td class="px-4 py-3 text-center font-bold">{{ index + 1 }}</td>
    <td class="px-4 py-3 font-bold">{{ collaborator.name }}</td>
    <td class="px-4 py-3 text-[#717171] truncate">{{ collaborator.email }}</td>
    <td class="px-4 py-3 text-center">
      <!-- Select for Access Rights -->
      <select
        v-model="collaborator.accessRight"
        @change="updateAccessRight(collaborator.oid, collaborator.accessRight)"
        class="border p-2 rounded-md"
      >
        <option value="READ">READ</option>
        <option value="WRITE">WRITE</option>
      </select>
    </td>
    <td class="px-4 py-3 text-center">
      <button
        @click="removeCollaborator(collaborator.oid)"
        class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-full"
      >
        Remove
      </button>
    </td>
  </tr>
</tbody>



      </table>
    </div>

    <!-- Add Collaborator Modal -->
    <div
      v-if="isModalOpen"
      class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
    >
      <div class="bg-white p-6 rounded-xl shadow-xl w-full max-w-lg">
        <h2 class="text-xl font-bold mb-4">Add Collaborator</h2>
        <input
          v-model="collaboratorEmail"
          type="email"
          placeholder="Collaborator Email"
          class="w-full p-2 mb-4 border rounded-md focus:ring focus:ring-blue-300"
        />
        <select
          v-model="accessRight"
          class="w-full p-2 mb-4 border rounded-md focus:ring focus:ring-blue-300"
        >
          <option value="READ">READ</option>
          <option value="WRITE">WRITE</option>
        </select>
        <div class="flex justify-end gap-4">
          <button
            @click="addCollab"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md transition"
          >
            Add
          </button>
          <button
            @click="closeModal"
            class="bg-gray-300 hover:bg-gray-400 text-gray-700 px-4 py-2 rounded-md transition"
          >
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
