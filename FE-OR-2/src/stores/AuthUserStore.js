import { defineStore } from "pinia";
import jwtDecode from "vue-jwt-decode";

const fetchLogin = import.meta.env.VITE_API_LOGIN;
const fetchRefreshToken = import.meta.env.VITE_API_REFRESH;
let isRefreshing = false;

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("jwtToken") || null,
    refreshToken: localStorage.getItem("refreshToken") || null,
    fullname: "Guest",
    oid: "",
    email: "",
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
        this.setTokens(data.access_token, data.refresh_token);
      } catch (error) {
        console.error("Error fetching user data:", error.message);
        throw error;
      }
    },

    setTokens(accessToken, refreshToken) {
      this.token = accessToken;
      this.refreshToken = refreshToken;
      localStorage.setItem("jwtToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);

      this.decodeToken();
    },

    decodeToken() {
      try {
        if (this.token) {
          const decoded = jwtDecode.decode(this.token);
          this.fullname = decoded.name || "Guest";
          this.oid = decoded.oid || null;
          this.email = decoded.email || null;
        }
      } catch (error) {
        console.error("Error decoding token:", error.message);
        this.logout();
        throw new Error("Invalid token detected");
      }
    },

    isTokenExpired() {
      if (!this.token) return true;
      const decoded = jwtDecode.decode(this.token);
      const currentTime = Date.now() / 1000;
      return decoded && decoded.exp < currentTime;
    },

    logout() {
      this.token = null;
      this.refreshToken = null;
      this.fullname = "Guest";
      this.oid = "";
      this.email = "";
      localStorage.removeItem("jwtToken");
      localStorage.removeItem("refreshToken");

      document.cookie = "jwtToken=; Max-Age=0; path=/;";
      document.cookie = "refreshToken=; Max-Age=0; path=/;";
    },

    async initializeStore() {
      try {
        if (this.token) {
          if (this.isTokenExpired()) {
            console.log("Token expired. Attempting to refresh...");
            const refreshSuccess = await this.safeRefreshToken();
            if (!refreshSuccess) {
              console.warn("Failed to refresh token. Logging out...");
              this.logout();
              return;
            }
          }
          this.decodeToken();
        } else {
          this.logout(); // Ensure clean state if no token exists
        }
      } catch (error) {
        console.error("Initialization failed:", error.message);
        this.logout();
      }

      // Listen for localStorage changes
      window.addEventListener("storage", (event) => {
        if (event.key === "jwtToken" || event.key === "refreshToken") {
          if (!localStorage.getItem("jwtToken") || !localStorage.getItem("refreshToken")) {
            console.warn("Token removed from storage. Logging out...");
            this.logout();
          } else {
            console.log("Token updated in storage. Attempting refresh...");
            this.token = localStorage.getItem("jwtToken");
            this.refreshToken = localStorage.getItem("refreshToken");
            this.decodeToken();
          }
        }
      });
    },

    async safeRefreshToken() {
      try {
        if (isRefreshing) return true; // Prevent multiple refresh attempts
        isRefreshing = true;

        await this.refreshAccessToken();
        return true; // Successfully refreshed token
      } catch (error) {
        console.error("Failed to safely refresh token:", error.message);
        return false; // Signal that refresh failed
      } finally {
        isRefreshing = false;
      }
    },

    async refreshAccessToken() {
      if (!this.refreshToken) {
        console.warn("No refresh token available.");
        this.logout();
        return;
      }

      try {
        console.log("Refreshing access token...");

        const response = await fetch(fetchRefreshToken, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${this.refreshToken}`,
          },
          body: JSON.stringify({ refresh_token: this.refreshToken }),
        });

        if (!response.ok) {
          console.warn(
            "Refresh token failed with status:",
            response.status,
            "Response:",
            await response.json()
          );
          this.logout();
          throw new Error("Failed to refresh token");
        }

        const data = await response.json();
        this.setTokens(data.access_token, this.refreshToken);
      } catch (error) {
        console.error("Error refreshing token:", error.message);
        this.logout();
      }
    },
  },
});
