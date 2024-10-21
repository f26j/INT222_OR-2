<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStatusStore } from "@/stores/StatusStore.js";
import { useToastStore } from "@/stores/ToastStore.js";

const store = useStatusStore();
const toastStore = useToastStore();
const router = useRouter();
const route = useRoute();

const newName = ref("");
const newDescription = ref("");
const originalName = ref("");
const originalDescription = ref("");
const isLoaded = ref(false);

const boardId = route.params.boardId;
const statusId = route.params.statusId;

const closeModal = () => {
  store.fetchStatuses(boardId);
  router.push({ name: "board-status", params: { boardId } });
};

const getStatus = async () => {
  try {
    if (isLoaded.value) return;

    await store.fetchStatuses(boardId);
    const status = store.statuses.find((s) => s.id === parseInt(statusId));
    if (!status) throw new Error(`Status with ID ${statusId} not found.`);

    newName.value = status.name;
    newDescription.value = status.description;
    originalName.value = status.name;
    originalDescription.value = status.description;

    isLoaded.value = true;
  } catch (error) {
    console.error(error.message);
    toastStore.addToast(
      "An error has occurred, the status could not be loaded.",
      "error"
    );
    router.push({ name: "board-status", params: { boardId } });
  }
};

onMounted(() => {
  getStatus();
});

const editStatus = async () => {
  const trimmedName = newName.value.trim();
  const trimmedDescription = newDescription.value.trim();

  if (!trimmedName) {
    toastStore.addToast("Name is required.", "error");
    return;
  }

  const updatedStatus = {
    id: statusId,
    name: trimmedName,
    description: trimmedDescription || null,
  };

  try {
    await store.updateStatus(boardId, statusId, updatedStatus);
    store.updateStatusInStore(updatedStatus); // อัปเดต Store
    toastStore.addToast("The status has been edited", "success");
    closeModal();
  } catch (error) {
    console.error("Error editing status:", error.message);
    toastStore.addToast(
      "An error has occurred, the status could not be edited.",
      "error"
    );
  }
};
</script>


<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50 z-[999]">
    <div class="modalsAddStt rounded-lg text-center shadow-xlp-6 w-min" role="dialog" aria-modal="true">
      <div class="itbkk-modal-status">
        <form @submit.prevent="editStatus">
          <div class="modal-header">
            <div class="itbkktitle text-left">
              <h4>Edit Status</h4>
            </div>
          </div>
          <hr />

          <div class="modal-body">
            <div class="text-left flex-col">
              <div class="titlee flex flex-wrap -mx-3 mb-2">
                <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                  <label for="name" class="block uppercase tracking-wide text-xs font-bold mb-2">Name</label>
                  <textarea
                    id="name"
                    v-model="newName"
                    class="itbkk-status-name ttitle-stt appearance-none block w-full bg-gray-200 border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
                    placeholder="Enter name... (Required)"
                    maxlength="50"
                  ></textarea>
                </div>
              </div>

              <div class="text-left">
                <div class="flex flex-wrap -mx-3 mb-2">
                  <div class="desc w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label for="description" class="block uppercase tracking-wide text-xs font-bold mb-2">Description</label>
                    <textarea
                      id="description"
                      v-model="newDescription"
                      class="itbkk-status-description itbkkdesc-stt appearance-none block w-full bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                      placeholder="Enter description..."
                      maxlength="200"
                    ></textarea>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <div class="itbkk-button">
              <hr />
              <button type="button" @click="closeModal" class="itbkk-button-cancel close border">
                Cancel
              </button>
              <button
                class="itbkk-button-confirm ok border"
                type="submit"
                :disabled="newName.trim().length === 0 || 
                            (newName === originalName && newDescription === originalDescription)"
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

.modalsAddStt {
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

.itbkk-message {
  color: black;
}

.titlee {
  padding: 10px;
}

.ttitle-stt {
  width: 760px;
  height: 50px;
}

.itbkkdesc-stt {
  width: 750px;
  height: 200px;
}

.time {
  padding-left: 15px;
  padding-bottom: 10px;
}

.itbkk-timezone {
  justify-content: center;
  align-items: center;
}

.itbkk-created-on {
  justify-content: center;
  align-items: center;
}

.ok {
  width: 4rem;
  background-color: rgb(179, 236, 168);
  color: rgb(15, 82, 21);
  margin: 10px;
  float: right;
  border-radius: 50px;
  transition: transform 0.3s ease;
}

.close {
  width: 4rem;
  background-color: #cfcfcf;
  color: #4b4b4b;
  margin: 10px;
  margin-right: 20px;
  float: right;
  border-radius: 50px;
  transition: transform 0.3s ease;
}

.ok:hover {
  background-color: rgb(28, 126, 36);
  color: rgb(179, 236, 168);
}

.ok:disabled {
  background-color: rgba(128, 128, 128, 0.8);
  color: #4b4b4b89;
}
.ok:disabled:hover {
  transform: scale(1);
}

.close:hover {
  background-color: #b7b5b5;
  color: #4b4b4b;
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

h4 {
  color: black;
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
  .modalsAddStt {
    width: 400px;
    height: 540px;
    overflow-y: scroll;
    overflow-x: hidden;
    padding-bottom: 3rem;
  }

  .modalsAddStt::-webkit-scrollbar {
    width: 0px;
  }

  h1 {
    padding-bottom: 5px;
  }

  .modal-body {
    display: block;
  }

  .ttitle-stt {
    width: 22rem;
    height: 4rem;
    overflow: hidden;
  }

  .itbkkdesc-stt {
    width: 22rem;
    height: 12rem;
    overflow: hidden;
  }
}
</style>
