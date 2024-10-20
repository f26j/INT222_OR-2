<script setup>
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/AuthUserStore.js";
import { useBoardStore } from "@/stores/BoardStore.js";

const userStore = useUserStore();
const fullname = computed(() => userStore.fullname); // ดึงข้อมูลชื่อผู้ใช้
const router = useRouter();
const visibility = ref("private");
const errorMessage = ref("");
const boardName = ref("");

// เรียก BoardStore มาใช้
const boardStore = useBoardStore();

// กำหนดชื่อบอร์ดให้ตรงกับชื่อผู้ใช้โดยอัตโนมัติ
watch(
  fullname,
  (newFullname) => {
    boardName.value = `${newFullname} personal board`;
  },
  { immediate: true }
);

async function createBoard() {
  try {
    const newBoard = {
      name: boardName.value.trim(),
      owner: fullname.value,
      visibility: visibility.value,
    };

    const createdBoard = await boardStore.createBoard(newBoard);

    console.log("Board created successfully:", createdBoard);
    router.push(`/board/${createdBoard.id}`);
  } catch (error) {
    console.error("Error creating board:", error);
    errorMessage.value = error.message || "An unexpected error occurred.";
  }
}


// ปิด Modal
function closeModal() {
  errorMessage.value = "";
  router.push("/board");
}
</script>

<template>
  <div class="itbkk-modal-new fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
    <div class="bg-white p-6 rounded-3xl text-center shadow-xlp-6 w-1/3">
      <h2 class="text-lg font-semibold mb-4">Create New Board</h2>
      <div v-if="errorMessage" class="text-red-500 mb-4">
        {{ errorMessage }}
      </div>
      <input
        v-model="boardName"
        type="text"
        class="itbkk-board-name border p-2 w-full mb-4"
        placeholder="Enter board name (max 120 characters)"
        maxlength="120"
      />
      <select v-model="visibility" class="border p-2 w-full mb-4">
        <option value="private">Private</option>
        <option value="public">Public</option>
      </select>
      <div class="button-addboard flex justify-center">
        <button
          @click="createBoard"
          :disabled="!boardName"
          :class="{
            'bg-gray-300 text-gray-500 cursor-not-allowed': !boardName,
            'bg-emerald-500 hover:bg-emerald-600': boardName
          }"
          class="itbkk-button-ok text-white px-4 py-2"
        >
          Create
        </button>
        <button 
          @click="closeModal" 
          class="itbkk-button-cancel bg-gray-300 hover:bg-gray-400 text-gray-500 px-4 py-2 ml-4"
        >
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.fixed {
  position: fixed;
}
.inset-0 {
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}

button {
  border-radius: 20px;
  transition: transform 0.3s ease-in-out;
}

button:hover:not(:disabled) {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  box-shadow: 5px 5px 5px #c6c6c646;
}

.itbkk-modal-new, .itbkk-board-name, .itbkk-button-ok, .itbkk-button-cancel {
  user-select: none;
}
</style>
