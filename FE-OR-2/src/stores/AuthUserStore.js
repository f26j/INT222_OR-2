// stores/AuthUserStore.js
import { defineStore } from "pinia";
import jwtDecode from "vue-jwt-decode";

const fetchLogin = import.meta.env.VITE_API_LOGIN;

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("jwtToken") || null,
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
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ userName: username, password: password }),
        });

        if (!response.ok) {
          const error = await response.json();
          throw new Error(error.message || "Failed to fetch token");
        }

        const data = await response.json();
        this.setToken(data.access_token);
      } catch (error) {
        console.error("Error fetching user data:", error.message);
        throw error;
      }
    },

    setToken(token) {
      this.token = token;
      localStorage.setItem("jwtToken", token);
      this.decodeToken();
    },

    decodeToken() {
      try {
        if (this.token) {
          const decodedToken = jwtDecode.decode(this.token);
          this.fullname = decodedToken.name || "Guest";
          this.oid = decodedToken.oid || null;
        }
      } catch (error) {
        console.error("Error decoding token:", error);
        this.logout();
      }
    },

    async checkAuth() {
      try {
        this.refreshToken();
        if (!this.token) throw new Error("No token found");

        const decodedToken = jwtDecode.decode(this.token);
        const currentTime = Date.now() / 1000;

        if (decodedToken.exp < currentTime) {
          console.warn("Token has expired.");
          throw new Error("Token has expired");
        }

        this.fullname = decodedToken.name || "Guest";
        this.oid = decodedToken.oid || "";
      } catch (error) {
        console.error("Authentication check failed:", error);
        this.logout();
      }
    },

    refreshToken() {
      const token = localStorage.getItem("jwtToken");
      if (token !== this.token) {
        this.token = token;
        this.decodeToken();
      }
    },

    logout() {
      this.token = null;
      this.fullname = "";
      this.oid = "";
      localStorage.removeItem("jwtToken");
    },

    initializeStore() {
      this.checkAuth();

      window.addEventListener("storage", (event) => {
        if (event.key === "jwtToken") {
          console.log("Token changed, updating store...");
          this.refreshToken();
        }
      });
    },
  },
});
