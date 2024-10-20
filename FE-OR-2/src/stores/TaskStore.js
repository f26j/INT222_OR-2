// stores/TaskStore.js
import { ref } from "vue";
import { defineStore } from "pinia";

export const useTaskStore = defineStore("apiTask", () => {
  const tasks = ref([]);

  const baseUrl = import.meta.env.VITE_API_BOARDS;

  // ฟังก์ชัน Fetch Tasks (ไม่ต้องใช้ Token)
  const fetchTasks = async (boardId) => {
    try {
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/tasks`, {
        headers: { "Content-Type": "application/json" },
      });

      if (!response.ok) throw new Error("Failed to fetch tasks");

      tasks.value = await response.json();
      console.log("Tasks fetched successfully:", tasks.value);
    } catch (error) {
      console.error("Error fetching tasks:", error);
    }
  };

  // ฟังก์ชันเพิ่ม Task (ต้องใช้ Token)
  const addTask = async (boardId, newTask) => {
    try {
      const token = localStorage.getItem("jwtToken");
      if (!token) throw new Error("No token available");

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
      console.error("Error adding task:", error);
    }
  };

  // ฟังก์ชันอัปเดต Task (ต้องใช้ Token)
  const updateTask = async (boardId, taskId, updatedTask) => {
    try {
      const token = localStorage.getItem("jwtToken");
      if (!token) throw new Error("No token available");

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/tasks/${taskId}`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(updatedTask),
      });

      if (!response.ok) throw new Error("Failed to update task");

      const index = tasks.value.findIndex((task) => task.id === taskId);
      if (index !== -1) tasks.value[index] = updatedTask;
      console.log("Task updated successfully:", updatedTask);
    } catch (error) {
      console.error("Error updating task:", error);
    }
  };

  // ฟังก์ชันลบ Task (ต้องใช้ Token)
  const deleteTask = async (boardId, taskId) => {
    try {
      const token = localStorage.getItem("jwtToken");
      if (!token) throw new Error("No token available");

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/tasks/${taskId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error("Failed to delete task");

      tasks.value = tasks.value.filter((task) => task.id !== taskId);
      console.log("Task deleted successfully:", taskId);
    } catch (error) {
      console.error("Error deleting task:", error);
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
