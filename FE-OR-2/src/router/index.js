import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "../stores/AuthUserStore";

// Import Component
import LoginView from "../views/LoginView.vue";
import BoardView from "@/views/BoardView.vue";
import ModalAddBoard from "@/components/modal/ModalAddBoard.vue";

import TaskView from "../views/TaskView.vue";
import ModalAddTask from "../components/modal/ModalAddTask.vue";
import ModalEditTask from "../components/modal/ModalEditTask.vue";
import ModalDetailTask from "../components/modal/ModalDetailTask.vue";

import StatusView from "../views/StatusView.vue";
import ModalAddStatus from "../components/modal/ModalAddStatus.vue";
import ModalEditStatus from "../components/modal/ModalEditStatus.vue";
import ManageStatus from "../components/modal/ManageStatus.vue";

import CollabBoard from "../views/CollabBoard.vue";

import NotFoundPage from "../components/css/NotFoundPage.vue";
import AccessDeniedPage from "../components/css/AccessDeniedPage.vue";
import UnauthorizedPage from "@/components/css/UnauthorizedPage.vue";

const baseUrl = import.meta.env.VITE_API_BOARDS;

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "login",
      component: LoginView,
      meta: { title: "ITBKK - Login" },
    },
    { path: "/", redirect: "/board" },
    {
      path: "/board",
      name: "board",
      component: BoardView,
      meta: { requiresAuth: true, title: "ITBKK - Board" },
      children: [{ path: "add", name: "board-add", component: ModalAddBoard, }],
    },
    { path: "/:pathMatch(.*)*", name: "NotFound", component: NotFoundPage },
    {
      path: "/board/:boardId/task",
      name: "board-tasks",
      component: TaskView,
      meta: { requiresAuth: false, title: "ITBKK - Task" },
      children: [
        { path: "add", name: "task-add", component: ModalAddTask, meta: { requiresAuth: true} },
        {
          path: ":taskId",
          name: "task-detail",
          component: ModalDetailTask,
        },
        {
          path: ":taskId/edit",
          name: "task-edit",
          component: ModalEditTask,
          meta: { requiresAuth: true}
        },
      ],
    },
    {
      path: "/board/:boardId/status",
      name: "board-status",
      component: StatusView,
      meta: { requiresAuth: false, title: "ITBKK - Status Management" },
      children: [
        { path: "manage", name: "status-manage", component: ManageStatus },
        { path: "add", name: "status-add", component: ModalAddStatus, meta: { requiresAuth: true} },
        {
          path: ":statusId/edit",
          name: "status-edit",
          component: ModalEditStatus,
          meta: { requiresAuth: true}
        },
      ],
    },
    {
      path: "/board/:boardId/collab",
      name: "collaborator-manage",
      component: CollabBoard,
      meta: { requiresAuth: false, title: "ITBKK - Collaborator Management" },
    },
    { path: "/forbidden", name: "AccessDenied", component: AccessDeniedPage },
    {
      path: "/unauthorized",
      name: "Unauthorized",
      component: UnauthorizedPage,
      meta: { title: "ITBKK - Unauthorized" },
    },    
  ],
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  try {
    if (userStore.token && userStore.isTokenExpired()) {
      await userStore.refreshAccessToken();
    }

    const hasToken = !!userStore.token;
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const boardId = to.params.boardId;

    // ตั้งค่า title ของหน้า (ถ้ามี)
    if (to.meta.title) {
      document.title = to.meta.title;
    }

    if (boardId) {
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        headers: {
          "Content-Type": "application/json",
          ...(hasToken && { Authorization: `Bearer ${userStore.token}` }),
        },
      });

      if (response.ok) {
        const board = await response.json();

        // ถ้าบอร์ดเป็น PUBLIC อนุญาตให้เข้าถึงได้
        if (board.visibility === "PUBLIC") {
          return next();
        }

        // ถ้าบอร์ดเป็น PRIVATE และไม่มี Token ให้ไปหน้า 401 Unauthorized
        if (board.visibility === "PRIVATE" && !hasToken) {
          console.warn("Unauthorized (401). Redirecting to Unauthorized.");
          return next({ name: "login" }); // ไปที่หน้า 401
        }

        return next();
      } else if (response.status === 403) {
        if (!hasToken) {
          // กรณีไม่มี Token แต่อยู่ใน Board Private ให้ไปหน้า 401
          console.warn("Unauthorized (401). Redirecting to Unauthorized.");
          return next({ name: "login" }); // ไปที่หน้า 401
        }
        console.warn("Forbidden (403). Redirecting to AccessDenied.");
        return next({ name: "AccessDenied" }); // กรณีมี Token แต่ไม่มีสิทธิ์
      } else if (response.status === 401) {
        console.warn("Unauthorized (401). Redirecting to Unauthorized.");
        userStore.logout();
        return next({ name: "Unauthorized" }); // ไปที่หน้า 401
      } else if (response.status === 404) {
        return next({ name: "NotFound" });
      }
    }

    if (requiresAuth && !hasToken) {
      return next({ name: "login" });
    }

    if (hasToken && to.name === "login") {
      return next({ name: "board" });
    }

    next();
  } catch (error) {
    if (error.message.includes("Failed to refresh token")) {
      userStore.logout();
      return next({ name: "login" });
    }
    next({ name: "NotFound" });
  }
});




export default router;
