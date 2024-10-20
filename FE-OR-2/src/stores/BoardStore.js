import { ref } from "vue";
import { defineStore } from "pinia";
import { useUserStore } from "@/stores/AuthUserStore.js";

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

      if (!response.ok) throw new Error("Failed to fetch boards");

      boards.value = await response.json();
    } catch (error) {
      console.error("Error fetching boards:", error.message);
    }
  };

  const createBoard = async (newBoard) => {
    try {
      const token = localStorage.getItem("jwtToken");
      const response = await fetch(`${baseUrl}/v3/boards`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newBoard),
      });
  
      if (!response.ok) throw new Error("Failed to create board");
  
      const createdBoard = await response.json();
  
      if (createdBoard.id) {
        boards.value.push(createdBoard); // เพิ่ม Board ใหม่พร้อม ID
        console.log("Board created successfully:", createdBoard);
        return createdBoard; // ส่งข้อมูลกลับไปใช้
      } else {
        throw new Error("Board ID is missing.");
      }
    } catch (error) {
      console.error("Error creating board:", error.message);
      throw error; // ให้ส่ง error กลับไปแสดงใน component
    }
  };
  

  const updateBoard = (updatedBoard) => {
    const index = boards.value.findIndex(board => board.id === updatedBoard.id);
    if (index !== -1) {
      boards.value[index] = updatedBoard;
      console.log("Board updated successfully:", updatedBoard);
    } else {
      console.error("Board not found in store");
    }
  };

  const updateVisibility = async (boardId, newVisibility) => {
    try {
      const token = localStorage.getItem("jwtToken");
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ visibility: newVisibility }),
      });

      if (!response.ok) throw new Error("Failed to update visibility");

      const board = boards.value.find(board => board.id === boardId);
      if (board) board.visibility = newVisibility;

      console.log("Visibility updated successfully:", newVisibility);
    } catch (error) {
      console.error("Error updating visibility:", error.message);
    }
  };

  const deleteBoard = async (boardId) => {
    try {
      const token = localStorage.getItem("jwtToken");
      const board = boards.value.find(board => board.id === boardId);

      if (!board) throw new Error("Board not found");
      if (board.owner.oid !== userStore.oid) throw new Error("Permission denied");

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error("Failed to delete board");

      boards.value = boards.value.filter(b => b.id !== boardId);
      console.log("Board deleted successfully:", boardId);
    } catch (error) {
      console.error("Error deleting board:", error.message);
    }
  };

  return {
    boards,
    fetchBoards,
    createBoard,
    updateBoard,
    updateVisibility,
    deleteBoard,
  };
});
