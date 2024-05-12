<script setup>
import Bg_effect from "../components/css/Background.vue";
import HomeView from "../views/HomeView.vue";
import Copyright from "../components/css/Copyright.vue";
import { useRouter, useRoute } from "vue-router";
import { onMounted, ref, onUnmounted } from "vue";
import { useCounterStore } from "@/stores/counter.js"

const store = useCounterStore()

const router = useRouter();
const fetchURL = ref(import.meta.env.VITE_API);
async function fetchData() {
  const response = await fetch(`${fetchURL.value}`);
  const data = await response.json();
  return data;
}
onMounted(async () => {
  const data = await fetchData();
  store.tasks.value = data
  console.log(store.tasks.value);
});
const openHome = () => {
  router.push({path: "/task"});
}
const showAdd = ref(false);
const openModalAdd = async (task) => {

  router.push({ name: "status-add" });
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
      <div class="btn-stt flex">
        <button type="button" class="itbkk-button-home flex" @click="openHome">
            <svg class="homeicon" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 20 20">
              <path fill="currentColor" d="m16 8.5l1.53 1.53l-1.06 1.06L10 4.62l-6.47 6.47l-1.06-1.06L10 2.5l4 4v-2h2zm-6-2.46l6 5.99V18H4v-5.97zM12 17v-5H8v5z"></path>
            </svg>
            <span class="hometext">HOME</span>
        </button>
        <span style="padding-left: 1rem;"></span>
        <button type="button" class="itbkk-button-add flex" @click="">
          <svg class="addicon" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 14 14">
            <path fill="currentColor" fillRule="evenodd" d="M8 1a1 1 0 0 0-2 0v5H1a1 1 0 0 0 0 2h5v5a1 1 0 1 0 2 0V8h5a1 1 0 1 0 0-2H8z" clipRule="evenodd"></path>
          </svg>
          <span class="addtext" @click="openModalAdd(task)">ADD</span>
        </button>
        <h3>Task Status</h3>
      </div>

      <div class="tb">
        <table>
          <thead>
            <tr>
              <th></th>
              <th>Name</th>
              <th>Description</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(task, index) in store.tasks.value" class="itbkk-item" >
            <td class="bgd">
              <span class="bgi">id : </span>
              <div class="idkb flex-">
                {{ index + 1 }}</div>
            </td>
            <td class="itbkk-status-name">
              <span>{{ task.status}}</span>
            </td>
            <td class="itbkk-status-description">
                <span>Nong Ploy Cute</span>
            </td>
              <td class="itbkk-button-action flex">
                <svg
                  class="itbkk-button-edit"
                  xmlns="http://www.w3.org/2000/svg"
                  width="30"
                  height="30"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="currentColor"
                    fill-rule="evenodd"
                    d="M17.907 4.93c.104-.105.294-.186.539-.18a.848.848 0 0 1 .572.216c.13.13.222.34.231.575c.01.234-.065.413-.167.515l-7.49 7.5l-1.273.116l.1-1.245zm2.173-1.024c-.437-.438-1.037-.643-1.6-.656c-.564-.013-1.182.167-1.635.62l-7.682 7.692a.75.75 0 0 0-.217.47l-.194 2.407a.75.75 0 0 0 .816.807l2.43-.22a.75.75 0 0 0 .463-.218l7.682-7.692c.456-.456.627-1.073.605-1.635a2.373 2.373 0 0 0-.668-1.575M5.812 4.75a2.568 2.568 0 0 0-2.563 2.562v10.875a2.568 2.568 0 0 0 2.563 2.563h10.875a2.568 2.568 0 0 0 2.563-2.562V12.75a.75.75 0 0 0-1.5 0v5.438c0 .582-.48 1.062-1.063 1.062H5.812c-.583 0-1.063-.48-1.063-1.062V7.312c0-.582.48-1.062 1.063-1.062h5.437a.75.75 0 0 0 0-1.5z"
                    clip-rule="evenodd"
                  />
                </svg>
                <svg
                  class="itbkk-button-delete"
                  xmlns="http://www.w3.org/2000/svg"
                  width="30"
                  height="30"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="currentColor"
                    d="M20 8.7H4a.75.75 0 1 1 0-1.5h16a.75.75 0 0 1 0 1.5"
                  />
                  <path
                    fill="currentColor"
                    d="M16.44 20.75H7.56A2.4 2.4 0 0 1 5 18.49V8a.75.75 0 0 1 1.5 0v10.49c0 .41.47.76 1 .76h8.88c.56 0 1-.35 1-.76V8A.75.75 0 1 1 19 8v10.49a2.4 2.4 0 0 1-2.56 2.26m.12-13a.74.74 0 0 1-.75-.75V5.51c0-.41-.48-.76-1-.76H9.22c-.55 0-1 .35-1 .76V7a.75.75 0 1 1-1.5 0V5.51a2.41 2.41 0 0 1 2.5-2.26h5.56a2.41 2.41 0 0 1 2.53 2.26V7a.75.75 0 0 1-.75.76Z"
                  />
                  <path
                    fill="currentColor"
                    d="M10.22 17a.76.76 0 0 1-.75-.75v-4.53a.75.75 0 0 1 1.5 0v4.52a.75.75 0 0 1-.75.76m3.56 0a.75.75 0 0 1-.75-.75v-4.53a.75.75 0 0 1 1.5 0v4.52a.76.76 0 0 1-.75.76"
                  />
                </svg>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </main>
    <footer>
      <Copyright />
    </footer>
    <Bg_effect />
  </div>
  <RouterView />
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

h3 {
  font-family: "Ubuntu", sans-serif;
  font-weight: 400;
  font-style: normal;
  font-size: 30px;
  color: #737292;
  text-align: center;
  margin-bottom: 10px;
}

.btn-stt {
  position: relative;
}

.itbkk-button-action {
  padding: 15px 5px 10px;
  display: flex;
}

.itbkk-button-action svg {
  transition: transform 0.3s ease;
  cursor: pointer;
}

.itbkk-button-action svg:hover {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  color: #ad386f;
}


.itbkk-button-home {
  justify-content: center;
  text-align: center;
  cursor: pointer;
  margin-left: 10rem;
  font-style: italic;
  background-color: #343365;
  color: #fff7d9;
  padding: 5px;
  border-radius: 50px;
  width: 120px;
  height: 50px;
}

.itbkk-button-add {
  justify-content: center;
  text-align: center;
  cursor: pointer;
  margin-left: 2rem;
  font-style: italic;
  background-color: #683c86;
  color: #fff7d9;
  padding: 5px;
  border-radius: 50px;
  width: 120px;
  height: 50px;
}

.hometext, .addtext {
  color: #ffe6fc;
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 20px;
  padding-top: 5px;
  padding-right: 10px;
}

.homeicon , .addicon {
  color: #ffedfd;
  width: 40px;
  height: 40px;
  padding-right: 10px;
  padding-left: 5px;
  cursor: pointer;
}

.itbkk-button-home:hover {
  background-color: #636296;
}

.itbkk-button-add:hover {
  background-color: #9b65be;
}

.unassigned {
  font-style: italic;
  color: gray;
}

header {
  margin-bottom: 20px;
}

.idkb {
  display: inline;
}

h1 {
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 40px;
  color: #683c86;
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
  color: #683c86;
  font-family: "Ubuntu", sans-serif;
  font-weight: 600;
  font-style: normal;
  
}

thead th {
  background-color: #8e5bb1;
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
    background-color: #8e5bb1;
    color: rgba(255, 255, 255, 0.774);
  }

  .bgi {
    display: inline;
    margin-left: -1rem;
  }

  .button {
    width: 60px;
    height: 60px;
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
    position: fixed;
  }

  .tb {
    z-index: 1;
    position: relative;
  }
}
</style>