<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToastStore } from "@/stores/ToastStore.js";
import { useTaskStore } from "@/stores/TaskStore.js";

const router = useRouter();
const route = useRoute();
const taskStore = useTaskStore(); 
const toastStore = useToastStore();

const boardId = route.params.boardId;

// Form Data
const newTitle = ref("");
const newDescription = ref("");
const newAssignees = ref("");
const newStatus = ref({ id: 1, name: "No Status" });

// Fetch statuses when component is mounted
onMounted(async () => {
  try {
    await taskStore.fetchTasks(boardId); // Fetch tasks using TaskStore
    const statuses = await taskStore.fetchTasks(boardId);
    if (statuses.length) {
      newStatus.value = statuses[0];
    }
  } catch (error) {
    console.error("Error fetching tasks:", error);
  }
});

// Add new task logic
const addTask = async () => {
  if (!newTitle.value.trim()) {
    toastStore.addToast("Title is required", "error");
    return;
  }

  const taskData = {
    title: newTitle.value.trim(),
    description: newDescription.value.trim() || null,
    assignees: newAssignees.value.trim() || null,
    status: { id: newStatus.value.id },
  };

  try {
    await taskStore.addTask(boardId, taskData); // Use TaskStore to add task
    toastStore.addToast("Task added successfully", "success");
    router.push({ name: "board-tasks", params: { boardId } });
  } catch (error) {
    console.error("Error adding task:", error);
    toastStore.addToast("Failed to add task", "error");
  }

  // Reset form
  newTitle.value = "";
  newDescription.value = "";
  newAssignees.value = "";
  newStatus.value = { id: 1, name: "No Status" };
};

// Close modal
const closeModal = () => {
  router.push({ name: "board-tasks", params: { boardId } }).catch(() => {});
};
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div
      class="itbkk-modal-task modals rounded-lg text-center shadow-xl w-min"
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
                  <label class="block uppercase tracking-wide text-xs font-bold mb-2">
                    Title
                  </label>
                  <textarea
                    class="ttitle block w-full bg-gray-200 border rounded py-3 px-4 leading-tight focus:bg-white"
                    placeholder="Enter title..."
                    v-model="newTitle"
                  ></textarea>
                </div>
              </div>

              <div class="itbkk-description text-left">
                <div class="flex flex-wrap -mx-3 mb-2">
                  <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label class="block uppercase tracking-wide text-xs font-bold mb-2">
                      Description
                    </label>
                    <textarea
                      class="itbkkdesc block w-full bg-gray-200 border rounded py-3 px-4 leading-tight focus:bg-white"
                      placeholder="Enter description..."
                      v-model="newDescription"
                    ></textarea>
                  </div>
                </div>
              </div>
            </div>

            <div class="detailright flex-col">
              <div class="text-left">
                <div class="assign flex flex-wrap -mx-3 mb-2">
                  <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label class="block uppercase tracking-wide text-xs font-bold mb-2">
                      Assignees
                    </label>
                    <textarea
                      class="itbkkassignees block w-full bg-gray-200 border rounded py-3 px-4 leading-tight focus:bg-white"
                      placeholder="Enter assignees..."
                      v-model="newAssignees"
                    ></textarea>
                  </div>
                </div>
              </div>

              <div class="text-left">
                <div class="w-full md:w-1/3 mb-6">
                  <label class="block uppercase tracking-wide text-xs font-bold mb-2">
                    Status
                  </label>
                  <div class="relative">
                    <select
                      class="itbkk-status block w-60 bg-gray-200 border py-3 px-4 pr-8 rounded focus:bg-white"
                      v-model="newStatus"
                    >
                      <option
                        v-for="status in taskStore.tasks"
                        :key="status.id"
                        :value="status"
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
              <button @click="closeModal" class="close">Cancel</button>
              <button
                class="itbkk-button-confirm"
                type="submit"
                :disabled="!newTitle.length"
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