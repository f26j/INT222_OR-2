import { defineStore } from "pinia";
import jwtDecode from "vue-jwt-decode";

const fetchUsers = import.meta.env.VITE_API_LOGIN;

export const useUserStore = defineStore("user", {
  state: () => ({
    token:
      typeof window !== "undefined" ? localStorage.getItem("jwtToken") : null, // ตรวจสอบการเข้าถึง localStorage
    fullname: "",
    oid: "", // เปลี่ยนเป็น oid
  }),
  actions: {
    async fetchUserData(username, password) {
      try {
        const response = await fetch(fetchUsers, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            userName: username,
            password: password,
          }),
        });

        if (!response.ok) {
          throw new Error("Failed to fetch token");
        }

        const data = await response.json();
        this.token = data.access_token;

        // เก็บ token ไว้ใน localStorage
        if (typeof window !== "undefined") {
          localStorage.setItem("jwtToken", this.token);
        }

        this.decodeToken(); // ถอดรหัสข้อมูลจาก token
      } catch (error) {
        console.error("Error fetching user data:", error);
      }
    },

    decodeToken() {
      try {
        if (this.token) {
          const decodedToken = jwtDecode.decode(this.token);
          this.fullname = decodedToken.name || "Unknown"; // ถอดรหัส fullname จาก token
          this.oid = decodedToken.oid || ""; // ถอดรหัส oid จาก token
        }
      } catch (error) {
        console.error("Error decoding token:", error);
      }
    },

    async checkAuth() {
      try {
        if (this.token) {
          const decodedToken = jwtDecode.decode(this.token);
          const currentTime = Date.now() / 1000;
          if (decodedToken.exp < currentTime) {
            this.logout(); // ถ้า token หมดอายุ ทำการ logout
            throw new Error("Token has expired");
          }
          this.fullname = decodedToken.name || "Unknown";
          this.oid = decodedToken.oid || ""; // ตั้งค่า oid จาก token
        } else {
          throw new Error("No token found");
        }
      } catch (error) {
        console.error("Error checking authentication:", error);
        this.logout();
      }
    },

    logout() {
      this.token = null;
      this.fullname = "";
      this.oid = ""; // รีเซ็ตค่า oid
      if (typeof window !== "undefined") {
        localStorage.removeItem("jwtToken"); // ลบ token ออกจาก localStorage
      }
    },
  },
});
