<script setup>
import { ref, defineProps, defineEmits } from "vue";
import { useRoute } from "vue-router";

const props = defineProps({
  title: String,
  description: String,
  assignees: String,
  status: String,
  createdOn: String,
  updatedOn: String,
  localTimeZone: String,
});

const route = useRoute();
const id = route.params.id;

console.log(id);

console.log("Task ID:", id);

const handleSubmit = () => {
  // ส่งข้อมูลที่กรอกในฟอร์มไปยัง API หรือทำการประมวลผลตามที่ต้องการ
}

const closeModal = () => {
  // ปิด Modal เมื่อไม่ต้องการใช้งาน
}
</script>

<template>
  <div class="details fixed inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div class="flex items-start justify-center mt-24 text-center">
      <div
        class="modals rounded-lg text-center shadow-xlp-6 w-min"
        role="dialog"
        aria-modal="true"
      >
        <div class="modal-content">
          <form @submit.prevent="handleSubmit">
            <div class="modal-header">
              <div class="itbkk-title text-left">
                <h3>Add New Task</h3>
              </div>
            </div>
            <hr />

            <div class="modal-body">
              <div class="itbkk-description text-left">
                <div class="flex flex-wrap -mx-3 mb-2">
                  <div class="desc w-full md:w-1/3 px-3 mb-6 md:mb-0">
                    <label
                      class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                      for="grid-city"
                    >
                      Description
                    </label>
                    <textarea class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                                    id="description" type="text" placeholder="Albuquerque" ></textarea>
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
                        v-model="props.assignees"
                        class="itbkk-assignees ass appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 leading-tight focus:bg-white focus:outline-none"
                        id="assign"
                        type="text"
                        placeholder="Enter assignees..."
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
                        v-model="props.status"
                        class="statusbx block w-60 bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                        id="grid-state"
                      >
                        <option value="NO_STATUS">No Status</option>
                        <option value="TO_DO">To Do</option>
                        <option value="DOING">Doing</option>
                        <option value="DONE">Done</option>
                      </select>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer">
              <div class="itbkk-button">
                <hr />
                <button @click="closeModal" class="close border">Close</button>
                <button type="submit" class="ok border">Add</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");

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

  h3 {
    color: #47467b;
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
