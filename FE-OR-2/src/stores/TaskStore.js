import { ref } from "vue";
import { defineStore } from "pinia";
import { useUserStore } from "@/stores/AuthUserStore.js";

export const useTaskStore = defineStore("taskStore", () => {
  const tasks = ref([]);
  const baseUrl = import.meta.env.VITE_API_BOARDS;
  const userStore = useUserStore(); // เรียก user store

  const fetchTasks = async (boardId) => {
    try {
      const token = userStore.token; // ใช้ token จาก store

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/tasks`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
      });

      if (!response.ok) {
        if (response.status === 403) {
          throw new Error("Access forbidden. Board might be private.");
        }
        throw new Error("Failed to fetch tasks");
      }

      tasks.value = await response.json();
    } catch (error) {
      console.error("Error fetching tasks:", error.message);
      throw error;
    }
  };

  const addTask = async (boardId, newTask) => {
    try {
      const token = userStore.token;

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/tasks`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newTask),
      });

      if (!response.ok) throw new Error("Failed to add task");

      const task = await response.json();
      tasks.value.push(task);
      console.log("Task added successfully:", task);
    } catch (error) {
      console.error("Error adding task:", error.message);
    }
  };

  const updateTask = async (boardId, taskId, updatedTask) => {
    try {
      const token = userStore.token;

      const response = await fetch(
        `${baseUrl}/v3/boards/${boardId}/tasks/${taskId}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(updatedTask),
        }
      );

      if (!response.ok) throw new Error("Failed to update task");

      const index = tasks.value.findIndex((task) => task.id === taskId);
      if (index !== -1) tasks.value[index] = updatedTask;
      console.log("Task updated successfully:", updatedTask);
    } catch (error) {
      console.error("Error updating task:", error.message);
    }
  };

  const deleteTask = async (boardId, taskId) => {
    try {
      const token = userStore.token;

      const response = await fetch(
        `${baseUrl}/v3/boards/${boardId}/tasks/${taskId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) throw new Error("Failed to delete task");

      tasks.value = tasks.value.filter((task) => task.id !== taskId);
      console.log("Task deleted successfully:", taskId);
    } catch (error) {
      console.error("Error deleting task:", error.message);
    }
  };

  return {
    tasks,
    fetchTasks,
    addTask,
    updateTask,
    deleteTask,
  };
});
