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

  // ดึงข้อมูล Collaborators ทั้งหมดของ Board
  const fetchCollaborators = async (boardId) => {
    try {
      const token = checkToken();
  
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/collabs`, {
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
        throw new Error("Failed to fetch collaborators");
      }
  
      const data = await response.json();
      collabBoards.value = data; // อัปเดต collabBoards
      return data;
    } catch (error) {
      console.error("Error fetching collaborators:", error.message);
      throw error;
    }
  };
  
  // เพิ่ม Collaborator
  const addCollaborator = async (boardId, email, accessRight) => {
    try {
      const token = checkToken();
  
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/collabs`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, accessRight }),
      });
  
      if (!response.ok) {
        // throw new Error("Failed to add collaborator");
        const error = new Error("HTTP error");
        error.response = { status: response.status }; // Add status to the error object
        throw error;
      }
  
      await fetchCollaborators(boardId); // อัปเดต collabBoards หลังจากเพิ่มสำเร็จ
    } catch (error) {
      console.error("Error adding collaborator:", error.message);
      throw error;
    }
  };
  

  // ลบ Collaborator
  const removeCollaborator = async (boardId, collabOid) => {
    try {
      const token = checkToken();
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/collabs/${collabOid}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
  
      if (!response.ok) {
        throw new Error("Failed to remove collaborator");
      }
  
      await fetchCollaborators(boardId); // อัปเดต collabBoards หลังจากลบสำเร็จ
    } catch (error) {
      console.error("Error removing collaborator:", error.message);
      throw error;
    }
  };
  

  // อัปเดตสิทธิ์ Collaborator
  const updateCollaboratorPermission = async (boardId, collabOid, accessRight) => {
    try {
      const token = checkToken();
  
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}/collabs/${collabOid}`, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ accessRight: accessRight }),
      });
  
      if (!response.ok) {
        throw new Error("Failed to update collaborator access right");
      }
  
      await fetchCollaborators(boardId); // อัปเดต collabBoards หลังจากเปลี่ยนสิทธิ์สำเร็จ
    } catch (error) {
      console.error("Error updating collaborator access right:", error.message);
      throw error;
    }
  };
  

  return {
    collabBoards,
    currentCollabBoard,
    fetchCollaborators,
    addCollaborator,
    removeCollaborator,
    updateCollaboratorPermission,
  };
});
