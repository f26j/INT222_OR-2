// stores/user.js
import { defineStore } from "pinia";
import jwtDecode from "vue-jwt-decode";

const fetchLogin = import.meta.env.VITE_API_LOGIN;

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("jwtToken") || null,
    fullname: "Gust",
    oid: "",
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
  },

  actions: {
    // ฟังก์ชัน Login
    async fetchUserData(username, password) {
      try {
        const response = await fetch(fetchLogin, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ userName: username, password: password }),
        });

        if (!response.ok) throw new Error("Failed to fetch token");

        const data = await response.json();
        this.setToken(data.access_token);
      } catch (error) {
        console.error("Error fetching user data:", error);
      }
    },

    // ตั้งค่า token และบันทึกใน LocalStorage
    setToken(token) {
      this.token = token;
      localStorage.setItem("jwtToken", token);
      this.decodeToken();
    },

    // ถอดรหัสข้อมูลจาก JWT token
    decodeToken() {
      try {
        if (this.token) {
          const decodedToken = jwtDecode.decode(this.token);
          this.fullname = decodedToken.name || "Unknown";
          this.oid = decodedToken.oid || "";
        }
      } catch (error) {
        console.error("Error decoding token:", error);
        this.logout();
      }
    },

    // ตรวจสอบความถูกต้องของ token
    async checkAuth() {
      try {
        this.refreshToken(); // อัปเดต token จาก LocalStorage
        if (!this.token) throw new Error("No token found");

        const decodedToken = jwtDecode.decode(this.token);
        const currentTime = Date.now() / 1000;

        if (decodedToken.exp < currentTime) {
          console.warn("Token has expired.");
          throw new Error("Token has expired");
        }

        this.fullname = decodedToken.name || "Unknown";
        this.oid = decodedToken.oid || "";
      } catch (error) {
        console.error("Authentication check failed:", error);
        this.logout();
      }
    },

    // ดึง token จาก LocalStorage เพื่ออัปเดต state
    refreshToken() {
      const token = localStorage.getItem("jwtToken");
      if (token !== this.token) {
        this.token = token;
        this.decodeToken();
      }
    },

    // ลบ token ออกจาก state และ LocalStorage
    logout() {
      this.token = null;
      this.fullname = "";
      this.oid = "";
      localStorage.removeItem("jwtToken");
    },

    // ตรวจสอบการเปลี่ยนแปลงของ LocalStorage
    initializeStore() {
      this.checkAuth();

      // ตรวจจับการเปลี่ยนแปลงใน LocalStorage
      window.addEventListener("storage", (event) => {
        if (event.key === "jwtToken") {
          console.log("Token changed, updating store...");
          this.refreshToken();
        }
      });
    },
  },
});
