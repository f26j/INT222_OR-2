import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "../stores/AuthUserStore";

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

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/login", name: "login", component: LoginView },
    { path: "/", redirect: "/board" },
    {
      path: "/board",
      name: "board",
      component: BoardView,
      meta: { requiresAuth: true },
      children: [{ path: "add", name: "board-add", component: ModalAddBoard }],
    },
    { path: "/:pathMatch(.*)*", name: "NotFound", component: NotFoundPage },
    {
      path: "/board/:boardId",
      name: "board-tasks",
      component: TaskView,
      meta: { requiresAuth: false },
      children: [
        { path: "task/add", name: "task-add", component: ModalAddTask },
        {
          path: "task/:taskId",
          name: "task-detail",
          component: ModalDetailTask,
        },
        {
          path: "task/:taskId/edit",
          name: "task-edit",
          component: ModalEditTask,
        },
      ],
    },
    {
      path: "/board/:boardId/status",
      component: StatusView,
      meta: { requiresAuth: false },
      children: [
        { path: "manage", name: "status-manage", component: ManageStatus },
        { path: "add", name: "status-add", component: ModalAddStatus },
        { path: ":statusId/edit", name: "status-edit", component: ModalEditStatus },
      ],
    },
    { path: "/forbidden", name: "AccessDenied", component: AccessDeniedPage },
  ],
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  await userStore.refreshToken(); // Ensure token is refreshed
  const hasToken = !!userStore.token;
  const requiresAuth = to.meta.requiresAuth;
  const boardId = to.params.boardId;

  try {
    if (to.path.startsWith("/board/") && boardId) {
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        headers: {
          "Content-Type": "application/json",
          ...(hasToken && { Authorization: `Bearer ${userStore.token}` }),
        },
      });

      if (response.ok) {
        const board = await response.json();
        if (board.visibility === "PRIVATE" && !hasToken) {
          console.warn("Access denied. Redirecting to login.");
          return next({ name: "login" });
        }
      } else if (response.status === 403) {
        console.warn("Access denied (403). Redirecting to AccessDenied.");
        return next({ name: "AccessDenied" });
      } else if (response.status === 404) {
        console.warn("Board not found. Redirecting to NotFound.");
        return next({ name: "NotFound" });
      }
    }

    if (requiresAuth && !hasToken) {
      console.warn("Authentication required. Redirecting to login.");
      return next({ name: "login" });
    }

    if (hasToken && to.name === "login") {
      return next({ name: "board" });
    }

    next();
  } catch (error) {
    console.error("Error during navigation:", error);
    next({ name: "NotFound" });
  }
});

export default router;
