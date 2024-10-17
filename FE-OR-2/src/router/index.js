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
    const isBoardPath = to.path.startsWith("/board/");
  
    // กรณีที่ไม่มี Token และต้องการเข้าถึง /board/:boardId
    if (!userStore.token && isBoardPath && to.params.boardId) {
      try {
        const boardId = to.params.boardId;
        const response = await fetch(`${baseUrl}/v3/boards/${boardId}`);
        
        if (response.ok) {
          const board = await response.json();
  
          // ตรวจสอบว่าเป็น public หรือไม่
          if (board.visibility === "PUBLIC") {
            return next(); // อนุญาตให้เข้าถึง
          } else {
            console.warn("Board is private. Redirecting to login.");
            return next({ name: "login" }); // หากไม่ใช่ public ให้ไปที่ login
          }
        } else {
          console.warn("Board not found. Redirecting to NotFound.");
          return next({ name: "NotFound" }); // ไม่พบบอร์ด
        }
      } catch (error) {
        console.error("Error fetching board visibility:", error);
        return next({ name: "NotFound" });
      }
    }
  
    // ตรวจสอบ Token หากเส้นทางต้องการ Auth แต่ไม่มี Token
    if (requiresAuth && !userStore.token) {
      return next({ name: "login" });
    }
  
    // หากมี Token และพยายามเข้า /login ให้ redirect ไป /board
    if (userStore.token && to.name === "login") {
      return next({ name: "board" });
    }
  
    // ตรวจสอบ Token ถ้ามี แต่เกิดข้อผิดพลาด (เช่น Token หมดอายุ)
    if (userStore.token) {
      try {
        await userStore.checkAuth();
      } catch (error) {
        userStore.logout();
        return next({ name: "login" });
      }
    }
  
    next(); // อนุญาตให้เข้าถึงเส้นทาง
  });
  
  
  
  export default router;
