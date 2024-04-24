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
    
  
</script>

<template>

    <header>
      <br>
      <h1 class="text-center">IT-Bangmod Kradan Kanban</h1>  
    </header>
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
          <div v-for="task in tasks">
            <tr>
              <td>{{task.id}}</td>
              <td>{{task.title}}</td>
              <td>{{task.assignees}}</td>
              <td>{{task.status}}</td>
            </tr>
        </div>
      </tbody>
  </table>
    <ul class="effectbg" style="z-index: -1; position: fixed">
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
    </ul>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono');
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
header {
  margin-top: 50px;
  margin-bottom:50px;
}
table {
  margin: 0 auto;
}
h1 {
  font-family: 'Proza Libre', sans-serif;
  font-size: 40px;
  color: #3A405A;
  font-weight: 800;
}
table th{ 
  font-family: 'Proza Libre', sans-serif;
  font-size: 20px;
  color: #3A405A;
  padding: 10px;
}
.pad{
  padding-left: 50px;
  padding-right: 50px;
  margin-left: 10px;
  text-decoration: none;
  border: solid 5px;
  border-color: white;
  border-radius: 25px;
  background-color: pink;
}

.effectbg {
  z-index: -1;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.effectbg li {
  position: absolute;
  display: block;
  list-style: none;
  width: 20px;
  height: 20px;
  background: rgba(127, 127, 127, 0.096);
  animation: animate 25s linear infinite;
  bottom: -150px;
}

.effectbg li:nth-child(1) {
  left: 25%;
  width: 80px;
  height: 80px;
  animation-delay: 0s;
}

.effectbg li:nth-child(2) {
  left: 10%;
  width: 20px;
  height: 20px;
  animation-delay: 2s;
  animation-duration: 12s;
}

.effectbg li:nth-child(3) {
  left: 70%;
  width: 20px;
  height: 20px;
  animation-delay: 4s;
}

.effectbg li:nth-child(4) {
  left: 40%;
  width: 60px;
  height: 60px;
  animation-delay: 0s;
  animation-duration: 18s;
}

.effectbg li:nth-child(5) {
  left: 65%;
  width: 20px;
  height: 20px;
  animation-delay: 0s;
}

.effectbg li:nth-child(6) {
  left: 75%;
  width: 110px;
  height: 110px;
  animation-delay: 3s;
}

.effectbg li:nth-child(7) {
  left: 35%;
  width: 150px;
  height: 150px;
  animation-delay: 7s;
}

.effectbg li:nth-child(8) {
  left: 50%;
  width: 25px;
  height: 25px;
  animation-delay: 15s;
  animation-duration: 45s;
}

.effectbg li:nth-child(9) {
  left: 20%;
  width: 15px;
  height: 15px;
  animation-delay: 2s;
  animation-duration: 35s;
}

.effectbg li:nth-child(10) {
  left: 85%;
  width: 150px;
  height: 150px;
  animation-delay: 0s;
  animation-duration: 11s;
}

@keyframes animate {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
    border-radius: 0;
  }

  100% {
    transform: translateY(-1000px) rotate(720deg);
    opacity: 0;
    border-radius: 50%;
  }
}
</style>
