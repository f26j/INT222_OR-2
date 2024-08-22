import { ref, toRaw } from "vue";
import { defineStore } from "pinia";

export const useStatusStore = defineStore("apiStatus", () => {
  const token = Text();

  const updateStatus = (updatedStatus) => {
    console.log(updatedStatus);
    console.log(toRaw(statuses.value.value));
    for (const status of statuses.value) {
      console.log(statuses);
    }
    const index = toRaw(statuses.value.value).findIndex(
      (status) => parseInt(status.id) === parseInt(updatedStatus.id)
    );
    if (index !== -1) {
      statuses.value.value[index] = updatedStatus;
    } else {
      console.error("Status not found in store");
    }
  };

  return { token, updateStatus };
});
