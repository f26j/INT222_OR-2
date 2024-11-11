<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useTaskStore } from "@/stores/TaskStore.js";
import { useStatusStore } from "@/stores/StatusStore.js";
import { useToastStore } from "@/stores/ToastStore.js";
import { useUserStore } from "@/stores/AuthUserStore.js";

// Router & Store
const route = useRoute();
const router = useRouter();
const taskStore = useTaskStore();
const statusStore = useStatusStore();
const toastStore = useToastStore();
const userStore = useUserStore();

// Variables
const boardId = route.params.boardId;
const taskId = route.params.taskId;

const modalDetail = ref({
  title: "",
  description: "",
  assignees: "",
  status: { id: null, name: "" },
});

const originalTask = ref({});
const localTimeZone = ref(Intl.DateTimeFormat().resolvedOptions().timeZone);

// Fetch task details

async function fetchTaskDetail() {
  try {
    const task = await taskStore.fetchTaskDetail(boardId, taskId);
    if (!task) {
      router.push({ name: "NotFound" });
      return;
    }
    modalDetail.value = task;
    originalTask.value = JSON.parse(JSON.stringify(task)); // Clone original task
  } catch (error) {
    console.error("Error fetching task details:", error);
    router.push("/");
  }
}

// Save task changes
async function saveTaskChanges() {
  try {
    const updatedTask = {
      title: modalDetail.value.title.trim(),
      description: modalDetail.value.description.trim(),
      assignees: modalDetail.value.assignees.trim(),
      status: statusStore.statuses.find(
        (status) => status.id === modalDetail.value.status.id
      ),
    };

    await taskStore.updateTask(boardId, taskId, updatedTask);
    toastStore.addToast("Task updated successfully", "success");
    closeModal();
  } catch (error) {
    console.error("Error updating task:", error);
   
  }
}

// Check if form is changed
function isFormChanged() {
  return (
    modalDetail.value.title !== originalTask.value.title ||
    modalDetail.value.description !== originalTask.value.description ||
    modalDetail.value.assignees !== originalTask.value.assignees ||
    modalDetail.value.status.id !== originalTask.value.status.id
  );
}

// Format date
function formatDate(date) {
  if (!date) return "N/A";
  const d = new Date(date);
  return d.toLocaleString("en-GB", { timeZone: localTimeZone.value }).split(",").join(" ");
}

// Close modal
const closeModal = () => {
  router.push({ name: "board-tasks", params: { boardId } });
};

// On mounted
onMounted(async () => {
  if (!userStore.token) {
    return router.push({ name: "AccessDenied" });
  }
  await statusStore.fetchStatuses(boardId);
  await fetchTaskDetail();
});
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div class="modals rounded-lg text-center shadow-xlp-6 w-min" role="dialog" aria-modal="true">
      <div class="modal-content">
        <form @submit.prevent="saveTaskChanges">
          <div class="modal-header">
            <div class="itbkk-title text-left pl-4">
              <h1>
                <input
                  v-model="modalDetail.title"
                  class="border-0 text-left p-2 rounded-sm"
                  placeholder="Enter task title"
                />
              </h1>
            </div>
          </div>
          <hr />

          <div class="modal-body">
            <div class="itbkk-description text-left">
              <div class="flex flex-wrap -mx-3 mb-2">
                <div class="desc w-full md:w-1/3 px-3 mb-6 md:mb-0">
                  <label class="block uppercase tracking-wide text-xs font-bold mb-2">
                    Description
                  </label>
                  <textarea
                    v-model="modalDetail.description"
                    class="itbkk-description descinput appearance-none block w-full bg-gray-200 border rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white"
                    placeholder="Enter task description"
                  ></textarea>
                </div>
              </div>
            </div>

            <div class="detailright flex-col">
              <div class="assign flex flex-wrap -mx-3 mb-2">
                <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                  <label class="block uppercase tracking-wide text-xs font-bold mb-2">
                    Assignees
                  </label>
                  <textarea
                    v-model="modalDetail.assignees"
                    class="itbkk-assignees ass appearance-none block w-full bg-gray-200 border rounded py-3 px-4 leading-tight focus:outline-none"
                    placeholder="Enter assignees"
                  ></textarea>
                </div>
              </div>

              <div class="itbkk-status text-left" v-if="statusStore.statuses.length">
                <div class="w-full md:w-1/3 mb-6 md:mb-0">
                  <label class="block uppercase tracking-wide text-xs font-bold mb-2">
                    Status
                  </label>
                  <select
                    v-model="modalDetail.status.id"
                    class="statusbx block w-60 bg-gray-200 border rounded py-3 px-4 pr-8 leading-tight focus:outline-none focus:bg-white"
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

              <div class="time">
                <div class="itbkk-timezone text-left">
                  <p>Timezone: {{ localTimeZone }}</p>
                </div>
                <div class="itbkk-created-on text-left">
                  <p>Created On: {{ formatDate(modalDetail.created_on) }}</p>
                </div>
                <div class="itbkk-updated-on text-left">
                  <p>Updated On: {{ formatDate(modalDetail.updated_on) }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <div class="itbkk-button">
              <hr />
              <button @click.prevent="closeModal" class="close border">Cancel</button>
              <button
                type="submit"
                class="ok border itbkk-button"
                :disabled="!isFormChanged()"
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
  background-color: #bfb8da;
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

.ok {
  width: 4rem;
  background-color: rgb(179, 236, 168);
  color: rgb(15, 82, 21);
  margin: 10px;
  float: right;
  border-radius: 50px;
}

.itbkk-button-confirm:disabled {
  background-color: #a8a8a8;
  color: #ffffff;
}
.itbkk-button-confirm:disabled:hover {
  transform: scale(1);
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

.ok:hover {
  background-color: rgb(28, 126, 36);
  color: rgb(179, 236, 168);
}

.close:hover {
  background-color: rgb(163, 42, 48);
  color: rgb(248, 195, 199);
}

.itbkk-button:disabled {
  background-color: #a8a8a8;
  color: #ffffff;
}
.itbkk-button:disabled:hover {
  transform: scale(1);
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
