<script setup>
// Import zone
import { ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStatusStore } from "@/stores/StatusStore.js"; // ใช้ StatusStore
import { useToastStore } from "@/stores/ToastStore.js";

// Variable zone
const store = useStatusStore();
const toastStore = useToastStore();
const router = useRouter();
const route = useRoute();
const newName = ref("");
const newDescription = ref("");
const boardId = route.params.boardId;

// ปิด Modal และกลับไปหน้า Status Management
const closeModal = () => {
  router.push({ name: "status-manage" });
};

// ฟังก์ชันเพิ่ม Status ใหม่
const addStatus = async () => {
  const trimmedName = newName.value.trim();
  const trimmedDescription = newDescription.value.trim();

  if (!trimmedName) return; // ถ้าไม่มีชื่อ Status ไม่ให้ดำเนินการต่อ

  const newStatus = {
    name: trimmedName,
    description: trimmedDescription || null, // หากไม่มีคำอธิบายจะกำหนดเป็น null
  };

  try {
    await store.addStatus(boardId, newStatus); // เพิ่ม Status ผ่าน Store
    toastStore.handleStatusAdded(); // แสดงข้อความ Toast ว่าการเพิ่มสำเร็จ
    closeModal(); // ปิด Modal หลังจากเพิ่มเสร็จสิ้น
  } catch (error) {
    console.error("Error adding new status:", error.message);
    toastStore.handleStatusAddedF(); // แสดงข้อความ Toast ว่าการเพิ่มล้มเหลว
    closeModal();
  }

  // รีเซ็ตฟิลด์หลังจากเพิ่มเสร็จ
  newName.value = "";
  newDescription.value = "";
};
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50 z-[999]">
    <div class="modalsAddStt rounded-lg text-center shadow-xlp-6 w-min" role="dialog" aria-modal="true">
      <div class="itbkk-modal-status">
        <form @submit.prevent="addStatus">
          <div class="modal-header">
            <div class="itbkktitle text-left">
              <h4>Add New Status</h4>
            </div>
          </div>
          <hr />

          <div class="modal-body">
            <div class="text-left flex-col">
              <div class="titlee flex flex-wrap -mx-3 mb-2">
                <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                  <label class="block uppercase tracking-wide text-xs font-bold mb-2" for="name">
                    Name
                  </label>
                  <textarea
                    id="name"
                    class="itbkk-status-name ttitle-stt appearance-none block w-full bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
                    placeholder="Enter name... (Required)"
                    v-model="newName"
                    maxlength="50"
                  ></textarea>
                </div>
              </div>

              <div class="text-left">
                <div class="flex flex-wrap -mx-3 mb-2">
                  <div class="desc w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label class="block uppercase tracking-wide text-xs font-bold mb-2" for="description">
                      Description
                    </label>
                    <textarea
                      id="description"
                      class="itbkk-status-description itbkkdesc-stt appearance-none block w-full bg-gray-200 border border-gray-200 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                      placeholder="Enter description..."
                      v-model="newDescription"
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
              <button @click="closeModal" class="itbkk-button-cancel close border">Cancel</button>
              <button
                class="itbkk-button-confirm ok border"
                type="submit"
                :disabled="newName.length === 0"
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