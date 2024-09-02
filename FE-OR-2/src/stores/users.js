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

        const decodedToken = jwtDecode.decode(this.token);
        this.fullname = decodedToken.name;

      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    },
  },
});