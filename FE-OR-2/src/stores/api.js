import { ref } from "vue";
import { defineStore } from "pinia";

// Task Store
export const useTaskStore = defineStore("apiTask", () => {
  const tasks = ref([]);
  const status = ref([]);

  const updateTask = (updatedTask) => {
    const index = tasks.value.findIndex(
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
// Board Store
export const useBoardStore = defineStore("apiBoard", () => {
  const boards = ref([]);

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

  return { boards, createBoard, updateBoard, addBoard };
});
