import { ref } from "vue";
import { defineStore } from "pinia";

export const useStatusStore = defineStore("apiStatus", () => {
  const statuses = ref([]);

  const updateStatus = (updatedStatus) => {
    const index = statuses.value.findIndex(status => status.id === updatedStatus.id);
    if (index !== -1) {
      statuses.value[index] = updatedStatus;
    } else {
      console.error("Status not found in store");
    }
  };

  return { statuses, updateStatus };
});
