import { defineStore } from 'pinia';
import jwtDecode from 'vue-jwt-decode';

const fetchUsers = import.meta.env.VITE_API_USERS;

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || null, // ดึง token จาก localStorage
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
        localStorage.setItem('token', this.token);

        const decodedToken = jwtDecode.decode(this.token);
        this.fullname = decodedToken.name || 'Unknown';
      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    },

    async checkAuth() {
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
      }
    },

    logout() {
      this.token = null;
      this.fullname = '';
      localStorage.removeItem('token'); // ลบ token ออกจาก localStorage
    },
  },
});