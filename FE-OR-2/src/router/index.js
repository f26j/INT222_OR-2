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
      path: "/board/:boardId",
      name: "board-tasks",
      component: TaskView,
      meta: { requiresAuth: false, title: "ITBKK - Task" },
      children: [
        { path: "task/add", name: "task-add", component: ModalAddTask, meta: { requiresAuth: true} },
        {
          path: "task/:taskId",
          name: "task-detail",
          component: ModalDetailTask,
        },
        {
          path: "task/:taskId/edit",
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
        { path: "add", name: "status-add", component: ModalAddStatus },
        {
          path: ":statusId/edit",
          name: "status-edit",
          component: ModalEditStatus,
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
        if (board.visibility === "PRIVATE" && !hasToken) {
          return next({ name: "login" });
        }
        return next();
      } else if (response.status === 401) {
        console.warn("Unauthorized (401). Redirecting to login.");
        userStore.logout();
        return next({ name: "login" });
      } else if (response.status === 403) {
        return next({ name: "AccessDenied" });
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
