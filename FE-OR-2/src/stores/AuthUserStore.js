// stores/AuthUserStore.js
import { defineStore } from "pinia";
import jwtDecode from "vue-jwt-decode";

const fetchLogin = import.meta.env.VITE_API_LOGIN;
const fetchRefreshToken = import.meta.env.VITE_API_REFRESH;

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("jwtToken") || null,
    refreshToken: localStorage.getItem("refreshToken") || null,
    fullname: "Guest",
    oid: "",
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
  },

  actions: {
    async fetchUserData(username, password) {
      try {
        const response = await fetch(fetchLogin, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ userName: username, password: password }),
        });

        if (!response.ok) {
          const error = await response.json();
          throw new Error(error.message || "Failed to fetch token");
        }

        const data = await response.json();
        console.log("Login successful. Tokens received:", data);

        // ตั้งค่า access_token และ refresh_token
        this.setTokens(data.access_token, data.refresh_token);
      } catch (error) {
        console.error("Error fetching user data:", error.message);
        throw error;
      }
    },

    setTokens(accessToken, refreshToken) {
      console.log("Setting tokens...");

      // ตั้งค่า token และเก็บใน localStorage
      this.token = accessToken;
      this.refreshToken = refreshToken;
      localStorage.setItem("jwtToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);

      // Decode ข้อมูลจาก token ใหม่เพื่ออัพเดต fullname และ oid
      this.decodeToken();
    },

    decodeToken() {
      try {
        if (this.token) {
          const decoded = jwtDecode.decode(this.token);
          this.fullname = decoded.name || "Guest";
          this.oid = decoded.oid || null;
          console.log("Decoded token:", decoded);
        }
      } catch (error) {
        console.error("Error decoding token:", error.message);
        this.logout();
      }
    },

    async refreshAccessToken() {
      try {
        console.log("Refreshing access token...");

        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) throw new Error("No refresh token available.");

        const response = await fetch(fetchRefreshToken, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${refreshToken}`,
          },
          body: JSON.stringify({ refresh_token: refreshToken }),
        });

        if (!response.ok) {
          console.warn("Refresh token failed with status:", response.status);
          this.logout();
          throw new Error("Failed to refresh token");
        }

        const data = await response.json();
        this.setTokens(data.access_token, refreshToken);
        console.log("Token refreshed successfully.");
      } catch (error) {
        console.error("Error refreshing token:", error.message);
        this.logout();
      }
    },
    isTokenExpired() {
      if (!this.token) return true;
      const decoded = jwtDecode.decode(this.token);
      const currentTime = Date.now() / 1000;
      return decoded.exp < currentTime;
    },

    logout() {
      console.log("Logging out...");
      this.token = null;
      this.refreshToken = null;
      this.fullname = "Guest";
      this.oid = "";
      localStorage.removeItem("jwtToken");
      localStorage.removeItem("refreshToken");
    },

    async initializeStore() {
      try {
        if (this.token && this.isTokenExpired()) {
          console.log("Token expired. Attempting to refresh...");
          await this.refreshAccessToken();
        }

        // ถ้า token ใช้งานได้ ให้ decode token เพื่ออัปเดต fullname และ oid
        if (this.token) {
          this.decodeToken();
        } else {
          console.warn("No valid token found.");
          this.logout(); // ล้างข้อมูลถ้าไม่มี token
        }
      } catch (error) {
        console.error("Initialization failed:", error.message);
        this.logout();
      }

      // ตรวจสอบการเปลี่ยนแปลง token ผ่าน localStorage
      window.addEventListener("storage", (event) => {
        if (event.key === "jwtToken") {
          console.log("Token changed. Refreshing...");
          this.refreshAccessToken();
        }
      });
    },
  },
});
