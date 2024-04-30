<script setup>
import Bg_effect from "../components/css/Background.vue";
import Badges from "../components/css/Badge.vue";
import Copyright from "../components/css/Copyright.vue";
import ModalDetail from "./ModalDetail.vue";
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
    alert("The requested task does not exist");
    router.push("/");
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
      <div class="addtask">
        <button class="btna">ADD TASK</button>
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
              <td class="bgd"><span class="bgi">id : </span>{{ task.id }}</td>
              <td class="itbkk-title">
                <p @click="openModal(task.id)" value="id">{{ task.title }}</p>
              </td>
              <td class="itbkk-assignees">
                <span v-if="!task.assignees?.trim()" class="itbkk-assignees unassigned"
                  >Unassigned</span
                >
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
        @close="closeModal"
      ></ModalDetail>
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

::-webkit-scrollbar {
  width: 0px;
}
::-webkit-scrollbar-thumb {
  background: #333;
}
.itbkk-assignees {
  font-style: normal;
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

header {
  margin-bottom: 20px;
}

.btna {
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
}
.addtask {
  margin-right: 45rem;
  text-align: center;
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
  /* table-layout: fixed; */
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
}
</style>
