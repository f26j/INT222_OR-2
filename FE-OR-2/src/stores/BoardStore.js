import { ref } from "vue";
import { defineStore } from "pinia";
import { useUserStore } from "@/stores/AuthUserStore.js";

export const useBoardStore = defineStore("apiBoard", () => {
  const boards = ref([]);
  const currentBoard = ref(null); // เก็บบอร์ดที่โหลด
  const baseUrl = import.meta.env.VITE_API_BOARDS;
  const userStore = useUserStore();

  // ฟังก์ชันตรวจสอบและรีเฟรช token ถ้าหมดอายุ
  const checkToken = () => {
    if (!userStore.token) {
      throw new Error("Authentication required");
    }
    return userStore.token;
  };

  const fetchBoards = async () => {
    try {
      const token = checkToken();

      const response = await fetch(`${baseUrl}/v3/boards`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error(`Failed to fetch boards. Status: ${response.status}`);

      boards.value = await response.json();
    } catch (error) {
      console.error("Error fetching boards:", error.message);
    }
  };

  const getBoard = async (boardId) => {
    try {
      const headers = { "Content-Type": "application/json" };
  
      // ตรวจสอบว่ามี Token หรือไม่
      const token = userStore.token;
      if (token) {
        headers.Authorization = `Bearer ${token}`;
      } else {
        console.log("No token, fetching as public access");
      }
  
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        method: "GET",
        headers,
      });
  
      if (!response.ok) {
        if (response.status === 404) {
          console.error("Board not found.");
          return null; // ถ้าไม่พบ ให้ return null แทน
        }
        throw new Error(`Failed to fetch board. Status: ${response.status}`);
      }
  
      const boardData = await response.json();
      currentBoard.value = boardData; // เก็บข้อมูลบอร์ดใน state
  
      return boardData;
    } catch (error) {
      console.error("Error fetching board:", error.message);
      throw error;
    }
  };
  
  

  const createBoard = async (newBoard) => {
    try {
      const token = checkToken();

      const response = await fetch(`${baseUrl}/v3/boards`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newBoard),
      });

      if (!response.ok) throw new Error(`Failed to create board. Status: ${response.status}`);

      const createdBoard = await response.json();
      boards.value.push(createdBoard);
      return createdBoard;
    } catch (error) {
      console.error("Error creating board:", error.message);
      throw error;
    }
  };

  const updateBoard = async (boardId, updatedBoard) => {
    const index = boards.value.findIndex((board) => board.id === boardId);
    if (index !== -1) {
      boards.value[index] = updatedBoard;
    } else {
      console.error("Board not found in store");
    }
  };

  const updateVisibility = async (boardId, newVisibility) => {
    try {
      const token = checkToken();

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ visibility: newVisibility }),
      });

      if (!response.ok) throw new Error(`Failed to update visibility. Status: ${response.status}`);

      const board = boards.value.find((board) => board.id === boardId);
      if (board) board.visibility = newVisibility;

    } catch (error) {
      console.error("Error updating visibility:", error.message);
    }
  };

  const deleteBoard = async (boardId) => {
    try {
      const token = checkToken();

      const board = boards.value.find((board) => board.id === boardId);
      if (!board) throw new Error("Board not found");
      if (board.owner.oid !== userStore.oid) throw new Error("Permission denied");

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error(`Failed to delete board. Status: ${response.status}`);

      boards.value = boards.value.filter((b) => b.id !== boardId);
    } catch (error) {
      console.error("Error deleting board:", error.message);
    }
  };

  return {
    boards,
    currentBoard,
    fetchBoards,
    getBoard,
    createBoard,
    updateBoard,
    updateVisibility,
    deleteBoard,
  };
});
