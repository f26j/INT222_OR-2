import { defineStore } from 'pinia';
import jwtDecode from 'vue-jwt-decode';
import { useRouter } from 'vue-router';

const fetchUsers = import.meta.env.VITE_API_USERS;

export const useUserStore = defineStore('user', {
  state: () => ({
    token: typeof window !== "undefined" ? localStorage.getItem('token') : null,  // ตรวจสอบการเข้าถึง localStorage
    fullname: '',
  }),
  actions: {
    async fetchUserData(username, password) {
      try {
        const response = await fetch(fetchUsers, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            userName: username,
            password: password,
          }),
        });

        if (!response.ok) {
          throw new Error('Failed to fetch token');
        }

        const data = await response.json();
        this.token = data.access_token;

        // เก็บ token ไว้ใน localStorage
        if (typeof window !== "undefined") {
          localStorage.setItem('token', this.token);
        }

        this.decodeToken();
      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    },

    decodeToken() {
      try {
        if (this.token) {
          const decodedToken = jwtDecode.decode(this.token);
          this.fullname = decodedToken.name || 'Unknown';
        }
      } catch (error) {
        console.error('Error decoding token:', error);
      }
    },

    async checkAuth() {
      const router = useRouter();  // เพิ่ม router เพื่อใช้งาน
      try {
        if (this.token) {
          const decodedToken = jwtDecode.decode(this.token);
          const currentTime = Date.now() / 1000;
          if (decodedToken.exp < currentTime) {
            this.logout(); // ถ้า token หมดอายุ ทำการ logout
            throw new Error('Token has expired');
          }
          this.fullname = decodedToken.name || 'Unknown';
        } else {
          throw new Error('No token found');
        }
      } catch (error) {
        console.error('Error checking authentication:', error);
        this.logout();
        router.push('/login');  // Redirect ไปยังหน้า login
      }
    },

    logout() {
      const router = useRouter(); // ใช้งาน router สำหรับ redirect
      this.token = null;
      this.fullname = '';
      if (typeof window !== "undefined") {
        localStorage.removeItem('token');  // ลบ token ออกจาก localStorage
      }
      router.push('/login');  // Redirect ไปยังหน้า login
    }
  },
});