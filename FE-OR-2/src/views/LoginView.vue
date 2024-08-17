<script setup>
import Bg_effect from "../components/css/Background.vue"
import Toast_login from "../components/toast/ToastLogin.vue"
import { useToastStore} from "@/stores/toast";

const toastStore = useToastStore();
</script>
<template>
  <div
    class="flex min-h-full flex-col justify-center items-center px-6 py-32 lg:px-8"
  >
    <div
      class="w-full bg-white bg-opacity-60 rounded-lg shadow dark:border md:mt-0 sm:max-w-md p-6 space-y-4 md:space-y-6 sm:p-8"
    >
      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <img
          class="mx-auto my-2 h-10 w-auto"
          src="../../public/ORv1.2.ico"
          alt="Your Company"
        />
        <h2
          class="mt-8 text-center text-2xl font-bold leading-9 tracking-tight text-[#47467b]"
        >
          Welcome to ITB-KK
        </h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form class="space-y-6" action="#" method="POST">
          <div class="itbkk-username">
            <label
              for="email"
              class="block text-sm font-medium leading-6 text-[#47467b]"
              >Username</label
            >
            <div class="mt-2">
              <input placeholder="Username"
                required
                class="block w-full rounded-md border-0 py-1.5 p-2 bg-white text-gray-600 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset sm:text-sm sm:leading-6"
              />
            </div>
          </div>

          <div class="itbkk-password">
            <div class="flex items-center justify-between">
              <label
                for="password"
                class="block text-sm font-medium leading-6 text-[#47467b]"
                >Password</label
              >
              <!-- <div class="text-sm">
              <a
                href="#"
                class="font-semibold text-indigo-600 hover:text-indigo-500"
                >Forgot password?</a
              >
            </div> -->
            </div>
            <div class="mt-2">
              <input placeholder="Password"
                id="password"
                name="password"
                type="password"
                autocomplete="current-password"
                required
                class="block w-full rounded-md border-0 py-1.5 p-2 bg-white text-gray-600 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
            </div>
          </div>

          <div>
            <button
              type="submit"
              class="itbkk-button-signin flex w-full justify-center rounded-md bg-[#8e5bb1] px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-[#6a4581] hover:scale-105 transition-transform duration-300 ease"
            >
              Sign in
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <Bg_effect />
  <Toast_login/>
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
