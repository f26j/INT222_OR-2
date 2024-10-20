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

import NotFoundPage from "../components/css/NotFoundPage.vue";
import AccessDeniedPage from "../components/css/AccessDeniedPage.vue";

const baseUrl = import.meta.env.VITE_API_BOARDS;

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
      meta: { requiresAuth: false },
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
      meta: { requiresAuth: false },
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
      meta: { requiresAuth: false },
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
  const requiresAuth = to.meta.requiresAuth;
  const boardId = to.params.boardId;

  // ตรวจสอบว่าผู้ใช้มี Token หรือไม่
  userStore.refreshToken();
  const hasToken = !!userStore.token;

  if (to.path.startsWith("/board/") && boardId) {
    try {
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        headers: {
          "Content-Type": "application/json",
          ...(hasToken && { Authorization: `Bearer ${userStore.token}` }),
        },
      });

      if (response.ok) {
        const board = await response.json();

        // ตรวจสอบสถานะการมองเห็นของบอร์ด
        if (board.visibility === "PRIVATE" && !hasToken) {
          console.warn("Access denied. Redirecting to login.");
          return next({ name: "login" });
        } else {
          console.log("Access to board granted.");
          return next();
        }
      } else if (response.status === 403) {
        console.warn("Access denied (403). Redirecting to AccessDenied.");
        return next({ name: "AccessDenied" });
      } else if (response.status === 404) {
        console.warn("Board not found. Redirecting to NotFound.");
        return next({ name: "NotFound" });
      }
    } catch (error) {
      console.error("Error fetching board visibility:", error);
      return next({ name: "NotFound" });
    }
  }

  if (requiresAuth && !hasToken) {
    console.warn("Authentication required. Redirecting to login.");
    return next({ name: "login" });
  }

  // ถ้าผู้ใช้มี Token แต่พยายามไปหน้า login ให้ Redirect ไปหน้า board
  if (hasToken && to.name === "login") {
    return next({ name: "board" });
  }

  next();
});

export default router;
