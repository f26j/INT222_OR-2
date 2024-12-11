import { ref } from "vue";
import { defineStore } from "pinia";
import { useUserStore } from "@/stores/AuthUserStore.js";
import { useRouter } from "vue-router";

export const useBoardStore = defineStore("apiBoard", () => {
  const personalBoards = ref([]); // สำหรับ Personal Boards
  const collaborativeBoards = ref([]); // สำหรับ Collaborative Boards
  const currentBoard = ref(null); // บอร์ดที่กำลังดู
  const baseUrl = import.meta.env.VITE_API_BOARDS; // URL ของ API
  const userStore = useUserStore();
  const router = useRouter();

  // ตรวจสอบ Token
  const checkToken = () => {
    if (!userStore.token) {
      router.push({ name: "login" });
      throw new Error("Authentication required");
    }
    return userStore.token;
  };

  // Fetch Boards
  const fetchBoards = async () => {
    try {
      const headers = { "Content-Type": "application/json" };
  
      // เพิ่มเงื่อนไขตรวจสอบ Token
      const token = userStore.token;
      if (token) {
        headers.Authorization = `Bearer ${token}`;
      } else {
        console.log("No token, fetching boards as public access");
      }
  
      const response = await fetch(`${baseUrl}/v3/boards`, {
        method: "GET",
        headers,
      });
  
      if (!response.ok) {
        if (response.status === 401) {
          console.warn("Unauthorized (401). Redirecting to login.");
          userStore.logout();
          router.push({ name: "login" });
          return;
        }
        throw new Error(`Failed to fetch boards. Status: ${response.status}`);
      }
  
      const data = await response.json();
  
      // แยกข้อมูลที่ได้มา
      personalBoards.value = data.personal_boards || [];
      collaborativeBoards.value = data.collaborative_boards || [];
    } catch (error) {
      console.error("Error fetching boards:", error.message);
    }
  };
  
    

  // Get a single Board
  const getBoard = async (boardId) => {
    try {
      const headers = { "Content-Type": "application/json" };
      const token = userStore.token;

      if (token) {
        headers.Authorization = `Bearer ${token}`;
      }

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        method: "GET",
        headers,
      });

      if (!response.ok) {
        if (response.status === 404) {
          console.error("Board not found.");
          return null; // ถ้าไม่พบ ให้ return null แทน
        } else if (response.status === 401) {
          console.warn("Unauthorized (401). Redirecting to login.");
          userStore.logout();
          router.push({ name: "login" });
          return;
        }
        throw new Error(`Failed to fetch board. Status: ${response.status}`);
      }

      const boardData = await response.json();
      currentBoard.value = boardData;

      return boardData;
    } catch (error) {
      console.error("Error fetching board:", error.message);
      throw error;
    }
  };

  // Create a new Board
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

      if (!response.ok) {
        if (response.status === 401) {
          console.warn("Unauthorized (401). Redirecting to login.");
          userStore.logout();
          router.push({ name: "login" });
          return;
        }
        throw new Error(`Failed to create board. Status: ${response.status}`);
      }

      const createdBoard = await response.json();
      personalBoards.value.push(createdBoard); // เพิ่มใน Personal Boards
      return createdBoard;
    } catch (error) {
      console.error("Error creating board:", error.message);
      throw error;
    }
  };

  // Update Board
  const updateBoard = async (boardId, updatedBoard) => {
    const index = personalBoards.value.findIndex((board) => board.id === boardId);
    if (index !== -1) {
      personalBoards.value[index] = updatedBoard;
    } else {
      console.error("Board not found in store");
    }
  };

  // Update Board Visibility
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

      if (!response.ok) {
        throw new Error(`Failed to update visibility. Status: ${response.status}`);
      }

      const board = personalBoards.value.find((board) => board.id === boardId);
      if (board) board.visibility = newVisibility;
    } catch (error) {
      console.error("Error updating visibility:", error.message);
    }
  };

  // Delete a Board
  const deleteBoard = async (boardId) => {
    try {
      const token = checkToken();

      const board = personalBoards.value.find((board) => board.id === boardId);
      if (!board) throw new Error("Board not found");
      if (board.owner.oid !== userStore.oid) throw new Error("Permission denied");

      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error(`Failed to delete board. Status: ${response.status}`);
      }

      personalBoards.value = personalBoards.value.filter((b) => b.id !== boardId);
    } catch (error) {
      console.error("Error deleting board:", error.message);
    }
  };

  return {
    personalBoards,
    collaborativeBoards,
    currentBoard,
    fetchBoards,
    getBoard,
    createBoard,
    updateBoard,
    updateVisibility,
    deleteBoard,
  };
});
