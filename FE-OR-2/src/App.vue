<script setup>
import {onMounted, ref} from 'vue'
const tasks = ref([]);
async function fetchData() {
  const response = await fetch('http://localhost:8080/api/v1/tasks');
  const data = await response.json();
  console.log(data)
  return data;
}

onMounted(async () => {
  const data = await fetchData()
  tasks.value = data
})

console.log(tasks.value);

let doing = document.getElementById('stt')

</script>

<template>
  <div class="body">
    <header>
      <br>
      <h1 class="text-center">IT-Bangmod Kradan Kanban</h1>  
    </header>
    <br>
    <hr class="style">
    <br>
    <div class="tb">
      <table>
        <thead>
          <tr>
              <th class="pad">id</th>
              <th class="pad">Title</th>
              <th class="pad">Assignees</th>
              <th class="pad">Status</th>
          </tr>
        </thead>
        <tbody>
            <tr class="itbkk-item" v-for="task in tasks">
              <td>{{task.id}}</td>
              <td class="itbkk-title">{{task.title}}</td>
              <td class="itbkk-assignees">{{task.assignees}}</td>
              <td class="itbkk-status">
                <span class="relative inline-block px-3 py-1 font-semibold text-green-900 leading-tight">
                  <span aria-hidden class="absolute inset-0 bg-green-200 opacity-50 rounded-full"></span>
                  <span class="relative">{{task.status}}</span>
                </span>
              </td>
              
            </tr>          
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono');
@import url('https://fonts.googleapis.com');
@import url('https://fonts.gstatic.com');
@import url('https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap');

/* * {
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
  background: #bb4d4d;
} */

.body {
  min-height: 100vb;
  margin: 0;
  padding: 0;
  background: linear-gradient(45deg, #9b9bd8, #ffd6e0);
	font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
}

h1 {
  font-family: "Ubuntu", sans-serif;
  font-weight: 500;
  font-style: normal;
  font-size: 40px;
  color: #47467B;
  font-weight: 800;
}

header {
  margin-bottom:20px;
}

hr.style {
  border-top: 6px solid #ad386f;
  margin-bottom: 40px;
  margin-left: 100px;
  margin-right: 100px;
}


table {
  width: 800px;
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
  color:#ad386f;
  font-family: "Ubuntu", sans-serif;
  font-weight: 600;
  font-style: normal;

}

thead {
	th {
		background-color: #47467B;
	}
}

tbody {
	tr {
		&:hover {
			background-color: rgba(255, 168, 229, 0.63);
		}
	}
	td {
		position: relative;
		&:hover {
			&:before {
				content: "";
				position: absolute;
				left: 0;
				right: 0;
				top: -9999px;
				bottom: -9999px;
				background-color: rgba(255, 255, 255, 0.726);
				z-index: -1;
			}
		}
	}
}

</style>
