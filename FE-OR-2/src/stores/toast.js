import { defineStore } from "pinia";
import { ref } from "vue";

export const useToastStore = defineStore("toast", () => {
  const showToastAdd = ref(false);
  const showToastEdit = ref(false);
  const showToastAddStatus = ref(false);

  const handleTaskAdded = () => {
    showToastAdd.value = true;
    console.log(showToastAdd.value);
    setTimeout(() => {
      showToastAdd.value = false;
    }, 5000);
  };

  const handleTaskEdited = () => {
    showToastEdit.value = true;
    console.log(showToastEdit.value);
    setTimeout(() => {
      showToastEdit.value = false;
    }, 5000);
  };

  const handleStatusAdded = () => {
    showToastAddStatus.value = true;
    console.log(showToastAddStatus.value);
    setTimeout(() => {
        showToastAddStatus.value = false;
    }, 5000);
  };

  const closeToast = () => {
    showToastAdd.value = false;
    showToastEdit.value = false;
    showToastAddStatus.value = false;
  };

  return {
    showToastAdd,
    showToastEdit,
    showToastAddStatus,
    handleTaskAdded,
    handleTaskEdited,
    handleStatusAdded,
    closeToast,
  };
});
