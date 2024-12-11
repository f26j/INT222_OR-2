<script setup>
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/AuthUserStore.js";
import { useBoardStore } from "@/stores/BoardStore.js";
import { useToastStore } from "@/stores/ToastStore.js";

// Stores และ States
const userStore = useUserStore();
const boardStore = useBoardStore();
const toastStore = useToastStore();
const fullname = computed(() => userStore.fullname); // ชื่อเต็มผู้ใช้
const userOid = computed(() => userStore.oid); // oid ของผู้ใช้
const router = useRouter();

// ค่าฟอร์ม
const visibility = ref("private");
const errorMessage = ref("");
const boardName = ref("");

// ตั้งชื่อบอร์ดเริ่มต้น
watch(
  fullname,
  (newFullname) => {
    boardName.value = `${newFullname}'s Personal Board`;
  },
  { immediate: true }
);

// ฟังก์ชันสร้างบอร์ดใหม่
async function createBoard() {
  try {
    if (!boardName.value.trim()) {
      toastStore.addToast("Board name cannot be empty.", "error");
      return;
    }

    const newBoard = {
      name: boardName.value.trim(),
      ownerOid: userOid.value, // ใช้ oid ของผู้ใช้เป็นเจ้าของบอร์ด
      visibility: visibility.value,
    };

    // เรียกใช้ API เพื่อสร้างบอร์ด
    const createdBoard = await boardStore.createBoard(newBoard);

    toastStore.addToast("The board has been successfully created.", "success");
    console.log("Board created successfully:", createdBoard);

    // นำทางไปยังบอร์ดที่สร้างใหม่
    router.push(`/board/${createdBoard.id}/task`);
  } catch (error) {
    console.error("Error creating board:", error);

    if (error.response && error.response.status === 401) {
      // กรณี Unauthorized
      toastStore.addToast("Please log in to create a board.", "error");
      router.push(`/login`);
    } else {
      // กรณีอื่นๆ
      toastStore.addToast("Error creating board.", "error");
      errorMessage.value = error.message || "An unexpected error occurred.";
    }
  }
}

// ฟังก์ชันปิด Modal
function closeModal() {
  errorMessage.value = "";
  router.push("/board");
}
</script>

<template>
  <div class="itbkk-modal-new fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
    <div class="bg-white p-6 rounded-3xl text-center shadow-xlp-6 w-1/3">
      <h2 class="text-lg font-semibold mb-4">Create New Board</h2>
      <input
        v-model="boardName"
        type="text"
        class="itbkk-board-name border p-2 w-full mb-4"
        placeholder="Enter board name (max 120 characters)"
        maxlength="120"
      />
      <div class="button-addboard flex justify-center">
        <button
          @click="createBoard"
          :disabled="!boardName.trim()"
          :class="{
            'bg-gray-300 text-gray-500 cursor-not-allowed': !boardName.trim(),
            'bg-emerald-500 hover:bg-emerald-600': boardName.trim()
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
