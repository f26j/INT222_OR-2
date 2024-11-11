<script setup>
import { onMounted } from "vue";
import { useUserStore } from "@/stores/AuthUserStore.js";

const userStore = useUserStore();

const props = defineProps({
  showModal: Boolean,
  taskId: {
    type: Number,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  indexs: {
    type: Number,
    required: true,
  },
});

onMounted(async () => {
  if (!userStore.token) {
    return router.push({ name: "AccessDenied" });
  }
});

defineEmits(["closeModal", "confirmDeleteModal"]);
</script>
<template>
  <div class="details fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
    <div
      class="modalDelete rounded-lg text-center shadow-xlp-6 w-min"
      role="dialog"
      aria-modal="true"
    >
      <div
        class="confirmModal-content flex flex-col justify-center items-center"
      >
        <svg
          class="mb-3"
          xmlns="http://www.w3.org/2000/svg"
          width="50"
          height="50"
          viewBox="0 0 24 24"
        >
          <path
            fill="#e05555"
            d="M12 21a9 9 0 1 1 9-9a9 9 0 0 1-9 9m0-16.5a7.5 7.5 0 1 0 7.5 7.5A7.5 7.5 0 0 0 12 4.5"
          />
          <path
            fill="#e05555"
            d="M12 13a.76.76 0 0 1-.75-.75v-3.5a.75.75 0 0 1 1.5 0v3.5A.76.76 0 0 1 12 13m0 3a.76.76 0 0 1-.75-.75v-.5a.75.75 0 0 1 1.5 0v.5A.76.76 0 0 1 12 16"
          />
        </svg>
        <span class="text-2xl font-bold mb-4">Delete a Task</span>
        <div
          class="itbkk-message w-10/12 break-all text-ellipsis overflow-hidden"
        >
          Do you want to delete the task <br />
          number {{ indexs + 1 }} - "{{ title }}" ?
        </div>
        <div
          class="Button h-32 flex justify-center items-center gap-10 rounded-full"
        >
          <button class="itbkk-button-cancel" @click="$emit('closeModal')">
            Cancel
          </button>
          <button
            class="itbkk-button-confirm"
            @click="$emit('confirmDeleteModal', taskId)"
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.details {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
}

.modalDelete {
  background-color: #ffffff;
  color: black;
  max-width: 600px;
  width: 600px;
  min-height: 200px;
  padding-top: 15px;
  border-radius: 40px;
  overflow: hidden;
}

.confirmModal-content {
  padding-top: 20px;
  font-size: large;
}

.Button button {
  font-size: 1.2rem;
  width: 110px;
  height: 50px;
  border-radius: 20px;
  transition: transform 0.3s ease-in-out;
}

.Button button:hover {
  transform: scale(1.08);
  transition: transform 0.3s ease;
  box-shadow: 5px 5px 5px #c6c6c646;
}

.itbkk-button-confirm {
  background-color: #e05555;
  color: #ffffff;
}

.itbkk-button-cancel {
  background-color: #e0e0e0;
  color: #808080;
}
</style>
