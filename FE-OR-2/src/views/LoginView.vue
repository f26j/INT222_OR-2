<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useToastStore } from "@/stores/toast.js";
import { useUserStore } from '@/stores/users.js';
import { storeToRefs } from "pinia";
import Bg_effect from "../components/css/Background.vue";
import Toast_login from "../components/toast/ToastLogin.vue";

const toastStore = useToastStore();
const { showToastLogin, closeToast } = storeToRefs(toastStore);
const router = useRouter();

const username = ref('');
const password = ref('');

const isLoginValid = computed(() => {
  return username.value.trim() !== "" && password.value.trim() !== "";
});

const userStore = useUserStore();

const handleSubmit = async (event) => {
  event.preventDefault();

  if (!isLoginValid.value) {
    toastStore.handleLogin(); // แสดงข้อความเมื่อกรอกข้อมูลไม่ครบ
    return;
  }

  try {
    await userStore.fetchUserData(username.value, password.value);

    if (userStore.token) {
      localStorage.setItem("jwtToken", userStore.token); // เก็บ JWT Token ใน localStorage
      router.push("/task"); // เปลี่ยนเส้นทางไปยัง /tasks หลังจาก login สำเร็จ
    } else {
      toastStore.handleLogin(); // แสดงข้อความ error ถ้า token ไม่มีหรือ login ไม่สำเร็จ
    }
  } catch (error) {
    console.error("Error during login request:", error);
    toastStore.handleLogin(); // แจ้งเตือนเมื่อเกิดข้อผิดพลาด
  }
};

console.log()
</script>

<template>
  <div class="flex min-h-full flex-col justify-center items-center px-6 py-32 lg:px-8">
    <div class="w-full bg-white bg-opacity-60 rounded-lg shadow dark:border md:mt-0 sm:max-w-md p-6 space-y-4 md:space-y-6 sm:p-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <img
          class="mx-auto my-2 h-10 w-auto"
          src="../../public/ORv1.2.ico"
          alt="OR-2"
        />
        <h2
          class="mt-8 text-center text-2xl font-bold leading-9 tracking-tight text-[#47467b]"
        >
          Welcome to ITB-KK
        </h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form class="space-y-6" @submit.prevent="handleSubmit" method="POST">
          <div>
            <label
              class="block text-sm font-medium leading-6 text-[#47467b]"
              for="username"
              >Username</label
            >
            <div class="mt-2">
              <input
                class="itbkk-username block w-full rounded-md border-0 py-1.5 p-2 bg-white text-gray-600 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-300 focus:ring-2 focus:ring-inset sm:text-sm sm:leading-6"
                v-model="username"
                id="username"
                name="username"
                type="text"
                placeholder="Username"
                autocomplete="username"
                maxlength="50"
                required
              />
            </div>
          </div>

          <div>
            <div class="flex items-center justify-between">
              <label
                class="block text-sm font-medium leading-6 text-[#47467b]"
                for="password"
                >Password</label
              >
            </div>
            <div class="mt-2">
              <input
                class="itbkk-password block w-full rounded-md border-0 py-1.5 p-2 bg-white text-gray-600 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-300 focus:ring-2 focus:ring-inset sm:text-sm sm:leading-6"
                v-model="password"
                id="password"
                name="password"
                type="password"
                placeholder="Password"
                autocomplete="current-password"
                maxlength="14"
                required
              />
            </div>
          </div>

          <div>
            <button
              :class="{ disabled: !isLoginValid }"
              class="itbkk-button-signin disabled:cursor-not-allowed disabled:bg-gray-400 disabled:hover:scale-100 flex w-full justify-center rounded-md bg-[#8e5bb1] px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-[#6a4581] hover:scale-105 transition-transform duration-300 ease-in-out"
              type="submit"
              :disabled="!isLoginValid"
            >
              Sign in
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <Bg_effect />
  <Toast_login v-if="showToastLogin" @close="closeToast"></Toast_login>
</template>

<style scope>
@import url("https://fonts.googleapis.com/css?family=Proza+Libre|Fira+Mono");
@import url("https://fonts.googleapis.com");
@import url("https://fonts.gstatic.com");
@import url("https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap");

div {
  font-family: "Ubuntu", sans-serif;
}
</style>