<script setup>
import Bg_effect from "../components/css/Background.vue";
import Badges from "../components/css/Badge.vue";
import Copyright from "../components/css/Copyright.vue";
import ModalDetail from "./ModalDetail.vue";
import ModalAdd from "./ModalAdd.vue";

import { onMounted, ref } from "vue";
import { useRouter, useRoute } from "vue-router";


const fetchURL = ref(import.meta.env.VITE_API)
const router = useRouter();
const tasks = ref([]);
const route = useRoute();
const title = ref("");
const description = ref("");
const assignees = ref("");
const status = ref("");
const createdOn = ref("");
const updatedOn = ref("");
const taskId = route.params.id;
const localTimeZone = ref(Intl.DateTimeFormat().resolvedOptions().timeZone);
console.log(taskId);

function formatDate(date) {
  const d = new Date(date);
  return d
    .toLocaleString("en-GB", { timeZone: localTimeZone.value })
    .split(",")
    .join(" ");
}

async function fetchData() {
  const response = await fetch(`${fetchURL.value}`);
  const data = await response.json();
  return data;
}

onMounted(async () => {
  const data = await fetchData();
  tasks.value = data;
});

const show = ref(false);

const openModal = async (id) => {
  const data = await fetch(`${fetchURL.value}/${id}`);
  const res = await data.json();
  if (!data.ok) {
    router.push("/");
    setTimeout(() => {
      alert("The requested task does not exist");
    }, 500);
    return;
  }

  console.log(data);
  console.log(res);
  title.value = res.title;
  description.value = res.description;
  assignees.value = res.assignees;
  status.value = res.status;
  createdOn.value = formatDate(res.createdOn);
  updatedOn.value = formatDate(res.updatedOn);
  show.value = true;
  router.push({ name: "task-detail", params: { id: id } });
  console.log(title.value);
};
if (taskId) {
  openModal(taskId);
}
const openModalAdd = async () => {
  title.value = "";
  description.value = "";
  assignees.value = "";
  status.value = "NO_STATUS"; 
  createdOn.value = formatDate(new Date());
  updatedOn.value = formatDate(new Date());

  show.value = true;
  router.push({ name: "task-add" });
}


const closeModal = () => {
  show.value = false;
  router.push({ name: "task" });
};
</script>

<template>
  <div class="body">
    <header>
      <hr class="my-6 sm:mx-auto border border-white/20 lg:my-8" />
      <h1 class="text-center">IT-Bangmod Kradan Kanban</h1>
      <hr class="my-6 sm:mx-auto border border-white/20 lg:my-8" />
    </header>
    <main>
      <div class="btn-a"    v-for="task in tasks">
        <div class="button iconbutton" @click="openModalAdd()">
          <i class="plus">
            <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24"><g fill="none">
              <path d="M24 0v24H0V0zM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z"></path>
              <path fill="currentColor" d="M10.5 20a1.5 1.5 0 0 0 3 0v-6.5H20a1.5 1.5 0 0 0 0-3h-6.5V4a1.5 1.5 0 0 0-3 0v6.5H4a1.5 1.5 0 0 0 0 3h6.5z"></path>
            </g></svg>
          </i>
        </div>
      </div>
      <div class="btn-a">
        <div @click="openModal()" class="button iconbutton" >
          <i class="plus">+</i>
        </div>
      </div>
      <div class="tb">
        <table>
          <thead>
            <tr>
              <th>id</th>
              <th>Title</th>
              <th>Assignees</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="task in tasks"
              :key="task.id"
              class="itbkk-item"
              :data-task-id="task.id"
            >
              <td class="bgd">
                <span class="bgi">id : </span>
                <div class="idkb">
                  {{ task.id }}
                  <span class="kebab">
                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 20 20">
                    <path fill="currentColor" d="M10 9a2 2 0 1 1 0-4a2 2 0 0 1 0 4m0 6a2 2 0 1 1 0-4a2 2 0 0 1 0 4"></path></svg>
                  </span>
                </div>
              </td>
              <td class="itbkk-title">
                <p @click="openModal(task.id)" value="id">{{ task.title }}</p>
              </td>
              <td class="itbkk-assignees">
                <span v-if="!task.assignees?.trim()" class="itbkk-assignees unassigned">
                  Unassigned
                </span>
                <span v-else>{{ task.assignees }}</span>
              </td>
              <td class="itbkk-status"><Badges :status="task.status" /></td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <ModalDetail
        v-if="show"
        :title="title"
        :description="description"
        :assignees="assignees"
        :status="status"
        :createdOn="createdOn"
        :updatedOn="updatedOn"
        :localTimeZone="localTimeZone"
        @close="closeModal">
      </ModalDetail>
      <ModalAdd
        @close="closeModal"
      ></ModalAdd>
    </main>

    <footer>
      <Copyright />
    </footer>
    <Bg_effect />
  </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");

* {
  user-select: none;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  scroll-behavior: smooth;
}

.unassigned {
  font-style: italic;
  color: gray;
}

.addtask {
  margin-right:45rem;
  text-align: center;
}

.plus {
  font-size: 40px;
  font-weight: 10px;
  color: #d1ffca;
}

.btn-a {
  position:fixed;
  bottom:50px;
  right:50px;
  cursor: pointer;
}

.iconbutton {
  width:50px;
  height:50px;
  border-radius: 100%;
  box-shadow: 5px 5px 5px #00000046;
}

.button {
  width:80px;
  height:80px;
  background:#ad386f;
}

.iconbutton i {
  display:flex;
  align-items:center;
  justify-content:center;
  height: 100%;
  color:white;
}

.button:hover {
  background-color: #66163b; 
  color: white;
} 

/* ::-webkit-scrollbar {
  width: 0px;
}
::-webkit-scrollbar-thumb {
  background: #333;
} */

.itbkk-assignees {
  font-style: normal;
  width: auto;
}

.itbkk-title p {
  transition: transform 0.3s ease;
  color: #ad386f;
}

.itbkk-title p:hover {
  cursor: pointer;
  transform: scale(1.01);
  color: rgb(250, 133, 188);
}

.itbkk-item p {
  width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.itbkk-title {
  max-width: 700px;
}

.unassigned {
  color: gray;
  font-style: italic;
}

header {
  margin-bottom: 20px;
}

/* .btna {
  background-color: #04aa6d76;
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  border-radius: 50px;
  border: 2px solid #04AA6D;
}

.btna:hover {
  background-color: #adcdc1;
  color: white;
} */

.addtask {
  margin-right: 45rem;
  text-align: center;
}

.kebab {
  color: #939393;
}

.idkb {
  display: inline;
}

h1 {
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 40px;
  color: #47467b;
  font-weight: 800;
  margin: 4rem 0 2rem;
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
  color: #47467b;
  font-family: "Ubuntu", sans-serif;
  font-weight: 600;
  font-style: normal;
}

thead th {
  background-color: #47467b;
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
    background-color: #47467b;
    color: rgba(255, 255, 255, 0.774);
  }

  .bgi {
    display: inline;
    margin-left: -1rem;
  }

  .button {
  width:60px;
  height:60px;
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
  z-index: 2;
  position: fixed;
}

  .tb {
    z-index: 1;
    position: relative;
  }
}
</style>
