<script setup>
// Import zone
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useTaskStore } from "@/stores/api.js";
import { useToastStore } from "@/stores/toast.js";

// Store,Api zone
const route = useRoute();
const router = useRouter();
const store = useTaskStore();
const toastStore = useToastStore();
const fetchTask = import.meta.env.VITE_API_TASK;
const fetchStatus = import.meta.env.VITE_API_STATUS

// Variable zone
const id = route.params.id;
const modalEdit = ref([""]);
const localTimeZone = ref(Intl.DateTimeFormat().resolvedOptions().timeZone);
const newTitle = ref("");
const newDescription = ref("");
const newAssignees = ref("");
const newStatus = ref({});

const originalTitle = ref("");
const originalAssignee = ref("");
const originalDescription = ref("");
const originalStatus = ref({});

async function fetchDataStatus() {
  const response = await fetch(fetchStatus)
  const dataStatus = await response.json()
  return dataStatus
}

function formatDate(date) {
  const d = new Date(date)
  return d
    .toLocaleString("en-GB", { timeZone: localTimeZone.value })
    .split(",")
    .join(" ")
}

const openModalEdit = async () => {
  const data = await fetch(`${fetchTask}/${id}`);
  const res = await data.json();
  if (!data.ok) {
    router.push("/")
    setTimeout(() => {
      alert("The requested task does not exist")
    }, 500)
    return
  }
  modalEdit.value = res;
  newTitle.value = modalEdit.value.title || "";
  newDescription.value = modalEdit.value.description || "";
  newAssignees.value = modalEdit.value.assignees || ""; 
  newStatus.value = { ...modalEdit.value.status };

  originalTitle.value = modalEdit.value.title || "";
  originalAssignee.value = modalEdit.value.assignees || "";
  originalDescription.value = modalEdit.value.description || "";
  originalStatus.value = { ...modalEdit.value.status };

};

onMounted(async () => {
  openModalEdit();
  const datastatus = await fetchDataStatus()
  store.status.value = datastatus
});

const editTask = async () => {
  const editedTask = {
    id: id,
    title: newTitle.value.trim(),
    description: newDescription.value.trim(),
    assignees: newAssignees.value.trim(),
    status: store.status.value.find(status => status.name == newStatus.value.name),
  };
  if (!editedTask.title) {
    return;
  }
  try {
    const response = await fetch(`${fetchTask}/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(editedTask),
    });
    if (!response.ok) {
      throw new Error("Failed to edit task");
    } else if (response.ok) {
      store.updateTask({ ...editedTask });
    }
    closeModal();
    toastStore.handleTaskEdited();
  } catch (error) {
    console.error("Error editing task:", error.message);
  }
};

const closeModal = () => {
  router.push({ name: "task" });
};
</script>
<template>
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
                    class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                    for="grid-city"
                  >
                    Title
                  </label>
                  <textarea
                    class="ttitle appearance-none block w-full h-32 bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
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
                      class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                      for="grid-city"
                      >Description
                    </label>
                    <textarea
                      class="itbkkdesc appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
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
                      class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                      for="grid-city"
                    >
                      Assignees
                    </label>
                    <textarea
                      class="itbkkassignees appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
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
                    class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                    for="grid-state"
                  >
                    Status
                  </label>
                  <div class="relative itbkk-status">
                    <select
                      class="statusbx block w-60 bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                      id="grid-state"
                      v-model="newStatus.name"
                    >
                      <option v-for="(status, index) in store.status.value" :value="status.name">{{ status.name }}</option>
                    </select>
                  </div>
                </div>
              </div>
              <div class="time">
                <div class="itbkk-timezone text-left">
                  <p>Timezone : {{ localTimeZone }}</p>
                </div>
                <div class="itbkk-created-on text-left">
                  <p>CreatedOn : {{ formatDate(modalEdit.createdOn) }}</p>
                </div>
                <div class="itbkk-updated-on text-left">
                  <p>UpdatedOn : {{ formatDate(modalEdit.updatedOn) }}</p>
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
                    JSON.stringify(newStatus) === JSON.stringify(originalStatus))
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
</template>

<style>
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
  color: #47467b;
  width: 800px;
}

.details {
  color: #47467b;
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
  color: #47467b;
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
