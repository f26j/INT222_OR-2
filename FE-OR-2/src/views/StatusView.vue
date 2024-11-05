<script setup>
// import zone
import { onMounted, ref, onUpdated } from "vue";
import { useRouter, useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useStatusStore } from "@/stores/StatusStore.js"; // ใช้ StatusStore
import { useToastStore } from "@/stores/ToastStore.js";
import { useBoardStore } from "@/stores/BoardStore.js";
import Copyright from "../components/css/Copyright.vue";
import ModalDeleteStatus from "../components/modal/ModalDeleteStatus.vue";
import Navbar from "../components/css/Navbar.vue";

// Store, API zone
const router = useRouter();
const route = useRoute();
const store = useStatusStore();
const toastStore = useToastStore();
const boardStore = useBoardStore();
const { statuses } = storeToRefs(store);

// Variable zone
const showDelete = ref(false);
const selectDeleteStatusId = ref("");
const selectDeleteStatusName = ref("");
const boardId = route.params.boardId;
const boardName = ref("");

// ดึงข้อมูล Board และ Status เมื่อ component ถูก mounted
const isLoaded = ref(false); // เพิ่มตัวแปร

onMounted(async () => {
  if (!isLoaded.value) {
    try {
      await store.fetchStatuses(boardId); // ดึงข้อมูล Status ครั้งเดียว
      const board = await boardStore.getBoard(boardId); // ดึงข้อมูลบอร์ด
      boardName.value = board.name;
      isLoaded.value = true; // กำหนดค่าให้โหลดเสร็จแล้ว
    } catch (error) {
      console.error("Error fetching data:", error);
      router.push({ name: "NotFound" });
    }
  }
});

// onUpdated(async () => {
//   await store.fetchStatuses(boardId);
// });

// เปิดหน้า Home
const openHome = () => {
  router.push({ name: "board-tasks" });
};

// เปิด Modal สำหรับการเพิ่ม Status
const openModalAdd = () => {
  router.push({ name: "status-add" });
};

// เปิด Modal สำหรับการแก้ไข Status
const openModalEdit = (id) => {
  if (id) {
    router.push({ name: "status-edit", params: { statusId: id } });
  } else {
    console.error("ID parameter is required to open the edit modal");
  }
};

// เปิด Modal สำหรับการลบ Status
const openModalDelete = (id, name) => {
  showDelete.value = true;
  selectDeleteStatusId.value = id;
  selectDeleteStatusName.value = name;
};

const deleteStatus = async (id) => {
  try {
    await store.deleteStatus(boardId, id); // ใช้ store แทน statusStore
    toastStore.addToast("The status has been deleted", "success");
    showDelete.value = false;
  } catch (error) {
    console.error("Failed to delete status:", error);
    toastStore.addToast(
      "An error has occurred, the status could not be deleted.",
      "error"
    );
  }
};

const deleteTransfer = async (id, newId) => {
  try {
    await store.transferStatus(boardId, id, newId); // ใช้ store แทน statusStore
    toastStore.addToast("The status has been deleted", "success");
    showDelete.value = false;
  } catch (error) {
    console.error("Failed to transfer status:", error);
    toastStore.addToast(
      "An error has occurred, the status could not be deleted.",
      "error"
    );
  }
};


// ปิด Modal
const closeModal = () => {
  showDelete.value = false;
  router.push({ name: "board-status", params: { boardId } });
};
</script>

<template>
  <Navbar class="itbkk-fullname" />
  <div class="pt-24">
    <header class="mb-2 max-w-6xl mx-auto">
      <h1 class="text-left text-3xl font-bold">IT-Bangmod Kradan Kanban</h1>
      <div class="flex justify-between items-center mt-2">
        <h2 class="text-lg font-semibold text-gray-800">
          <span
            class="itbkk-boards cursor-pointer"
            @click="router.push({ name: 'board' })"
            >Board</span
          >&nbsp;>&nbsp;
          <span
            class="itbkk-tasks cursor-pointer"
            @click="router.push({ name: 'board-tasks' })"
            >{{ boardName }}</span
          >&nbsp;>&nbsp;<span class="textPurple">Status</span>
        </h2>
        <div class="flex space-x-4">
          <button
            @click="openHome"
            class="itbkk-button-home flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="32"
              height="32"
              fill="currentColor"
              class="mr-2"
              viewBox="0 4 16 16"
            >
              <path
                fill="currentColor"
                d="m16 8.5l1.53 1.53l-1.06 1.06L10 4.62l-6.47 6.47l-1.06-1.06L10 2.5l4 4v-2h2zm-6-2.46l6 5.99V18H4v-5.97zM12 17v-5H8v5z"
              ></path>
            </svg>
            Back
          </button>

          <button
            @click="openModalAdd"
            class="itbkk-button-add flex items-center bg-white text-gray-800 px-4 py-2 rounded-full shadow hover:shadow-md hover:scale-105 transition-transform duration-300"
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
            Add Status
          </button>
        </div>
      </div>
    </header>
  </div>
  <div class="body">
    <main class="flex flex-col items-center">
      <div class="tb">
        <table>
          <thead>
            <tr>
              <th></th>
              <th>Name</th>
              <th>Description</th>
              <th class="flex justify-center">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(status, index) in statuses"
              :key="status.id"
              class="itbkk-item"
            >
              <td class="bgd">
                <div class="idkb flex">{{ index + 1 }}</div>
              </td>
              <td class="itbkk-status-name">
                <p>{{ status.name }}</p>
              </td>
              <td :class="{ 'italic-gray': !status.description?.trim() }">
                <p v-if="!status.description?.trim()">
                  No description provided
                </p>
                <p v-else>{{ status.description }}</p>
              </td>
              <td class="itbkk-button-action flex">
                <button
                  class="itbkk-button-edit"
                  @click="openModalEdit(status.id)"
                  v-if="status.name !== 'No Status' && status.name !== 'Done'"
                >
                  Edit
                </button>
                <button
                  class="itbkk-button-delete"
                  @click="openModalDelete(status.id, status.name)"
                  v-if="status.name !== 'No Status' && status.name !== 'Done'"
                >
                  Delete
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
  </div>
  <ModalDeleteStatus
    v-if="showDelete"
    :statusId="selectDeleteStatusId"
    :name="selectDeleteStatusName"
    @closeModal="closeModal"
    @confirmDeleteStatus="deleteStatus"
    @deleteTransfer="deleteTransfer"
  />
  <router-view />
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");

h1 {
  font-family: "Ubuntu", sans-serif;
  color: black;
}

h2 {
  color: black;
}

h3 {
  font-family: "Ubuntu", sans-serif;
  color: #737292;
}

.a {
  color: rgb(250, 133, 188);
}

.textGray {
  color: #717171;
}

.statusBtn {
  display: flex;
  height: 40px;
  width: 100%;
  max-width: 900px;
  padding-left: 2rem;
}

.btn-stt {
  display: flex;
  justify-content: center;
  font-style: italic;
  padding: 5px;
  border-radius: 50px;
  width: 120px;
  height: 50px;
  transition: transform 0.3s ease;
}

.itbkk-tasks:hover {
  color: #a31a5a;
}

.itbkk-boards:hover {
  color: #47467b;
}

.itbkk-button-home {
  font-style: italic;
  background-color: rgba(255, 255, 255, 0);
  color: #683c86;
  border: 2px solid #683c86;
  border-color: #683c86;
  transition: background-color 0.5s ease,
    transform 150ms cubic-bezier(0.4, 0, 0.2, 1);
}
.itbkk-button-home:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.itbkk-button-add {
  background-color: #683c86;
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
.itbkk-button-add:hover {
  background-color: #9b65be;
}

.itbkk-button-action {
  padding: 15px 5px 10px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.itbkk-button-action button {
  background-color: #905cb334;
  border-radius: 10px;
  height: 40px;
  width: 70px;
  justify-content: center;
  align-items: center;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.itbkk-button-action button:hover {
  transform: scale(1.06);
  transition: transform 0.3s ease;
  color: #683c86;
}

.itbkk-status-name p,
.itbkk-status-description p {
  width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  /* word-break: break-all; */
}

.itbkk-status-name p {
  color: black;
}

.italic-gray {
  font-style: italic;
  color: gray;
}

.textPurple {
  color: #683c86;
}

.textPurple:hover {
  color: #5e1f87;
}

header {
  margin-bottom: 20px;
}

.idkb {
  display: inline;
}

.tb {
  padding: 2rem;
  width: 100%;
  overflow: auto;
}

table {
  border-collapse: collapse;
  width: 100%;
  max-width: 900px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.432);
  margin: 0 auto;
  border-radius: 25px;
}

th,
td {
  padding: 15px;
  background-color: rgba(255, 254, 255, 0.329);
}

th {
  text-align: left;
  color: rgba(255, 255, 255, 0.748);
}

td {
  color: black;
  font-family: "Ubuntu", sans-serif;
  font-weight: 600;
  font-style: normal;
}

td.bgd {
  padding-left: 30px;
}

thead th {
  background-color: #683c86;
}

tbody tr:hover {
  background-color: rgba(255, 255, 255, 0.63);
}

.bgi {
  display: none;
}

tbody {
  td {
    position: relative;
  }
  &:hover {
    &:before {
      content: "";
      position: absolute;
      left: 0;
      right: 0;
      background-color: rgba(255, 255, 255, 0.726);
      z-index: -1;
    }
  }
}

footer {
  padding-bottom: 5rem;
}

@media screen and (max-width: 745px) {
  table,
  thead,
  tbody,
  th,
  td,
  tr {
    display: block;
  }

  thead tr {
    position: absolute;
    top: -9999px;
    left: -9999px;
  }

  .bgd {
    background-color: #8e5bb1;
    color: rgba(255, 255, 255, 0.774);
  }

  .bgi {
    display: inline;
    margin-left: -1rem;
  }

  .button {
    width: 60px;
    height: 60px;
  }

  tr {
    border: 1px solid #ccc;
  }

  td {
    border: none;
    border-bottom: 1px solid #eee;
    position: relative;
    padding-left: 3rem;
  }

  td:before {
    position: absolute;
    top: 6px;
    left: 6px;
    width: 45%;
    padding-right: 10px;
    white-space: nowrap;
  }

  .btn-a {
    position: fixed;
  }

  .tb {
    z-index: 1;
    position: relative;
  }

  .itbkk-button-action {
    justify-content: left;
    padding-left: 3rem;
    gap: 0;
  }

  .itbkk-status-name p,
  .itbkk-status-description p {
    width: 100%;
  }
}
tbody tr {
  min-height: 60px;
}

tbody td {
  min-height: inherit;
}

</style>
