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
  const userStore = useUserStore();

  // ถ้าไม่มี token และไม่ใช่หน้า login ให้ redirect ไปที่ login
  if (!userStore.token) {
    if (to.name !== 'login') {
      return next({ name: 'login' });
    }
    return next();
  }

  // ถ้า token มีอยู่และผู้ใช้พยายามเข้าหน้า login ให้ redirect ไปที่ task
  if (userStore.token && to.name === 'login') {
    return next({ name: 'task' });
  }

  // ตรวจสอบ token ว่าหมดอายุหรือไม่
  try {
    await userStore.checkAuth(); // ตรวจสอบ token
  } catch (error) {
    userStore.logout(); // ทำการ logout ผู้ใช้
    return next({ name: 'login' });  // Redirect ไปที่ login
  }

  next();
});

export default router;