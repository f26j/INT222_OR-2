<script setup>
import { ref, onMounted, computed } from "vue";
import { useUserStore } from "@/stores/AuthUserStore";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();

const fullname = computed(() => userStore.fullname);

const dropdownOpen = ref(false);

const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value;
};


onMounted(() => {
  if (userStore.token) {
    userStore.checkAuth(); 
  } else {
    router.push("/login");
  }
});

const home = () => {
  router.push("/board");
};

const logout = () => {
  userStore.logout();
  console.log("Token after logout:", userStore.token); 
  router.push("/login");
  dropdownOpen.value = false;
};

</script>

<template>
  <div class="navbar bg-base-100 bg-opacity-50 shadow-md">
    <div class="itbkk-home flex-1 ml-2">
      <a
        @click="home"
        class="btn btn-ghost normal-case text-xl flex items-center"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-6 w-6 mr-2"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M3 9l9-7 9 7v11a1 1 0 01-1 1H4a1 1 0 01-1-1z"
          />
        </svg>
        ITBKK
      </a>
    </div>
    <div class="itbkk-fullname flex-none mr-2 dropdown dropdown-end">
      <a
        href="#"
        class="btn btn-ghost normal-case text-xl flex items-center"
        @click="toggleDropdown"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-10 w-10 rounded-full mr-2"
          viewBox="0 0 24 24"
          fill="currentColor"
        >
          <path
            fill-rule="evenodd"
            d="M12 12c2.485 0 4.5-2.015 4.5-4.5S14.485 3 12 3 7.5 5.015 7.5 7.5 9.515 12 12 12zM3 18c0-3.315 2.685-6 6-6h6c3.315 0 6 2.685 6 6v1.5a.5.5 0 01-.5.5h-17a.5.5 0 01-.5-.5V18z"
            clip-rule="evenodd"
          />
        </svg>
        <div tabindex="0" role="button">
          <!-- แสดงชื่อผู้ใช้ถ้ามีข้อมูล fullname -->
          <p v-if="fullname" class="text-sm font-semibold">{{ fullname }}</p>
        </div>
      </a>
      <!-- ใช้ v-if เพื่อควบคุมการแสดงผลของ dropdown -->
      <ul
        v-if="dropdownOpen"
        class="dropdown-content menu bg-white text-[#343365] rounded-box z-[1] w-36 p-2 mt-2 shadow"
      >
        <li>
          <a @mousedown="logout" class="itbkk-sign-out flex items-center space-x-2 group"> <!-- ใช้ @mousedown -->
            <svg
              fill="#000000"
              width="20px"
              height="20px"
              viewBox="0 0 512 512"
              xmlns="http://www.w3.org/2000/svg"
              class="hover-red-icon transition-colors duration-300"
            >
              <path
                d="M497 273L329 441c-15 15-41 4.5-41-17v-96H152c-13.3 0-24-10.7-24-24v-96c0-13.3 10.7-24 24-24h136V88c0-21.4 25.9-32 41-17l168 168c9.3 9.4 9.3 24.6 0 34zM192 436v-40c0-6.6-5.4-12-12-12H96c-17.7 0-32-14.3-32-32V160c0-17.7 14.3-32 32-32h84c6.6 0 12-5.4 12-12V76c0-6.6-5.4-12-12-12H96c-53 0-96 43-96 96v192c0 53 43 96 96 96h84c6.6 0 12-5.4 12-12z"
              ></path>
            </svg>
            <span class="transition-colors duration-300">Sign Out</span>
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.navbar {
  position: fixed; /* ทำให้ navbar ติดอยู่บนสุดของหน้าจอ */
  top: 0; /* ให้ชิดด้านบน */
  left: 0;
  right: 0;
  z-index: 99999;
}
  
.hover-red-icon {
  transition: fill 0.3s ease;
}

.group:hover .hover-red-icon {
  fill: #ef4444; /* red-500 */
}

.group:hover span {
  color: #ef4444; /* red-500 */
}

a {
  color: black;
  transition: color 0.3s ease;
}

.itbkk-fullname, .itbkk-home, .itbkk-sign-out {
  user-select: none;
}
</style>
