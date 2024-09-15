import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "../stores/users"; // import user store
import LoginView from "../views/LoginView.vue";
import HomeView from "../views/HomeView.vue";
import ModalDetail from "../components/ModalDetail.vue";
import ModalAdd from "../components/ModalAdd.vue";
import ModalEdit from "../components/ModalEdit.vue";
import StatusView from "../views/StatusView.vue";
import ModalAddStatus from "../components/ModalAddStatus.vue";
import ModalEditStatus from "../components/ModalEditStatus.vue";
import ManageStatus from "../components/ManageStatus.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/login", // หน้าเริ่มต้น
    },
    {
      path: "/task",
      name: "task",
      component: HomeView,
      children: [
        {
          path: ":id",
          name: "task-detail",
          component: ModalDetail,
        },
        {
          path: "add",
          name: "task-add",
          component: ModalAdd,
        },
        {
          path: ":id/edit",
          name: "task-edit",
          component: ModalEdit,
        },
      ],
    },
    {
      path: "/status",
      component: StatusView,
      children: [
        {
          path: "manage",
          name: "statusView",
          component: ManageStatus,
        },
        {
          path: "add",
          name: "status-add",
          component: ModalAddStatus,
        },
        {
          path: ":id/edit",
          name: "status-edit",
          component: ModalEditStatus,
        },
      ],
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
  ],
});

// Navigation Guard
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore(); // เข้าถึง store

  // อนุญาตให้เข้าถึงหน้า login ได้เสมอ
  if (to.name === 'login') {
    return next();
  }

  // ตรวจสอบ token ว่ามีอยู่หรือไม่ ถ้าไม่มีให้ redirect ไปที่ login
  if (!userStore.token) {
    try {
      await userStore.checkAuth(); // เรียก API เพื่อตรวจสอบการยืนยันตัวตน
    } catch (error) {
      console.error('Error checking authentication:', error);
    }

    if (!userStore.token) {
      return next({ name: 'login' }); // Redirect ไป login ถ้าไม่มี token หลังจาก checkAuth
    }
  }

  // ถ้ามี token ให้อนุญาตไปที่หน้าอื่นๆ
  next();
});

export default router;