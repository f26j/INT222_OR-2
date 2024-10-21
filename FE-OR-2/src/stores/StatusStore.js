// stores/StatusStore.js
import { defineStore } from "pinia";
import { ref } from "vue";

const baseUrl = import.meta.env.VITE_API_BOARDS;

export const useStatusStore = defineStore("statusStore", () => {
  const statuses = ref([]);

  // ฟังก์ชันดึง JWT Token
  function getToken() {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
      console.error("No token available");
      return null;
    }
    return token;
  }

  const getStatus = (statusId) => {
    const status = statuses.value.find((s) => s.id === statusId);
    if (!status) {
      console.error(`Status with ID ${statusId} not found`);
      return null;
    }
    return status;
  };

  // ฟังก์ชัน fetch พร้อม Token
  async function fetchWithToken(url, options = {}) {
    const token = getToken();
    if (!token) return null;

    const response = await fetch(url, {
      ...options,
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
        ...options.headers,
      },
    });

    if (response.status === 401) {
      console.error("Unauthorized access - Redirecting to login");
      // router.push("/login");
      return null;
    }

    if (response.status === 403) {
      console.error("Access Denied");
      return null;
    }

    return response;
  }

  // Fetch all statuses for a board
  const fetchStatuses = async (boardId) => {
    try {
      const response = await fetchWithToken(`${baseUrl}/v3/boards/${boardId}/statuses`);
      if (!response) throw new Error("Failed to fetch statuses");
  
      const data = await response.json();
      statuses.value = data;
    } catch (error) {
      console.error("Error fetching statuses:", error.message);
      throw error;
    }
  };  

  // Add a new status
  const addStatus = async (boardId, newStatus) => {
    try {
      const response = await fetchWithToken(`${baseUrl}/v3/boards/${boardId}/statuses`, {
        method: "POST",
        body: JSON.stringify(newStatus),
      });

      if (!response) throw new Error("Failed to add status");

      const createdStatus = await response.json();
      statuses.value.push(createdStatus);
    } catch (error) {
      console.error("Error adding status:", error.message);
      throw error;
    }
  };

  // Update an existing status
  const updateStatus = async (boardId, statusId, updatedStatus) => {
    try {
      const response = await fetchWithToken(
        `${baseUrl}/v3/boards/${boardId}/statuses/${statusId}`,
        {
          method: "PUT",
          body: JSON.stringify(updatedStatus),
        }
      );

      if (!response) throw new Error("Failed to update status");

      const index = statuses.value.findIndex((s) => s.id === statusId);
      if (index !== -1) statuses.value[index] = updatedStatus;
    } catch (error) {
      console.error("Error updating status:", error.message);
      throw error;
    }
  };

   // อัปเดตสถานะใน Store โดยไม่ต้องรีเฟรชทั้งหมด
  const updateStatusInStore = (updatedStatus) => {
    const index = statuses.value.findIndex((s) => s.id === updatedStatus.id);
    if (index !== -1) {
      statuses.value[index] = { ...statuses.value[index], ...updatedStatus };
    }
  };

  // Delete a status
  const deleteStatus = async (boardId, statusId) => {
    try {
      const response = await fetchWithToken(
        `${baseUrl}/v3/boards/${boardId}/statuses/${statusId}`,
        { method: "DELETE" }
      );
  
      if (!response) throw new Error("Failed to delete status");
      statuses.value = statuses.value.filter((s) => s.id !== statusId);
    } catch (error) {
      console.error("Error deleting status:", error.message);
      throw error;
    }
  };

  // Transfer tasks to another status and delete the original status
  const transferStatus = async (boardId, statusId, newStatusId) => {
    try {
      const response = await fetchWithToken(
        `${baseUrl}/v3/boards/${boardId}/statuses/${statusId}/${newStatusId}`,
        { method: "DELETE" }
      );

      if (!response) throw new Error("Failed to transfer and delete status");

      statuses.value = statuses.value.filter((s) => s.id !== statusId);
    } catch (error) {
      console.error("Error transferring status:", error.message);
      throw error;
    }
  };

  return {
    statuses,
    fetchStatuses,
    addStatus,
    updateStatus,
    updateStatusInStore,
    deleteStatus,
    transferStatus,
    getStatus,
  };
});
