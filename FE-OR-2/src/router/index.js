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
      children: [{ path: "add", name: "board-add", component: ModalAddBoard }],
    },
    { path: "/:pathMatch(.*)*", name: "NotFound", component: NotFoundPage },
    {
      path: "/board/:boardId",
      name: "board-tasks",
      component: TaskView,
      meta: { requiresAuth: false, title: "ITBKK - Task" },
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
    { path: "/forbidden", name: "AccessDenied", component: AccessDeniedPage },
  ],
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  await userStore.refreshToken();

  const hasToken = !!userStore.token;
  const requiresAuth = to.meta.requiresAuth;
  const boardId = to.params.boardId;

  if (to.meta.title) {
    document.title = to.meta.title;
  }

  console.log(`Navigating to: ${to.path}`);
  console.log(`Token status: ${hasToken ? "Has Token" : "No Token"}`);
  console.log(`Requires Auth: ${requiresAuth}`);
  console.log(`Board ID: ${boardId}`);

  try {
    if (boardId) {
      const response = await fetch(`${baseUrl}/v3/boards/${boardId}`, {
        headers: {
          "Content-Type": "application/json",
          ...(hasToken && { Authorization: `Bearer ${userStore.token}` }),
        },
      });

      if (response.ok) {
        const board = await response.json();
        console.log("Board data:", board);

        // เงื่อนไขนี้เป็นจุดที่อาจพาไป /login
        if (board.visibility === "PRIVATE" && !hasToken) {
          console.warn("Access denied. Redirecting to login.");
          return next({ name: "login" });
        }

        // ข้ามการตรวจสอบหากเป็น public board
        return next(); // <<< นี่คือส่วนที่ควรอนุญาตให้ผ่าน
      } else if (response.status === 403) {
        console.warn("Access Denied (403). Redirecting to AccessDenied.");
        return next({ name: "AccessDenied" });
      } else if (response.status === 404) {
        console.warn("Board not found. Redirecting to NotFound.");
        return next({ name: "NotFound" });
      }
    }

    // ตรวจสอบว่า Route ต้องการ Auth แต่ไม่มี Token
    if (requiresAuth && !hasToken) {
      console.warn("Authentication required. Redirecting to login.");
      return next({ name: "login" });
    }

    // ถ้าล็อกอินแล้วไม่ให้เข้าหน้า login อีก
    if (hasToken && to.name === "login") {
      console.log("Already logged in. Redirecting to /board.");
      return next({ name: "board" });
    }

    console.log("Navigation allowed.");
    next(); // อนุญาตให้ Navigate ได้ตามปกติ
  } catch (error) {
    console.error("Error during navigation:", error);
    next({ name: "NotFound" });
  }
});

export default router;
