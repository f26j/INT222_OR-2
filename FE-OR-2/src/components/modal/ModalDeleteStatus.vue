<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { useStatusStore } from "@/stores/StatusStore.js";
import { useTaskStore } from "@/stores/TaskStore.js";
import { useUserStore } from "@/stores/AuthUserStore.js";

// Stores and route setup
const statusStore = useStatusStore();
const taskStore = useTaskStore();
const route = useRoute();
const userStore = useUserStore();

// Props from parent component
const props = defineProps({
  statusId: Number,
  name: String,
});

// Emits for communication with parent
defineEmits(["closeModal", "confirmDeleteStatus", "deleteTransfer"]);

// State variables
const currentStatus = ref({});
const newStatusId = ref(null);
const isDeletable = ref(true);
const tasksInStatus = ref([]);
const boardId = route.params.boardId;

// Helper function to safely unwrap a ref or reactive object
const unwrap = (reactiveObject) => {
  return Array.isArray(reactiveObject) ? reactiveObject : reactiveObject?.value || [];
};

// Function to fetch and check tasks with status
const getStatusWithTasks = async () => {
  try {
    const status = statusStore.getStatus(props.statusId);
    if (!status) throw new Error("Status not found");
    currentStatus.value = status;

    await taskStore.fetchTasks(boardId); // Fetch tasks for the board

    const allTasks = unwrap(taskStore.tasks); // Safely unwrap tasks

    // Filter tasks matching the status ID
    tasksInStatus.value = allTasks.filter(
      (task) => task?.status?.id === props.statusId
    );


    isDeletable.value = tasksInStatus.value.length === 0;

    // Prepare for transfer if tasks exist
    if (!isDeletable.value) {
      const availableStatuses = unwrap(statusStore.statuses).filter(
        (s) => s?.id !== props.statusId // ไม่แสดงสถานะที่จะลบ
      );
      newStatusId.value = availableStatuses.length
        ? availableStatuses[0]?.id
        : null;
    }
  } catch (error) {
    console.error("Error fetching status or tasks:", error.message);
  }
};

onMounted(async () => {
  if (!userStore.token) {
    return router.push({ name: "AccessDenied" });
  }
  await getStatusWithTasks();
});
// Watcher for tasks to log updates
watch(
  () => taskStore.tasks,
  (newTasks) => {
  },
  { deep: true }
);
</script>


<template>
  <div class="details fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
    <div
      v-if="isDeletable"
      class="modalDelete itbkk-modal rounded-lg text-center shadow-xl p-6"
      role="dialog"
      aria-modal="true"
    >
      <h2 class="text-2xl font-bold mb-4">Delete a Status</h2>
      <p class="itbkk-message">Do you want to delete the <strong>{{ name }}</strong> status?</p>
      <div class="Button flex justify-center gap-4 mt-5">
        <button class="itbkk-button-cancel" @click="$emit('closeModal')">
          Cancel
        </button>
        <button
          class="itbkk-button-confirm"
          @click="$emit('confirmDeleteStatus', props.statusId)"
        >
          Confirm
        </button>
      </div>
    </div>

    <div
      v-else
      class="modalDelete itbkk-modal rounded-lg text-center shadow-xl p-6"
      role="dialog"
      aria-modal="true"
    >
      <h2 class="text-2xl font-bold mb-4">Transfer Tasks</h2>
      <p>
        There are tasks in <strong>{{ name }}</strong> status. Please transfer
        them to another status before deleting.
      </p>
      <div class="flex items-center gap-4 mt-4">
        <label for="newStatus">Transfer tasks to:</label>
        <select v-model="newStatusId" class="itbkk-status-select">
          <option
            v-for="status in unwrap(statusStore.statuses).filter(
              (s) => s?.id !== props.statusId
            )"
            :key="status?.id"
            :value="status?.id"
          >
            {{ status?.name }}
          </option>
        </select>
      </div>
      <div class="Button flex justify-center gap-4 mt-5">
        <button class="itbkk-button-cancel" @click="$emit('closeModal')">
          Cancel
        </button>
        <button
          class="itbkk-button-transfer"
          :disabled="!newStatusId"
          @click="$emit('deleteTransfer', props.statusId, newStatusId)"
        >
          Transfer and Delete
        </button>
      </div>
    </div>
  </div>
</template>


<style scoped>
.modalDelete {
  background-color: #ffffff;
  border-radius: 20px;
  padding: 20px;
  max-width: 400px;
  width: 100%;
}

.itbkk-status-select {
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.Button button {
  width: 120px;
  height: 45px;
  border-radius: 15px;
  transition: transform 0.3s;
}

button:hover {
  transform: scale(1.08);
}

.itbkk-button-cancel {
  background-color: #e0e0e0;
  color: #808080;
  border: none;
}

.itbkk-button-confirm,
.itbkk-button-transfer {
  background-color: #e05555;
  color: white;
  border: none;
}
</style>
