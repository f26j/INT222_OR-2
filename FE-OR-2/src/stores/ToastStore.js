import { defineStore } from "pinia";
import { ref } from "vue";

export const useToastStore = defineStore("toast", () => {
  const toasts = ref([]);
  let nextId = 1;

  const addToast = (message, type = "success", duration = 5000) => {
    const id = nextId++;
    console.log("Adding Toast:", { id, message, type });
    toasts.value.push({ id, message, type });
  

    setTimeout(() => {
      console.log("Removing Toast:", id);
      removeToast(id);
    }, duration);
  };
  

  const removeToast = (id) => {
    toasts.value = toasts.value.filter((toast) => toast.id !== id);
  };

  return {
    toasts,
    addToast,
    removeToast,
  };
});
