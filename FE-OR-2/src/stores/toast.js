import { defineStore } from "pinia";
import { ref } from "vue";

export const useToastStore = defineStore("toast", () => {
  const showToastAdd = ref(false);
  const showToastEdit = ref(false);
  const showToastAddStatus = ref(false);
  const showToastEditStatus = ref(false);
  const showToastDeleteStatus = ref(false);
  const addSuccess = ref(false);
  const editSuccess = ref(false);
  const deleteSuccess = ref(false);
  const showToastLogin = ref(false);

  // Timeout references
  let toastAddTimeout = null;
  let toastEditTimeout = null;
  
  let toastAddStatusTimeout = null;
  let toastEditStatusTimeout = null;
  let toastDeleteStatusTimeout = null;
  let toastLoginTimeout = null;

  const resetTimeout = (timeoutRef, callback, duration = 5000) => {
    if (timeoutRef) clearTimeout(timeoutRef); // Clear existing timeout
    return setTimeout(callback, duration); // Set new timeout
  };

  const handleTaskAdded = () => {
    showToastAdd.value = true;
    toastAddTimeout = resetTimeout(toastAddTimeout, () => {
      showToastAdd.value = false;
    });
  };

  const handleTaskEdited = () => {
    showToastEdit.value = true;
    toastEditTimeout = resetTimeout(toastEditTimeout, () => {
      showToastEdit.value = false;
    });
  };

  const handleStatusAdded = () => {
    showToastAddStatus.value = true;
    addSuccess.value = true;
    toastAddStatusTimeout = resetTimeout(toastAddStatusTimeout, () => {
      showToastAddStatus.value = false;
    });
  };

  const handleStatusAddedF = () => {
    showToastAddStatus.value = true;
    addSuccess.value = false;
    toastAddStatusTimeout = resetTimeout(toastAddStatusTimeout, () => {
      showToastAddStatus.value = false;
    });
  };

  const handleStatusEdited = () => {
    showToastEditStatus.value = true;
    editSuccess.value = true;
    toastEditStatusTimeout = resetTimeout(toastEditStatusTimeout, () => {
      showToastEditStatus.value = false;
    });
  };

  const handleStatusEditedF = () => {
    showToastEditStatus.value = true;
    editSuccess.value = false;
    toastEditStatusTimeout = resetTimeout(toastEditStatusTimeout, () => {
      showToastEditStatus.value = false;
    });
  };

  const handleStatusDeleted = () => {
    showToastDeleteStatus.value = true;
    deleteSuccess.value = true;
    toastDeleteStatusTimeout = resetTimeout(toastDeleteStatusTimeout, () => {
      showToastDeleteStatus.value = false;
    });
  };

  const handleStatusDeletedF = () => {
    showToastDeleteStatus.value = true;
    deleteSuccess.value = false;
    toastDeleteStatusTimeout = resetTimeout(toastDeleteStatusTimeout, () => {
      showToastDeleteStatus.value = false;
    });
  };

  const handleLogin = () => {
    showToastLogin.value = true;
    toastLoginTimeout = resetTimeout(toastLoginTimeout, () => {
      showToastLogin.value = false;
    });
  };

  const closeToast = () => {
    showToastAdd.value = false;
    showToastEdit.value = false;
    showToastAddStatus.value = false;
    showToastEditStatus.value = false;
    showToastDeleteStatus.value = false;
    showToastLogin.value = false;

    // Clear all timeouts when toast is manually closed
    clearTimeout(toastAddTimeout);
    clearTimeout(toastEditTimeout);
    clearTimeout(toastAddStatusTimeout);
    clearTimeout(toastEditStatusTimeout);
    clearTimeout(toastDeleteStatusTimeout);
    clearTimeout(toastLoginTimeout);
  };

  return {
    showToastAdd,
    showToastEdit,
    showToastAddStatus,
    showToastEditStatus,
    showToastDeleteStatus,
    addSuccess,
    editSuccess,
    deleteSuccess,
    showToastLogin,
    handleTaskAdded,
    handleTaskEdited,
    handleStatusAdded,
    handleStatusAddedF,
    handleStatusEdited,
    handleStatusEditedF,
    handleStatusDeleted,
    handleStatusDeletedF,
    handleLogin,
    closeToast,
  };
});
