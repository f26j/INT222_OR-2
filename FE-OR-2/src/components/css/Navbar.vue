<script setup>
import { ref, onMounted, computed } from 'vue';
import { useUserStore } from '@/stores/users';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();

// ใช้ computed เพื่อดึง fullname จาก store
const fullname = computed(() => userStore.fullname);

// เรียก checkAuth เมื่อโหลดหน้าเพื่อให้มั่นใจว่า fullname ถูกดึงจาก token
onMounted(() => {
  if (userStore.token) {
    userStore.checkAuth(); // ตรวจสอบ token และดึง fullname เมื่อมี token
  } else {
    router.push('/login'); // ถ้าไม่มี token ให้เปลี่ยนไปที่หน้า login
  }
});

const home = () => {
  router.push('/task'); // เปลี่ยนเส้นทางไปที่หน้า login หลังจาก logout
};

const logout = () => {
  userStore.logout();
  router.push('/login'); // เปลี่ยนเส้นทางไปที่หน้า login หลังจาก logout
};
</script>

<template>
  <div class="navbar bg-base-100 bg-opacity-50 shadow-md">
    <div class="flex-1 ml-2">
      <a @click="home" href="#" class="btn btn-ghost normal-case text-xl flex items-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9l9-7 9 7v11a1 1 0 01-1 1H4a1 1 0 01-1-1z" />
        </svg>
        ITB-KK
      </a>
    </div>
    <div class="flex-none mr-2 dropdown dropdown-end">
      <a href="#" class="btn btn-ghost normal-case text-xl flex items-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 rounded-full mr-2" viewBox="0 0 24 24" fill="currentColor">
          <path fill-rule="evenodd" d="M12 12c2.485 0 4.5-2.015 4.5-4.5S14.485 3 12 3 7.5 5.015 7.5 7.5 9.515 12 12 12zM3 18c0-3.315 2.685-6 6-6h6c3.315 0 6 2.685 6 6v1.5a.5.5 0 01-.5.5h-17a.5.5 0 01-.5-.5V18z" clip-rule="evenodd" />
        </svg>
        <!-- แสดงชื่อผู้ใช้ถ้ามีข้อมูล fullname -->
        <div tabindex="0" role="button">
          <p v-if="fullname">{{ fullname }}</p>
        </div>
        <ul tabindex="0" class="dropdown-content menu bg-white text-[#343365] rounded-box z-[1] w-36 p-2 mt-24 shadow">
          <li>
            <a @click="logout">
              <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24">
                <path fill="currentColor" d="M6 7V5a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11V5H8v2zm5.5-.5l-1.414 1.414L13.172 11H3v2h10.172l-3.086 3.086L11.5 17.5L17 12z"></path>
              </svg>Sign Out
            </a>
          </li>
        </ul>
      </a>
    </div>
  </div>
</template>