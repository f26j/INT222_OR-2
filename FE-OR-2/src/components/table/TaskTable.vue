<script setup>
import { onMounted, ref, watch, computed } from "vue"
import { useRouter, useRoute } from "vue-router"
import { storeToRefs } from "pinia"
import { useBoardStore, useTaskStore } from "@/stores/apiController.js"
import { useToastStore } from "@/stores/ToastStore.js"
import { useUserStore } from "@/stores/AuthUserStore.js"
import Badges from "../components/css/Badge.vue"
import Copyright from "../components/css/Copyright.vue"
import ModalDelete from "../components/ModalDeleteTask.vue"
import Navbar from "../components/css/Navbar.vue"

const route = useRoute();
const boardId = route.params.boardId;

const taskStore = useTaskStore();
const tasks = taskStore.tasks; 
const selectedTask = ref(null);

onMounted(async () => {
  await taskStore.fetchTasks(boardId);
});

const openAddModal = () => {
  selectedTask.value = null; // Reset selected task
  router.push({ name: 'task-add', params: { boardId } });
};

const openEditModal = (task) => {
  selectedTask.value = task;
  router.push({ name: 'task-edit', params: { boardId, taskId: task.id } });
};

const deleteTask = async (taskId) => {
  await taskStore.deleteTask(boardId, taskId);
};



</script>
 
<template>
<div>
    <Navbar class="itbkk-fullname" />

    <Copyright />
    <ModalDelete />
</div>
</template>
 
<style scoped>
 
</style>