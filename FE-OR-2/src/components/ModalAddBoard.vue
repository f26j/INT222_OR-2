<script setup>
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/users.js";
import { useBoardStore } from "@/stores/api.js";

const userStore = useUserStore();
const token = computed(() => userStore.token);
const fullname = computed(() => userStore.fullname);
const visibility = ref("private");
const errorMessage = ref("");
const router = useRouter();

const boardStore = useBoardStore();
const boardName = ref("");
const baseUrl = import.meta.env.VITE_API_BOARDS;

watch(
  fullname,
  (newFullname) => {
    boardName.value = `${newFullname} personal board`;
  },
  { immediate: true }
);

async function createBoard() {
  const newBoard = {
    name: boardName.value.trim(),
    owner: fullname.value,
    visibility: visibility.value,
  };

  try {
    const response = await fetch(`${baseUrl}/v3/boards`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token.value}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newBoard),
    });

    if (response.ok) {
      const createdBoard = await response.json();
      console.log("Board created successfully:", createdBoard);
      boardStore.addBoard(createdBoard); // เพิ่มบอร์ดลงใน store
      router.push(`/board/${createdBoard.id}`);
    } else {
      console.error("Failed to create board:", await response.json());
    }
  } catch (error) {
    console.error("Error creating board:", error);
  }
}


function closeModal() {
  errorMessage.value = "";
  router.push("/board");
}
</script>

<template>
  <div class="itbkk-modal-new fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
    <div class="bg-white p-6 rounded-3xl text-center shadow-xlp-6 w-1/3">
      <h2 class="text-lg font-semibold mb-4">Create a new board</h2>
      <div v-if="errorMessage" class="text-red-500 mb-4">
        {{ errorMessage }}
      </div>
      <input
        v-model="boardName"
        type="text"
        class="itbkk-board-name border p-2 w-full mb-4"
        placeholder="Enter board name (max 120 characters)"
      />
      <!-- <select v-model="visibility" class="border p-2 w-full mb-4">
        <option value="private">Private</option>
        <option value="public">Public</option>
      </select> -->
      <div class="button-addboard flex justify-center">
        <button
          @click="createBoard"
          class="itbkk-button-ok bg-emerald-500 hover:bg-emerald-600 text-white px-4 py-2"
        >
          Save
        </button>
        <button 
          @click="closeModal" 
          class="itbkk-button-cancel bg-gray-300 hover:bg-gray-400 text-gray-600 px-4 py-2 ml-4"
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
.bg-opacity-50 {
  background-opacity: 0.5;
}

button {
  border-radius: 20px;
  transition: transform 0.3s ease-in-out;
}

button:hover {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  box-shadow: 5px 5px 5px #c6c6c646;
}
</style>
