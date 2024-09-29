<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/users.js"; // ใช้ userStore สำหรับ token
import { useBoardStore } from "@/stores/api.js"; // นำเข้า useBoardStore จาก api.js
import Navbar from "../components/css/Navbar.vue";
import Bg_effect from "../components/css/Background.vue";
import Copyright from "../components/css/Copyright.vue";

// API URL จาก environment
const Board_API = import.meta.env.VITE_API_BOARDS;

// ใช้ `ref` สำหรับเก็บข้อมูลบอร์ดและสถานะการโหลด
const boards = ref([]);
const loading = ref(true);
const error = ref(false);
const router = useRouter();

// เรียกใช้ userStore เพื่อดึง token
const userStore = useUserStore();
const { token } = userStore;

// เรียกใช้ boardStore เพื่อดึงและเก็บข้อมูลบอร์ด
const boardStore = useBoardStore();
const { boards: storeBoards } = boardStore;

// ฟังก์ชันเพื่อดึงข้อมูลบอร์ดทั้งหมดจาก API
async function fetchBoards() {
  try {
    if (!token) {
      router.push("/login");
      return;
    }

    console.log("Fetching all boards");

    const response = await fetch(`${Board_API}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Error fetching boards");
    }

    const data = await response.json();
    console.log("Received boards data:", data);

    // แสดงบอร์ดทั้งหมดที่ได้จาก API โดยไม่กรอง
    if (data.length > 0) {
      storeBoards.value = data; // บันทึกข้อมูลบอร์ดลงใน store
    } else {
      console.log("No boards found.");
      storeBoards.value = []; // ถ้าไม่มีบอร์ด ก็ให้เป็น array ว่าง
    }
  } catch (err) {
    console.error("Error fetching boards:", err);
    error.value = true;
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchBoards();
});
</script>

<template>
  <Navbar class="itbkk-fullname" />
  <div class="body">
    <header>
      <h1 class="text-center">IT-Bangmod Kradan Kanban</h1>
    </header>
  </div>
  <main>
    <div class="p-6 bg-gradient-to-r to-pink-100 rounded-lg">
      <div class="flex justify-between items-center mb-6 max-w-6xl mx-auto">
        <h1 class="text-xl font-semibold text-gray-800">Board List</h1>
        <button
          @click="router.push('/board/add')"
          class="flex items-center bg-white text-gray-800 px-4 py-2 rounded-lg shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            fill="currentColor"
            class="mr-2"
            viewBox="0 0 16 16"
          >
            <path
              d="M8 0a8 8 0 1 1 0 16A8 8 0 0 1 8 0Zm3.5 7.5H8.5v-3a.5.5 0 1 0-1 0v3h-3a.5.5 0 1 0 0 1h3v3a.5.5 0 1 0 1 0v-3h3a.5.5 0 1 0 0-1Z"
            />
          </svg>
          Create new board
        </button>
      </div>

      <table class="w-full text-left">
        <thead class="bg-violet-500">
          <tr>
            <th class="px-6 py-4 font-semibold">No</th>
            <th class="px-6 py-4 font-semibold">Name</th>
            <th class="px-6 py-4 font-semibold">Action</th>
          </tr>
        </thead>
        <tbody>
          <!-- ตรวจสอบสถานะการโหลด -->
          <tr v-if="loading">
            <td colspan="4" class="px-6 py-4 text-center text-gray-600">
              Loading...
            </td>
          </tr>
          <tr v-else-if="error">
            <td colspan="4" class="px-6 py-4 text-center text-red-600">
              Error loading boards.
            </td>
          </tr>

          <!-- แสดงข้อความหากไม่มีบอร์ด -->
          <tr v-if="storeBoards.length === 0 && !loading && !error">
            <td colspan="4" class="px-6 py-4 text-center">No Board Added</td>
          </tr>

          <!-- แสดงข้อมูลบอร์ด -->
          <tr
            v-for="(board, index) in storeBoards"
            :key="board.id"
            class="border-b last:border-0"
          >
            <td class="px-6 py-4">{{ index + 1 }}</td>
            <td class="px-6 py-4">{{ board.name }}</td>
            <td class="px-6 py-4 flex items-center space-x-4">
              <button
                class="itbkk-button-edit flex items-center space-x-2 w-20"
                @click="router.push(`/board/${board.id}`)"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="30"
                  height="30"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="currentColor"
                    fill-rule="evenodd"
                    d="M17.907 4.93c.104-.105.294-.186.539-.18a.848.848 0 0 1 .572.216c.13.13.222.34.231.575c.01.234-.065.413-.167.515l-7.49 7.5l-1.273.116l.1-1.245zm2.173-1.024c-.437-.438-1.037-.643-1.6-.656c-.564-.013-1.182.167-1.635.62l-7.682 7.692a.75.75 0 0 0-.217.47l-.194 2.407a.75.75 0 0 0 .816.807l2.43-.22a.75.75 0 0 0 .463-.218l7.682-7.692c.456-.456.627-1.073.605-1.635a2.373 2.373 0 0 0-.668-1.575M5.812 4.75a2.568 2.568 0 0 0-2.563 2.562v10.875a2.568 2.568 0 0 0 2.563 2.563h10.875a2.568 2.568 0 0 0 2.563-2.562V12.75a.75.75 0 0 0-1.5 0v5.438c0 .582-.48 1.062-1.063 1.062H5.812c-.583 0-1.063-.48-1.063-1.062V7.312c0-.582.48-1.062 1.063-1.062h5.437a.75.75 0 0 0 0-1.5z"
                    clip-rule="evenodd"
                  />
                </svg>
                <span>Edit</span>
              </button>

              <button
                class="itbkk-button-delete flex items-center space-x-2 w-24"
                @click="deleteBoard(board.id)"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="30"
                  height="30"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="currentColor"
                    d="M20 8.7H4a.75.75 0 1 1 0-1.5h16a.75.75 0 0 1 0 1.5"
                  />
                  <path
                    fill="currentColor"
                    d="M16.44 20.75H7.56A2.4 2.4 0 0 1 5 18.49V8a.75.75 0 0 1 1.5 0v10.49c0 .41.47.76 1 .76h8.88c.56 0 1-.35 1-.76V8A.75.75 0 1 1 19 8v10.49a2.4 2.4 0 0 1-2.56 2.26m.12-13a.74.74 0 0 1-.75-.75V5.51c0-.41-.48-.76-1-.76H9.22c-.55 0-1 .35-1 .76V7a.75.75 0 1 1-1.5 0V5.51a2.41 2.41 0 0 1 2.5-2.26h5.56a2.41 2.41 0 0 1 2.53 2.26V7a.75.75 0 0 1-.75.76Z"
                  />
                  <path
                    fill="currentColor"
                    d="M10.22 17a.76.76 0 0 1-.75-.75v-4.53a.75.75 0 0 1 1.5 0v4.52a.75.75 0 0 1-.75.76m3.56 0a.75.75 0 0 1-.75-.75v-4.53a.75.75 0 0 1 1.5 0v4.52a.76.76 0 0 1-.75.76"
                  />
                </svg>
                <span>Delete</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </main>
  <footer>
    <Copyright />
  </footer>
  <Bg_effect />
  <router-view></router-view>
</template>

<style scoped>
/* สไตล์ของตาราง */
h1 {
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 30px;
  color: #000000;
  font-weight: 800;
  margin: 4rem 0 2rem;
}

table {
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.432);
}

th,
td {
  background-color: rgba(255, 254, 255, 0.329);
}

th {
  color: rgba(255, 255, 255, 0.748);
}

td {
  color: #683c86;
  font-family: "Ubuntu", sans-serif;
  font-weight: 600;
  font-style: normal;
}

thead th {
  background-color: #8e5bb1;
}

tbody tr:hover {
  background-color: rgba(255, 255, 255, 0.63);
}
</style>
