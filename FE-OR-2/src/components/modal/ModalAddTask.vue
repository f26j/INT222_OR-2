<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToastStore } from "@/stores/ToastStore.js";
import { useTaskStore } from "@/stores/TaskStore.js";
import { useStatusStore } from "@/stores/StatusStore.js";
import { useUserStore } from "@/stores/AuthUserStore.js";

// Store, API zone
const router = useRouter();
const taskStore = useTaskStore();
const toastStore = useToastStore();
const userStore = useUserStore();
const statusStore = useStatusStore(); // ใช้ StatusStore สำหรับดึงสถานะของ Board
const route = useRoute();
const boardId = route.params.boardId; // ดึงค่า boardId จาก URL

// Variable zone
const newTitle = ref("");
const newDescription = ref("");
const newAssignees = ref("");
const newStatus = ref(null); // จะใช้เก็บ Status ที่เลือก

// ดึงข้อมูล Status ของ Board
const fetchStatuses = async () => {
  try {
    await statusStore.fetchStatuses(boardId);
    newStatus.value = statusStore.statuses[0]?.id || null;
  } catch (error) {
    console.error("Error fetching statuses:", error.message);
  }
};

// OnMounted ดึงข้อมูล Status เมื่อ component ถูก mount
onMounted(async () => {
  // ตรวจสอบว่ามี Token หรือไม่
  if (!userStore.token) {
    console.warn("No token available. Redirecting to 403.");
    return router.push({ name: "AccessDenied" }); // redirect ไปหน้า 403 (AccessDenied)
  }
  await fetchStatuses();
});

// ฟังก์ชันสำหรับเพิ่ม Task ใหม่
const addTask = async () => {
  const trimmedTitle = newTitle.value.trim();
  const trimmedDescription = newDescription.value.trim();
  const trimmedAssignees = newAssignees.value.trim();

  // ตรวจสอบให้แน่ใจว่า status ถูกต้อง
  if (!newStatus.value) {
    console.error("Task status cannot be null");
    return;
  }

  const newTask = {
    title: trimmedTitle,
    description: trimmedDescription || null,
    assignees: trimmedAssignees || null,
    status: { id: newStatus.value }, // ส่ง id ของ status ที่เลือก
  };

  if (!newTask.title) {
    return;
  }

  try {
    await taskStore.addTask(boardId, newTask);
    toastStore.addToast("Task added successfully.", "success");
    router.push({ name: "board-tasks", params: { boardId } }).catch(() => {});
  } catch (error) {
    console.error("Error adding new task:", error.message);
  }

  // Reset fields
  newTitle.value = "";
  newDescription.value = "";
  newAssignees.value = "";
  newStatus.value = statusStore.statuses[0]?.id || null; // Reset เป็นค่า default
};

const closeModal = () => {
  router.push({ name: "board-tasks", params: { boardId } }).catch(() => {});
};
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div
      class="itbkk-modal-task modals rounded-lg text-center shadow-xlp-6 w-min"
      role="dialog"
      aria-modal="true"
    >
      <div class="modal-content">
        <form @submit.prevent="addTask">
          <div class="modal-header">
            <div class="itbkktitle text-left">
              <h4>Add New Task</h4>
            </div>
          </div>
          <hr />

          <div class="modal-body">
            <div class="text-left flex-col">
              <div class="itbkk-title flex flex-wrap -mx-3 mb-2">
                <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                  <label
                    class="block uppercase tracking-wide text-xs font-bold mb-2"
                    for="grid-city"
                  >
                    Title
                  </label>
                  <textarea
                    class="ttitle appearance-none block w-full bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
                    id="title"
                    type="text"
                    placeholder="Enter title..."
                    v-model="newTitle"
                    maxlength="100"
                  ></textarea>
                </div>
              </div>

              <div class="itbkk-description text-left">
                <div class="flex flex-wrap -mx-3 mb-2">
                  <div class="desc w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label
                      class="block uppercase tracking-wide text-xs font-bold mb-2"
                      for="grid-city"
                      >Description
                    </label>
                    <textarea
                      class="itbkkdesc appearance-none block w-full bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                      id="description"
                      type="text"
                      placeholder="Enter description..."
                      v-model="newDescription"
                      maxlength="500"
                    ></textarea>
                  </div>
                </div>
              </div>
            </div>
            <div class="detailright flex-col">
              <div class="text-left">
                <div class="assign flex flex-wrap -mx-3 mb-2">
                  <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label
                      class="block uppercase tracking-wide text-xs font-bold mb-2"
                      for="grid-city"
                    >
                      Assignees
                    </label>
                    <textarea
                      class="itbkkassignees appearance-none block w-full bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
                      id="assign"
                      type="text"
                      placeholder="Enter assignees..."
                      v-model="newAssignees"
                      maxlength="30"
                    ></textarea>
                  </div>
                </div>
              </div>

              <div class="text-left">
                <div class="w-full md:w-1/3 mb-6 md:mb-0">
                  <label
                    class="block uppercase tracking-wide text-xs font-bold mb-2"
                    for="grid-state"
                  >
                    Status
                  </label>
                  <div class="relative">
                    <select
                      class="itbkk-status block w-60 bg-gray-200 border border-gray-200 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                      id="grid-state"
                      v-model="newStatus"
                    >
                      <option
                        v-for="(status, index) in statusStore.statuses"
                        :key="index"
                        :value="status.id"
                      >
                        {{ status.name }}
                      </option>
                    </select>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <div class="itbkk-button">
              <hr />
              <button @click="closeModal" class="close border">Cancel</button>
              <button
                @click="$emit('toastAdd')"
                class="itbkk-button-confirm border"
                type="submit"
                :disabled="newTitle.length === 0"
              >
                Save
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");

.modals {
  background-color: #c8badb;
  color: black;
  width: 800px;
}

.details {
  color: black;
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 3;
}

.itbkk-title {
  padding: 10px;
}

.ttitle {
  width: 400px;
  height: 50px;
}

.itbkkdesc {
  width: 400px;
  height: 200px;
}

.itbkkassignees {
  width: 320px;
  height: 220px;
}

.itbkk-button-confirm {
  width: 4rem;
  background-color: rgb(179, 236, 168);
  color: rgb(15, 82, 21);
  margin: 10px;
  float: right;
  border-radius: 50px;
}

.close {
  width: 4rem;
  background-color: rgb(248, 195, 199);
  color: rgb(82, 15, 18);
  margin: 10px;
  margin-right: 20px;
  float: right;
  border-radius: 50px;
}

.itbkk-button-confirm:hover {
  background-color: rgb(28, 126, 36);
  color: rgb(179, 236, 168);
}

.itbkk-button-confirm:disabled {
  background-color: #a8a8a8;
  color: #ffffff;
}
.itbkk-button-confirm:disabled:hover {
  transform: scale(1);
}

.close:hover {
  background-color: rgb(163, 42, 48);
  color: rgb(248, 195, 199);
}

.modal-header {
  color: black;
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 30px;
  margin: 15px;
  overflow-x: auto;
}

.modal-header::-webkit-scrollbar {
  height: 12px;
}

h1 {
  padding-bottom: 5px;
}

h4 {
  color: black;
}

.desc {
  margin: 15px;
}

.descinput {
  width: 30rem;
  height: 20rem;
}

.ass {
  width: 15rem;
  height: 10rem;
}

.time {
  margin-top: 10px;
  margin-right: 15px;
  font-size: 0.8rem;
  font-weight: 800;
}

.modal-body {
  display: flex;
  padding: 10px;
}

.detailright {
  padding: 15px;
}

::-webkit-scrollbar {
  width: 12px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: #a8a8a8;
}

::-webkit-scrollbar-thumb:hover {
  background: #9b9b9b;
}

@media screen and (max-width: 800px) {
  .modals {
    width: 400px;
    height: 500px;
    overflow-y: scroll;
    overflow-x: hidden;
    padding-bottom: 3rem;
  }

  .modal-header::-webkit-scrollbar {
    height: 12px;
  }

  h1 {
    padding-bottom: 5px;
  }

  .modal-body {
    display: block;
  }

  .itbkk-assignees {
    width: 21rem;
  }

  .descinput {
    width: 21rem;
    height: 20rem;
  }
}
</style>
