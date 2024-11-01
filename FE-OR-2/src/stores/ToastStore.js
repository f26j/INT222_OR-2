import { defineStore } from "pinia";
import { ref } from "vue";

export const useToastStore = defineStore("toast", () => {
  const toasts = ref([]);
  let nextId = 1;

  const addToast = (message, type = "success", duration = 5000) => {
    const id = nextId++;
    toasts.value.push({ id, message, type, duration });

    setTimeout(() => {
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
