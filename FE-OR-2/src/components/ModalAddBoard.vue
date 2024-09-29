<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/users.js";
import { useBoardStore } from "@/stores/api.js"; // ใช้ store สำหรับบอร์ด

const boardName = ref("");
const visibility = ref("private"); // ค่า default ของ visibility เป็น private
const errorMessage = ref(""); 
const router = useRouter();
const userStore = useUserStore();
const { token, fullname } = userStore; // ใช้ fullname เพื่อบันทึกเจ้าของบอร์ด
const boardStore = useBoardStore(); // ใช้ store บอร์ด
const Board_API = import.meta.env.VITE_API_BOARDS;

async function createBoard() {
  const trimmedBoardName = boardName.value.trim();

  if (trimmedBoardName === "" || trimmedBoardName.length > 120) {
    errorMessage.value = "ชื่อบอร์ดต้องไม่เว้นว่างและต้องไม่เกิน 120 ตัวอักษร!";
    return;
  }

  if (!token) {
    errorMessage.value = "ไม่มี token สำหรับการยืนยันตัวตน!";
    router.push("/login");
    return;
  }

  try {
    const newBoard = {
      name: trimmedBoardName,
      owner: fullname, // บันทึกผู้สร้างบอร์ด
      visibility: visibility.value, // บันทึกค่า visibility
    };

    const response = await fetch(Board_API, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newBoard),
    });

    if (response.ok) {
      const data = await response.json();
      boardStore.addBoard(data); // เพิ่มบอร์ดใหม่ลงใน store
      router.push(`/board/${data.id}`);
    } else {
      const errorResponse = await response.json();
      errorMessage.value = `มีปัญหาในการสร้างบอร์ด: ${errorResponse.message}`;
    }
  } catch (err) {
    errorMessage.value = "เกิดข้อผิดพลาดในการเชื่อมต่อกับเซิร์ฟเวอร์.";
  }
}

function closeModal() {
  errorMessage.value = "";
  router.push("/board");
}
</script>

<template>
  <div class="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center">
    <div class="bg-white p-6 rounded shadow-lg">
      <h2 class="text-lg font-semibold mb-4">Create a new board</h2>

      <div v-if="errorMessage" class="text-red-500 mb-4">{{ errorMessage }}</div>

      <input v-model="boardName" type="text" class="border p-2 w-full mb-4" placeholder="Enter board name (max 120 characters)" />
      <select v-model="visibility" class="border p-2 w-full mb-4">
        <option value="private">Private</option>
        <option value="public">Public</option>
      </select>

      <div class="flex justify-end">
        <button @click="createBoard" class="bg-blue-500 text-white px-4 py-2 rounded">Create</button>
        <button @click="closeModal" class="text-red-500 ml-2">Cancel</button>
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
</style>
