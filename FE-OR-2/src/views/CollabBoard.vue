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

// Modal State
const isAddModalOpen = ref(false);

const isModalOpen = ref(false); // สถานะเปิด-ปิด Modal
const modalActionType = ref(""); // Action type: 'remove' หรือ 'access-right'
const selectedCollaborator = ref(null); // เก็บ collaborator ที่ถูกเลือก
const newAccessRight = ref("READ"); // Access Right ใหม่สำหรับการเปลี่ยน
const previousAccessRight = ref(""); // เก็บค่า Access Right ก่อนการเปลี่ยน


const collaboratorEmail = ref("");
const accessRight = ref("READ");
const boardId = ref("");
const boardName = ref("");
const boardOwnerOid = ref("");
const tooltipText = "You need to be board owner to perform this action.";

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

const isAddButtonDisabled = computed(() => {
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const isValidEmail = emailPattern.test(collaboratorEmail.value);
  const isSelfEmail = collaboratorEmail.value.trim() === userStore.email; // ตรวจสอบว่า email ตรงกับ user ที่ login
  return !isValidEmail || isSelfEmail;
});


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
    isAddModalOpen.value = false;
    await fetchCollabBoardData();
  } catch (err) {
    console.error("Error adding collaborator:", err);

    // Handle specific HTTP status codes
    if (err.response && err.response.status) {
      switch (err.response.status) {
        case 401:
          router.push({ name: "login" });
          break;
        case 403:
          toastStore.addToast(
            "You do not have permission to add board collaborator.",
            "error"
          );
          break;
        case 404:
          toastStore.addToast("The user does not exists.", "error");
          break;
        case 409:
          toastStore.addToast(
            "The user is already the collaborator of this board.",
            "error"
          );
          break;
        default:
          toastStore.addToast(
            "There is a problem. Please try again later.",
            "error"
          );
      }
    } else {
      // Generic error message for non-HTTP errors
      toastStore.addToast(
        "There is a problem. Please try again later.",
        "error"
      );
    }
  }
};

const openModal = (actionType, collaborator, accessRight = "READ") => {
  modalActionType.value = actionType; // กำหนด action type
  selectedCollaborator.value = collaborator; // เก็บ collaborator ที่ถูกเลือก
  if (actionType === "access-right") {
    previousAccessRight.value = collaborator.accessRight; // เก็บค่าก่อนการเปลี่ยน
    newAccessRight.value = accessRight; // ตั้งค่า Access Right ใหม่
  }
  isModalOpen.value = true;
};



// Close Modal
const closeModal = () => {
  if (isAddModalOpen.value) {
    // ปิด Add Collaborator Modal
    isAddModalOpen.value = false;
    collaboratorEmail.value = ""; // รีเซ็ตค่าที่กรอกใน Modal
    accessRight.value = "READ";
  } else if (isModalOpen.value) {
    // ปิด Remove หรือ Access Right Modal
    if (modalActionType.value === "access-right" && selectedCollaborator.value) {
      selectedCollaborator.value.accessRight = previousAccessRight.value; // รีเซ็ต Access Right
    }
    isModalOpen.value = false;
    selectedCollaborator.value = null;
    newAccessRight.value = "READ";
  }
};




// Confirm Action (Remove or Change Access Right)
const confirmModalAction = async () => {
  if (modalActionType.value === "remove") {
    // ลบ collaborator
    await removeCollaborator(selectedCollaborator.value.oid);
  } else if (modalActionType.value === "access-right") {
    // เปลี่ยน Access Right
    await updateAccessRight(selectedCollaborator.value.oid, newAccessRight.value);
  }
  closeModal(); // ปิด Modal หลังดำเนินการสำเร็จ
};



// Remove Collaborator
const removeCollaborator = async (collaboratorId) => {
  try {
    await collabBoardStore.removeCollaborator(boardId.value, collaboratorId);
    toastStore.addToast("Collaborator removed successfully.", "success");
    await fetchCollabBoardData(); // รีเฟรชข้อมูล
  } catch (err) {
    console.error("Error removing collaborator:", err);
    toastStore.addToast("Failed to remove collaborator.", "error");
  }
};

// Update Collaborator Access
const updateAccessRight = async (collaboratorId, accessRight) => {
  try {
    await collabBoardStore.updateCollaboratorPermission(boardId.value, collaboratorId, accessRight);
    toastStore.addToast("Access right updated successfully.", "success");
    await fetchCollabBoardData(); // รีเฟรชข้อมูล
  } catch (err) {
    console.error("Error updating access right:", err);
    toastStore.addToast("Failed to update access right.", "error");
  }
};

const openAddModal = async () => {
  isAddModalOpen.value = true;
  await nextTick();
};

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
          <div class="tooltip-container">
            <button
              @click="openAddModal"
              :disabled="boardOwnerOid !== userStore.oid"
              :class="{
                'itbkk-collaborator-add flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow transition-transform duration-300': true,
                'hover:shadow-md hover:scale-105':
                  boardOwnerOid === userStore.oid,
                'opacity-50 cursor-not-allowed':
                  boardOwnerOid !== userStore.oid,
              }"
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
    <div class="p-8 bg-gradient-to-r to-pink-100 rounded-lg">
      <table class="w-full text-left table-fixed">
        <thead class="bg-violet-500">
          <tr>
            <th class="px-4 py-3 w-1/12 text-center font-semibold">No</th>
            <th class="px-4 py-3 w-2/12 text-left font-semibold">Name</th>
            <th class="px-4 py-3 w-4/12 text-left font-semibold">E-mail</th>
            <th class="px-4 py-3 w-2/12 text-left font-semibold">
              Access Right
            </th>
            <th class="px-4 py-3 w-3/12 text-center font-semibold">Action</th>
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
          <tr
            v-if="
              !collabBoards || (collabBoards.length === 0 && !loading && !error)
            "
          >
            <td colspan="5" class="px-4 py-4 text-center">No Collaborators</td>
          </tr>
          <tr
            v-for="(collaborator, index) in collabBoards"
            :key="collaborator.id"
            class="itbkk-item border-b"
          >
            <td class="px-4 py-3 text-center font-bold">{{ index + 1 }}</td>
            <td class="itbkk-name px-4 py-3 font-bold">
              {{ collaborator.name }}
            </td>
            <td class="itbkk-email px-4 py-3 text-[#717171] truncate">
              {{ collaborator.email }}
            </td>
            <td class="itbkk-access-right px-4 py-3 text-center">
              <!-- Select for Access Rights -->
              <div class="tooltip-container">
                <!-- @change="(collaborator.oid, collaborator.accessRight)" -->
                <select
                  :value="collaborator.accessRight"
                  @change="openModal('access-right', collaborator, $event.target.value)"
                  :disabled="boardOwnerOid !== userStore.oid"
                  :class="{
                    'itbkk-collab-removeborder p-2 rounded-md': true,
                    'hover:shadow-md hover:scale-105 transition-transform duration-300':
                      boardOwnerOid === userStore.oid,
                    'opacity-50 cursor-not-allowed':
                      boardOwnerOid !== userStore.oid,
                  }"
                >
                  <option value="READ">READ</option>
                  <option value="WRITE">WRITE</option>
                </select>
                <span
                  v-if="boardOwnerOid !== userStore.oid"
                  class="tooltip-text"
                >
                  {{ tooltipText }}
                </span>
              </div>
            </td>
            <td class="px-4 py-3 text-center">
              <div class="tooltip-container">
                <button
                  @click="openModal('remove', collaborator)"
                  :disabled="boardOwnerOid !== userStore.oid"
                  :class="{
                    'itbkk-collab-remove bg-red-500 text-white px-4 py-2 rounded-full': true,
                    'hover:bg-red-600 hover:shadow-md hover:scale-105 transition-transform duration-300':
                      boardOwnerOid === userStore.oid,
                    'opacity-50 cursor-not-allowed':
                      boardOwnerOid !== userStore.oid,
                  }"
                >
                  Remove
                </button>
                <span
                  v-if="boardOwnerOid !== userStore.oid"
                  class="tooltip-text"
                >
                  {{ tooltipText }}
                </span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Add Collaborator Modal -->
    <div
      v-if="isAddModalOpen"
      class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
    >
      <div
        class="itbkk-modal-alert bg-white p-6 rounded-xl shadow-xl w-full max-w-lg"
      >
        <h2 class="text-xl font-bold mb-4">Add Collaborator</h2>
        <p class="pb-1 font-medium">Collaborator e-mail</p>
        <input
          v-model="collaboratorEmail"
          type="email"
          maxlength="50"
          placeholder="Enter collaborator e-mail"
          class="itbkk-collaborator-email w-full p-2 mb-4 border rounded-md focus:ring focus:ring-blue-300"
        />
        <p class="pb-1 font-medium">Access Right</p>
        <select
          v-model="accessRight"
          class="itbkk-access-right w-full p-2 mb-4 border rounded-md focus:ring focus:ring-blue-300"
        >
          <option value="READ">READ</option>
          <option value="WRITE">WRITE</option>
        </select>
        <div class="flex justify-end gap-4">
          <button
            @click="addCollab"
            :disabled="isAddButtonDisabled"
            :class="{
              'bg-gray-300 text-gray-500 cursor-not-allowed':
                isAddButtonDisabled,
              'bg-emerald-500 hover:bg-emerald-600': !isAddButtonDisabled,
            }"
            class="itbkk-button-confirm text-white px-4 py-2"
          >
            Add
          </button>
          <button
            @click="closeModal"
            class="itbkk-button-cancel bg-gray-300 hover:bg-gray-400 text-gray-500 px-4 py-2"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>

    <div v-if="isModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
      <div class="itbkk-modal-alert bg-white p-6 rounded-xl shadow-xl w-full max-w-xl">
        <h2 class="text-xl font-bold mb-4">
          {{ modalActionType === 'remove' ? 'Remove Collaborator' : 'Change Access Right' }}
        </h2>
        <p class="itbkk-message mb-4">
          {{ modalActionType === 'remove'
            ? `Do you want to remove "${selectedCollaborator?.name}" from the board?`
            : `Do you want to change access right of "${selectedCollaborator?.name}" to "${newAccessRight}"?` }}
        </p>
        <div class="flex justify-end gap-4">
          <button @click="confirmModalAction"
            class="itbkk-button-confirm bg-emerald-500 hover:bg-emerald-600 text-white px-4 py-2 rounded-full">
            Confirm
          </button>
          <button @click="closeModal"
            class="itbkk-button-cancel bg-gray-300 hover:bg-gray-400 text-gray-700 px-4 py-2 rounded-full">
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
  max-width: 1000px;
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
