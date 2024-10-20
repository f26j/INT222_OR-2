<script setup>
import { onMounted, ref } from "vue"
import { useRouter } from "vue-router"
import { useUserStore } from "@/stores/authUser.js"
import { useBoardStore } from "@/stores/apiController.js"
import { storeToRefs } from "pinia"
import Navbar from "../components/css/Navbar.vue"
import Bg_effect from "../components/css/Background.vue"
import Copyright from "../components/css/Copyright.vue"

// API URL จาก environment
const baseUrl = import.meta.env.VITE_API_BOARDS

// ใช้ `ref` สำหรับเก็บข้อมูลบอร์ดและสถานะการโหลด
const store = useBoardStore()
const { boards } = storeToRefs(store)
const loading = ref(true)
const error = ref(false)
const router = useRouter()

// เรียกใช้ userStore เพื่อดึง token
const userStore = useUserStore()
const boardStore = useBoardStore()
const { token, oid } = storeToRefs(userStore)


async function fetchBoardApi() {
  try {
    loading.value = true
    const response = await fetch(`${baseUrl}/v3/boards`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token.value}`,
        "Content-Type": "application/json",
      },
    })

    // ตรวจสอบสถานะการตอบสนอง
    if (response.status === 401) {
      // ถ้าได้รับสถานะ 401 ให้ logout และเปลี่ยนเส้นทางไปที่หน้า login
      userStore.logout()
      // router.push("/login")
      return
    }

    if (!response.ok) {
      throw new Error("Error fetching boards")
    }

    const data = await response.json()

    // ตรวจสอบว่ามี oid ก่อนทำการกรองบอร์ด
    if (!oid.value) {
      throw new Error("User OID is missing")
    }

    // กรองบอร์ดที่เป็นเจ้าของโดย user หรือมี visibility เป็น public
    const filteredBoards = data.filter(
      (board) =>
        (board.owner && board.owner.oid === oid.value) ||
        board.visibility === "PUBLIC"
    )

    boards.value = filteredBoards
  } catch (err) {
    console.error("Error fetching boards:", err)
    error.value = true
  } finally {
    loading.value = false
  }
}

// ฟังก์ชันลบ Board พร้อมตรวจสอบ oid
const deleteBoard = async (boardId) => {
  const boardToDelete = boards.value.find((board) => board.id === boardId)

  if (boardToDelete && boardToDelete.owner.oid === userStore.oid) {
    try {
      await boardStore.deleteBoard(boardId)
      await boardStore.fetchBoards() // อัปเดตรายการบอร์ดหลังจากลบสำเร็จ
    } catch (error) {
      console.error("Error deleting board:", error)
    }
  } else {
    alert("You do not have permission to delete this board.")
  }
}

onMounted(() => {
  fetchBoardApi()
})
</script>

<template>
  <Navbar class="itbkk-fullname" />
  <div class="pt-24">
    <header class="mb-2 max-w-6xl mx-auto">
      <!-- IT-Bangmod Kradan Kanban อยู่ซ้ายบน -->
      <h1 class="text-left text-3xl font-bold">IT-Bangmod Kradan Kanban</h1>
      <!-- Board List และปุ่ม Create new board -->
      <div class="flex justify-between items-center mt-2">
        <!-- ขนาดของ Board List เล็กกว่า IT-Bangmod Kradan Kanban -->
        <h2 class="itbkk-boards text-lg font-semibold">Board List</h2>
        <button
          @click="router.push('/board/add')"
          class="itbkk-button-create flex items-center cursor-pointer bg-color-purple text-white px-4 py-2 shadow hover:shadow-md hover:scale-105 transition-transform duration-30 rounded-full"
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
          Create new board
        </button>
      </div>
    </header>
  </div>

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
                  class="flex items-center bg-white text-black font-bold px-3 py-1.5 rounded-lg shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
                  @click="editBoard(board.id)"
                  disabled
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    fill="currentColor"
                    class="mr-2"
                    viewBox="0 0 24 24"
                  >
                    <path
                      fill="currentColor"
                      fill-rule="evenodd"
                      d="M17.907 4.93c.104-.105.294-.186.539-.18a.848.848 0 0 1 .572.216c.13.13.222.34.231.575c.01.234-.065.413-.167.515l-7.49 7.5l-1.273.116l.1-1.245zm2.173-1.024c-.437-.438-1.037-.643-1.6-.656c-.564-.013-1.182.167-1.635.62l-7.682 7.692a.75.75 0 0 0-.217.47l-.194 2.407a.75.75 0 0 0 .816.807l2.43-.22a.75.75 0 0 0 .463-.218l7.682-7.692c.456-.456.627-1.073.605-1.635a2.373 2.373 0 0 0-.668-1.575M5.812 4.75a2.568 2.568 0 0 0-2.563 2.562v10.875a2.568 2.568 0 0 0 2.563 2.563h10.875a2.568 2.568 0 0 0 2.563-2.562V12.75a.75.75 0 0 0-1.5 0v5.438c0 .41-.48 1.062-1.063 1.062H5.812c-.583 0-1.063-.48-1.063-1.062V7.312c0-.582.48-1.062 1.063-1.062h5.437a.75.75 0 0 0 0-1.5z"
                      clip-rule="evenodd"
                    />
                  </svg>
                  <span>Edit</span>
                </button>
                <button
                  class="flex items-center bg-white text-black font-bold px-3 py-1.5 rounded-lg shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
                  @click="deleteBoard(board.id)"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    fill="currentColor"
                    class="mr-2"
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
  <Bg_effect />
  <router-view></router-view>
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
