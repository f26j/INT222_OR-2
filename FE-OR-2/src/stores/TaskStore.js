import { ref } from "vue";
import { defineStore } from "pinia";
import { useUserStore } from "@/stores/AuthUserStore.js";
import { useToastStore } from "@/stores/ToastStore.js";


export const useTaskStore = defineStore("taskStore", () => {
  const tasks = ref([]);
  const baseUrl = import.meta.env.VITE_API_BOARDS;
  const userStore = useUserStore(); // เรียก user store
  const selectedTask = ref(null);
  const toastStore = useToastStore();

  // const fetchTasks = async (boardId) => {
  //   try {
  //     const token = userStore.token; // ใช้ token จาก store

  //     const response = await fetch(`${baseUrl}/v3/boards/${boardId}/tasks`, {
  //       method: "GET",
  //       headers: {
  //         "Content-Type": "application/json",
  //         ...(token ? { Authorization: `Bearer ${token}` } : {}),
  //       },
  //     });

  //     if (!response.ok) {
  //       if (response.status === 403) {
  //         throw new Error("Access forbidden. Board might be private.");
  //       }
  //       throw new Error("Failed to fetch tasks");
  //     }

  //     tasks.value = await response.json();
  //   } catch (error) {
  //     console.error("Error fetching tasks:", error.message);
  //     throw error;
  //   }
  // };

  const fetchTasks = async (boardId) => {
    try {
      const headers = { "Content-Type": "application/json" };
  
      // ตรวจสอบว่ามี Token หรือไม่
      const token = userStore.token;
      if (token) {
        headers.Authorization = `Bearer ${token}`;
      } else {
        console.log("No token, fetching tasks as public access");
      }
  
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/tasks`, {
        method: "GET",
        headers,
      });
  
      if (!response.ok) {
        if (response.status === 403) {
          console.warn("Access forbidden. Board might be private.");
          throw new Error("Access forbidden.");
        }
        throw new Error("Failed to fetch tasks");
      }
  
      const data = await response.json();
      console.log("Fetched tasks:", data);
      tasks.value = data;
    } catch (error) {
      console.error("Error fetching tasks:", error.message);
      throw error;
    }
  };
  

  const fetchTaskDetail = async (boardId, taskId) => {
    try {
      const headers = { "Content-Type": "application/json" };
  
      // ถ้ามี Token ให้เพิ่มลงใน Header
      const token = userStore.token;
      if (token) {
        headers.Authorization = `Bearer ${token}`;
      } else {
        console.log("No token, fetching task as public access");
      }
  
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/tasks/${taskId}`, {
        method: "GET",
        headers,
      });
  
      if (!response.ok) {
        if (response.status === 401) {
          console.warn("Unauthorized access.");
          throw new Error("Unauthorized");
        }
        if (response.status === 404) {
          console.warn("Task not found.");
          throw new Error("Task not found");
        }
        throw new Error("Failed to fetch task detail");
      }
  
      const taskData = await response.json();
      return taskData;
    } catch (error) {
      console.error("Error fetching task detail:", error.message);
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
      await fetchTasks(boardId);
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

      await fetchTasks(boardId); // รีเฟรชข้อมูลทั้งหมด
    } catch (error) {
      console.error("Error updating task:", error.message);
    }
  };

  const selectDeleteTask = (taskId, title, index) => {
    selectedTask.value = { taskId, title, index };
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
      toastStore.addToast("Task deleted successfully", "success");
      tasks.value = tasks.value.filter((task) => task.id !== taskId);
    } catch (error) {
      toastStore.addToast("Failed to update task", "error");
      console.error("Error deleting task:", error.message);
    }
  };

  return {
    tasks,
    fetchTasks,
    fetchTaskDetail,
    addTask,
    updateTask,
    selectedTask,
    selectDeleteTask,
    deleteTask,
  };
});
