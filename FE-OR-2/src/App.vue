<script setup>
import Bg_effect from "./components/css/Background.vue";
import Badges from "./components/css/Badge.vue";
import Copyright from "./components/css/Copyright.vue";
import ModalDetail from "./components/modal/ModalDetail.vue";
import { onMounted, ref } from "vue";

const tasks = ref([]);

async function fetchData() {
  const response = await fetch("http://localhost:8080/api/v1/tasks");
  const data = await response.json();
  return data;
}

onMounted(async () => {
  const data = await fetchData();
  tasks.value = data;
});

const show = ref(false);

const selectedTask = ref(null);

const openModal = (task) => {
  show.value = true;
  selectedTask.value = task;
};

const closeModal = () => {
  show.value = false;
  selectedTask.value = null;
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
            <tr v-for="task in tasks" class="itbkk-item">
              <td>{{ task.id }}</td>
              <td class="itbkk-title">
                <p @click="openModal">{{ task.title }}</p>
              </td>
              <td class="itbkk-assignees">
                {{ task.assignees?.trim() || "Unassigned" }}
              </td>
              <td class="itbkk-status"><Badges :status="task.status" /></td>
            </tr>
          </tbody>
        </table>
      </div>
      <ModalDetail v-if="show" @close="closeModal"></ModalDetail>
    </main>
    <Copyright />
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
  font-style: italic;
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

header {
  margin-bottom: 20px;
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
}
table {
  width: auto;
  max-width: 800px;
  border-collapse: collapse;
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
</style>
