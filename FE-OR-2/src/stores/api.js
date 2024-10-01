import { reactive, ref } from "vue";
import { defineStore } from "pinia";
import { useUserStore } from "@/stores/users.js";

// Task Store
export const useTaskStore = defineStore("apiTask", () => {
  const tasks = reactive([]);
  const status = ref([]);

  const updateTask = (updatedTask) => {
    const index = tasks.value.findIndex((task) => {
      return parseInt(task.id) === parseInt(updatedTask.id);
    });
    if (index !== -1) {
      tasks.value[index] = updatedTask;
    } else {
      console.error("Task not found in store");
    }
  };

  const addTask = (task) => {
    tasks.value.push(task);
  };

  return { tasks, updateTask, status, addTask };
});

// Status Store
export const useStatusStore = defineStore("apiStatus", () => {
  const statuses = ref([]);

  const updateStatus = (updatedStatus) => {
    const index = statuses.value.findIndex(
      (status) => parseInt(status.id) === parseInt(updatedStatus.id)
    );
    if (index !== -1) {
      statuses.value[index] = updatedStatus;
    } else {
      console.error("Status not found in store");
    }
  };

  return { statuses, updateStatus };
});

// Board Store
export const useBoardStore = defineStore("apiBoard", () => {
  const boards = ref([]);
  const baseUrl = import.meta.env.VITE_API_BOARDS;
  const userStore = useUserStore();

  const fetchBoards = async () => {
    try {
      const token = localStorage.getItem("jwtToken");
      const response = await fetch(`${baseUrl}/v3/boards`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch boards");
      }
      const data = await response.json();
      boards.value = data; // Save the fetched boards
    } catch (error) {
      console.error("Error fetching boards:", error.message);
    }
  };

  const createBoard = async (newBoard) => {
    try {
      if (!newBoard || !newBoard.name || !newBoard.owner) {
        throw new Error("Invalid board data");
      }
      boards.value.push(newBoard);
      console.log("Board created successfully:", newBoard);
    } catch (error) {
      console.error("Error creating board:", error.message);
    }
  };

  const updateBoard = (updatedBoard) => {
    const index = boards.value.findIndex(
      (board) => parseInt(board.id) === parseInt(updatedBoard.id)
    );
    if (index !== -1) {
      boards.value[index] = updatedBoard;
      console.log("Board updated successfully:", updatedBoard);
    } else {
      console.error("Board not found in store");
    }
  };

  const addBoard = (board) => {
    if (board && board.id) {
      boards.value.push(board);
      console.log("Board added successfully:", board);
    } else {
      console.error("Invalid board data");
    }
  };

  // ฟังก์ชันลบ Board พร้อมตรวจสอบ oid
  const deleteBoard = async (boardId) => {
    try {
      const token = localStorage.getItem("jwtToken");
      const board = boards.value.find((board) => board.id === boardId);

      // ตรวจสอบว่าเจอ board หรือไม่
      if (!board) {
        throw new Error("Board not found");
      }

      // ตรวจสอบว่า oid ของบอร์ดตรงกับ oid ของผู้ใช้หรือไม่
      if (board.owner.oid !== userStore.oid) {
        throw new Error(
          "Permission denied: You are not the owner of this board"
        );
      }

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to delete board");
      }

      // ลบ board จาก store
      boards.value = boards.value.filter((b) => b.id !== boardId);
      console.log("Board deleted successfully:", boardId);
    } catch (error) {
      console.error("Error deleting board:", error.message);
    }
  };

  return {
    boards,
    createBoard,
    updateBoard,
    addBoard,
    fetchBoards,
    deleteBoard,
  };
});
