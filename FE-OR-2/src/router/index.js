import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "../stores/authUser";

// Import Component
import LoginView from "../views/LoginView.vue";
import BoardView from "@/views/BoardView.vue";
import ModalAddBoard from "@/components/ModalAddBoard.vue";

import TaskView from "../views/TaskView.vue";
import ModalAddTask from "../components/ModalAddTask.vue";
import ModalEditTask from "../components/ModalEditTask.vue";
import ModalDetailTask from "../components/ModalDetailTask.vue";

import StatusView from "../views/StatusView.vue";
import ModalAddStatus from "../components/ModalAddStatus.vue";
import ModalEditStatus from "../components/ModalEditStatus.vue";
import ManageStatus from "../components/ManageStatus.vue";

import NotFoundPage from "../components/css/NotFoundPage.vue"
import AccessDeniedPage from "../components/css/AccessDeniedPage.vue"

// Router Configuration
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/",
      redirect: "/board",
    },
    {
      path: "/board", // กลุ่มของ Board
      name: "board",
      component: BoardView,
      meta: { requiresAuth: true },
      children: [
        {
          path: "add", // เพิ่ม Board
          name: "board-add",
          component: ModalAddBoard,
        },
      ],
    },
    {
      path: "/:pathMatch(.*)*", // เส้นทางสำหรับหน้า NotFoundPage
      name: "NotFound",
      component: NotFoundPage,
    },
    {
      path: "/board/:boardId", // กลุ่มของ Task
      name: "board-tasks",
      component: TaskView, // ใช้ TaskView สำหรับการแสดง Task หลัก
      meta: { requiresAuth: true },
      children: [
        {
          path: "task/add", // เพิ่ม Task ใหม่
          name: "task-add",
          component: ModalAddTask,
        },
        {
          path: "task/:taskId", // รายละเอียด Task
          name: "task-detail",
          component: ModalDetailTask,
        },
        {
          path: "task/:taskId/edit", // แก้ไข Task
          name: "task-edit",
          component: ModalEditTask,
        },
      ],
    },
    {
      path: "/board/:boardId/status", // กลุ่มของ Status
      component: StatusView,
      meta: { requiresAuth: true },
      children: [
        {
          path: "manage", // จัดการ Status
          name: "status-manage",
          component: ManageStatus,
        },
        {
          path: "add", // เพิ่ม Status ใหม่
          name: "status-add",
          component: ModalAddStatus,
        },
        {
          path: ":statusId/edit", // แก้ไข Status
          name: "status-edit",
          component: ModalEditStatus,
        },
      ],
    },
    {
      path: "/forbidden", // เส้นทางสำหรับหน้า AccessDeniedPage
      name: "AccessDenied",
      component: AccessDeniedPage,
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  // if (!userStore.token && to.meta.requiresAuth) {
  //   return next({ name: "login" });
  // }
  // if (userStore.token && to.name === "login") {
  //   return next({ name: "board" });
  // }
  // if (userStore.token) {
  //   try {
  //     await userStore.checkAuth();
  //   } catch (error) {
  //     userStore.logout();
  //     return next({ name: "login" });
  //   }
  // }
  next();
});

export default router;
