import { defineStore } from "pinia"
import { ref } from "vue"

export const useToastStore = defineStore("toast", () => {
  const showToastAdd = ref(false)
  const showToastEdit = ref(false)
  const showToastAddStatus = ref(false)
  const showToastEditStatus = ref(false)
  const showToastDeleteStatus = ref(false)
  const addSuccess = ref(false)
  const editSuccess = ref(false)
  const deleteSuccess = ref(false)
  const showToastLogin = ref(false)

  const handleTaskAdded = () => {
    showToastAdd.value = true
    console.log(showToastAdd.value)
    setTimeout(() => {
      showToastAdd.value = false
    }, 5000)
  }

  const handleTaskEdited = () => {
    showToastEdit.value = true
    console.log(showToastEdit.value)
    setTimeout(() => {
      showToastEdit.value = false
    }, 5000)
  }

  const handleStatusAdded = () => {
    showToastAddStatus.value = true
    addSuccess.value = true
    console.log(showToastAddStatus.value)
    setTimeout(() => {
      showToastAddStatus.value = false
    }, 5000)
  }

  const handleStatusAddedF = () => {
    showToastAddStatus.value = true
    addSuccess.value = false
    console.log(showToastAddStatus.value)
    setTimeout(() => {
      showToastAddStatus.value = false
    }, 5000)
  }

  const handleStatusEdited = () => {
    showToastEditStatus.value = true
    editSuccess.value = true
    console.log(showToastEditStatus.value)
    setTimeout(() => {
      showToastEditStatus.value = false
    }, 5000)
  }

  const handleStatusEditedF = () => {
    showToastEditStatus.value = true
    editSuccess.value = false
    console.log(showToastEditStatus.value)
    setTimeout(() => {
      showToastEditStatus.value = false
    }, 5000)
  }

  const handleStatusDeleted = () => {
    showToastDeleteStatus.value = true
    deleteSuccess.value = true
    console.log(showToastDeleteStatus.value)
    setTimeout(() => {
      showToastLogin.value = false
    }, 5000)
  }

  const handleStatusDeletedF = () => {
    showToastDeleteStatus.value = true
    deleteSuccess.value = false
    console.log(showToastDeleteStatus.value)
    setTimeout(() => {
      showToastDeleteStatus.value = false
    }, 5000)
  }

  const handleLogin = () => {
    showToastLogin.value = true
    console.log(showToastLogin.value)
    setTimeout(() => {
      showToastLogin.value = false
    }, 5000)
  }

  const closeToast = () => {
    showToastAdd.value = false;
    showToastEdit.value = false;
    showToastAddStatus.value = false;
    showToastEditStatus.value = false;
    showToastDeleteStatus.value = false;
    showToastLogin.value = false;
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
    closeToast
  }
})
