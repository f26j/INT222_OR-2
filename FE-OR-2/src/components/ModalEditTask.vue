<script setup>
// Import zone
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useTaskStore } from "@/stores/apiController.js";
import { useToastStore } from "@/stores/ToastStore.js";

// Store and Router
const route = useRoute();
const router = useRouter();
const store = useTaskStore();
const toastStore = useToastStore();
const boardId = route.params.boardId;
const taskId = route.params.taskId || "";
const baseUrl = import.meta.env.VITE_API_BOARDS;

// Variables
const modalEdit = ref(null);
const localTimeZone = ref(Intl.DateTimeFormat().resolvedOptions().timeZone);
const newTitle = ref("");
const newDescription = ref("");
const newAssignees = ref("");
const newStatus = ref({});

const originalTitle = ref("");
const originalAssignee = ref("");
const originalDescription = ref("");
const originalStatus = ref({});

// Fetch with Token
async function fetchWithToken(url, options = {}) {
  const token = localStorage.getItem("jwtToken");
  if (!token) return null;

  try {
    const response = await fetch(url, {
      ...options,
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
        ...options.headers,
      },
    });

    if (response.status === 401) {
      router.push("/login");
      return null;
    } else if (response.status === 403) {
      router.push({ name: "AccessDenied" });
      return null;
    }

    return response;
  } catch (error) {
    console.error("Error fetching data:", error);
    toastStore.addToast("Failed to fetch data", "error");
    return null;
  }
}

// Fetch Status Data
async function fetchDataStatus() {
  const response = await fetchWithToken(`${baseUrl}/v3/boards/${boardId}/statuses`);
  if (!response) return;

  if (response.status === 404) {
    router.push({ name: "NotFound" });
    return;
  }

  const dataStatus = await response.json();
  if (!Array.isArray(dataStatus)) {
    throw new TypeError("Fetched status data is not an array");
  }

  return dataStatus;
}

// Open Modal and Fetch Task Data
const openModalEdit = async () => {
  if (!taskId) {
    console.error("Task ID is missing");
    return;
  }

  const response = await fetchWithToken(`${baseUrl}/v3/boards/${boardId}/tasks/${taskId}`);
  if (!response) return;

  if (!response.ok) {
    toastStore.addToast("The requested task does not exist", "error");
    router.push("/");
    return;
  }

  const res = await response.json();
  modalEdit.value = res;
  newTitle.value = res.title || "";
  newDescription.value = res.description || "";
  newAssignees.value = res.assignees || "";
  newStatus.value = { ...res.status };

  originalTitle.value = res.title || "";
  originalAssignee.value = res.assignees || "";
  originalDescription.value = res.description || "";
  originalStatus.value = { ...res.status };
};

// Edit Task
const editTask = async () => {
  const editedTask = {
    id: taskId,
    title: newTitle.value.trim(),
    description: newDescription.value.trim(),
    assignees: newAssignees.value.trim(),
    status: store.status.value.find((status) => status.name === newStatus.value.name),
  };

  if (!editedTask.title) {
    toastStore.addToast("Title is required", "error");
    return;
  }

  try {
    const response = await fetchWithToken(`${baseUrl}/v3/boards/${boardId}/tasks/${taskId}`, {
      method: "PUT",
      body: JSON.stringify(editedTask),
    });

    if (!response.ok) {
      throw new Error("Failed to edit task");
    }

    store.updateTask({ ...editedTask });
    closeModal();
    toastStore.addToast("Task edited successfully", "success");
  } catch (error) {
    console.error("Error editing task:", error.message);
    toastStore.addToast("Failed to edit task", "error");
  }
};

// Close Modal
const closeModal = () => {
  router.push({ name: "board-tasks", params: { boardId } });
};

// On Component Mount
onMounted(async () => {
  await openModalEdit();
  const statuses = await fetchDataStatus();
  store.status.value = statuses;
});
</script>
<template>
  <div
    v-if="modalEdit"
    class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50"
  >
    <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
      <div
        class="modals rounded-lg text-center shadow-xlp-6 w-min"
        role="dialog"
        aria-modal="true"
      >
        <div class="modal-content">
          <form @submit.prevent="editTask">
            <div class="modal-header">
              <div class="itbkktitle text-left">
                <h4>Edit Task</h4>
              </div>
            </div>
            <hr />

            <div class="modal-body">
              <div class="text-left flex-col">
                <div class="titlee flex flex-wrap -mx-3 mb-2">
                  <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label
                      class="block uppercase tracking-wide  text-xs font-bold mb-2"
                      for="grid-city"
                    >
                      Title
                    </label>
                    <textarea
                      class="ttitle appearance-none block w-full h-32 bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
                      id="assign"
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

                <div class="itbkk-status text-left">
                  <div class="w-full md:w-1/3 mb-6 md:mb-0">
                    <label
                      class="block uppercase tracking-wide text-xs font-bold mb-2"
                      for="grid-state"
                    >
                      Status
                    </label>
                    <div class="relative itbkk-status">
                      <select
                        class="statusbx block w-60 bg-gray-200 border border-gray-200 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                        id="grid-state"
                        v-model="newStatus.name"
                      >
                        <option
                          v-for="(status, index) in store.status.value"
                          :value="status.name"
                        >
                          {{ status.name }}
                        </option>
                      </select>
                    </div>
                  </div>
                </div>
                <div class="time">
                  <div class="itbkk-timezone text-left">
                    <p>Timezone : {{ localTimeZone }}</p>
                  </div>
                  <div class="itbkk-created-on text-left">
                    <p>CreatedOn : {{ formatDate(modalEdit.created_on) }}</p>
                  </div>
                  <div class="itbkk-updated-on text-left">
                    <p>UpdatedOn : {{ formatDate(modalEdit.updated_on) }}</p>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer">
              <div class="itbkk-button">
                <hr />
                <button @click="closeModal" class="close border">Cancel</button>
                <button
                  class="ok border"
                  type="submit"
                  :disabled="
                    newTitle?.length === 0 ||
                    (newTitle === originalTitle &&
                      newAssignees === originalAssignee &&
                      newDescription === originalDescription &&
                      JSON.stringify(newStatus) ===
                        JSON.stringify(originalStatus))
                  "
                >
                  Save
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");

textarea::placeholder {
  font-style: italic;
  color: gray;
}

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

.ok:disabled {
  color: white;
  background-color: #a8a8a8;
}

.ok:disabled:hover {
  color: white;
  background-color: #a8a8a8;
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