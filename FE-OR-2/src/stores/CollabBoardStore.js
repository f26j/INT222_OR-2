import { ref } from "vue";
import { defineStore } from "pinia";
import { useUserStore } from "@/stores/AuthUserStore.js";
import { useRouter } from "vue-router";

export const useCollabBoardStore = defineStore("collabBoardStore", () => {
  const collabBoards = ref([]);
  const currentCollabBoard = ref(null); // Board ที่กำลังดู
  const baseUrl = import.meta.env.VITE_API_BOARDS; // Base URL สำหรับ API
  const userStore = useUserStore();
  const router = useRouter();

  // ตรวจสอบ Token และ Refresh ถ้าจำเป็น
  const checkToken = () => {
    if (!userStore.token) {
      router.push({ name: "login" });
      throw new Error("Authentication required");
    }
    return userStore.token;
  };

  // ดึงข้อมูล Collaboration Boards
  const fetchCollabBoards = async () => {
    try {
      const token = checkToken();

      const response = await fetch(`${baseUrl}/v3/collab-boards`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        if (response.status === 401) {
          userStore.logout();
          router.push({ name: "login" });
        }
        throw new Error("Failed to fetch collaboration boards");
      }

      collabBoards.value = await response.json();
    } catch (error) {
      console.error("Error fetching collaboration boards:", error.message);
    }
  };

  // เพิ่มผู้ใช้ใหม่ใน Collaboration Board
  const addCollaborator = async (boardId, userId, permission) => {
    try {
      const token = checkToken();

      const response = await fetch(`${baseUrl}/v3/collab-boards/${boardId}/collaborators`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ userId, permission }), // เช่น { userId: "123", permission: "write" }
      });

      if (!response.ok) {
        throw new Error("Failed to add collaborator");
      }

      const updatedBoard = await response.json();
      currentCollabBoard.value = updatedBoard; // อัปเดต Board ที่ดูอยู่
    } catch (error) {
      console.error("Error adding collaborator:", error.message);
      throw error;
    }
  };

  // ลบผู้ใช้จาก Collaboration Board
  const removeCollaborator = async (boardId, userId) => {
    try {
      const token = checkToken();

      const response = await fetch(`${baseUrl}/v3/collab-boards/${boardId}/collaborators/${userId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to remove collaborator");
      }

      const updatedBoard = await response.json();
      currentCollabBoard.value = updatedBoard; // อัปเดต Board ที่ดูอยู่
    } catch (error) {
      console.error("Error removing collaborator:", error.message);
      throw error;
    }
  };

  // อัปเดตสิทธิ์ของผู้ใช้ใน Collaboration Board
  const updateCollaboratorPermission = async (boardId, userId, newPermission) => {
    try {
      const token = checkToken();

      const response = await fetch(`${baseUrl}/v3/collab-boards/${boardId}/collaborators/${userId}`, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ permission: newPermission }), // เช่น { permission: "read" }
      });

      if (!response.ok) {
        throw new Error("Failed to update collaborator permission");
      }

      const updatedBoard = await response.json();
      currentCollabBoard.value = updatedBoard; // อัปเดต Board ที่ดูอยู่
    } catch (error) {
      console.error("Error updating collaborator permission:", error.message);
      throw error;
    }
  };

  // ดึงข้อมูลผู้ใช้ใน Collaboration Board
  const fetchCollaborators = async (boardId) => {
    try {
      const token = checkToken();

      const response = await fetch(`${baseUrl}/v3/collab-boards/${boardId}/collaborators`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch collaborators");
      }

      return await response.json();
    } catch (error) {
      console.error("Error fetching collaborators:", error.message);
      throw error;
    }
  };

  return {
    collabBoards,
    currentCollabBoard,
    fetchCollabBoards,
    addCollaborator,
    removeCollaborator,
    updateCollaboratorPermission,
    fetchCollaborators,
  };
});
