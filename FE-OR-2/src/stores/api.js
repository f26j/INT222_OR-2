import { ref, toRaw } from "vue";
import { defineStore } from "pinia";

// Task Store
export const useTaskStore = defineStore("apiTask", () => {
  const tasks = ref([]);
  const status = ref([]);

  const updateTask = (updatedTask) => {
    console.log(updatedTask);
    console.log(toRaw(tasks.value));
    const index = toRaw(tasks.value).findIndex(
      (task) => parseInt(task.id) === parseInt(updatedTask.id)
    );
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
    console.log(updatedStatus);
    console.log(toRaw(statuses.value));
    const index = toRaw(statuses.value).findIndex(
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

  // ฟังก์ชันสร้างบอร์ดใหม่
  const createBoard = async (newBoard) => {
    try {
      // ตรวจสอบว่ามีข้อมูลบอร์ดที่จำเป็นหรือไม่
      if (!newBoard || !newBoard.name || !newBoard.owner) {
        throw new Error("Invalid board data");
      }

      // สมมติว่า newBoard คือข้อมูลที่ได้รับจาก API
      boards.value.push(newBoard);
      console.log("Board created successfully:", newBoard);
    } catch (error) {
      console.error("Error creating board:", error.message);
    }
  };

  // ฟังก์ชันอัปเดตบอร์ด
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

  // ฟังก์ชันเพิ่มบอร์ดเข้าใน store
  const addBoard = (board) => {
    if (board && board.id) {
      boards.value.push(board);
      console.log("Board added successfully:", board);
    } else {
      console.error("Invalid board data");
    }
  };

  return { boards, createBoard, updateBoard, addBoard };
});
