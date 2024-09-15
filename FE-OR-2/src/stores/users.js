import { defineStore } from 'pinia';
import jwtDecode from 'vue-jwt-decode';

// เพิ่มการนำเข้า URL ของ API จาก environment variable
const fetchUsers = import.meta.env.VITE_API_USERS;

export const useUserStore = defineStore('user', {
  state: () => ({
    token: null,
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

        // ใช้ jwtDecode โดยไม่ต้องเรียก .decode
        const decodedToken = jwtDecode.decode(this.token);
        this.fullname = decodedToken.name || 'Unknown'; // กรณีที่ไม่มี name ใน token

      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    },

    // ฟังก์ชันเพื่อตรวจสอบการยืนยันตัวตน
    async checkAuth() {
      try {
        if (this.token) {
          const decodedToken = jwtDecode.decode(this.token);

          // ตรวจสอบว่า token หมดอายุหรือไม่
          const currentTime = Date.now() / 1000;
          if (decodedToken.exp < currentTime) {
            this.token = null;
            this.fullname = '';
            throw new Error('Token has expired');
          }

          this.fullname = decodedToken.name || 'Unknown';
        } else {
          throw new Error('No token found');
        }
      } catch (error) {
        console.error('Error checking authentication:', error);
        this.token = null;
        this.fullname = '';
      }
    },
  },
});