import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "../stores/users"; // import user store
import BoardView from "@/views/BoardView.vue";
import LoginView from "../views/LoginView.vue";
import TaskView from "../views/TaskView.vue";
import ModalDetail from "../components/ModalDetail.vue";
import ModalAdd from "../components/ModalAdd.vue";
import ModalEdit from "../components/ModalEdit.vue";
import StatusView from "../views/StatusView.vue";
import ModalAddStatus from "../components/ModalAddStatus.vue";
import ModalEditStatus from "../components/ModalEditStatus.vue";
import ManageStatus from "../components/ManageStatus.vue";
import ModalAddBoard from "@/components/ModalAddBoard.vue"; // นำเข้า Modal สำหรับเพิ่มบอร์ด

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/board", // เปลี่ยนเส้นทางไปที่บอร์ดถ้าผู้ใช้เข้าสู่ระบบแล้ว
    },
    {
      path: "/board",
      name: "board",
      component: BoardView,  // แสดง BoardView เมื่อ path เป็น /board หรือ /board/add
      meta: { requiresAuth: true },  // ต้องตรวจสอบสิทธิ์การเข้าถึง
      children: [
        {
          path: "add", // เส้นทางสำหรับการเพิ่มบอร์ด
          name: "board-add",
          component: ModalAddBoard, // แสดง Modal สำหรับเพิ่มบอร์ด
        },
      ],
    },
    {
      path: "/board/:boardId", // แสดง task ในบอร์ดที่มี id
      name: "board-tasks", 
      component: TaskView, // ใช้ TaskView เมื่อเข้าสู่บอร์ดด้วย boardId
      meta: { requiresAuth: true },
      children: [
        {
          path: "task/:taskId", // แสดงรายละเอียดของ task
          name: "task-detail",
          component: ModalDetail,
        },
        {
          path: "task/add", // เพิ่ม task ใหม่
          name: "task-add",
          component: ModalAdd,
        },
        {
          path: "task/:taskId/edit", // แก้ไข task
          name: "task-edit",
          component: ModalEdit,
        },
        {
          path: "status", // แสดงสถานะของ task
          name: "status",
          component: StatusView,
          children: [
            {
              path: "manage", // จัดการสถานะของงาน
              name: "status-manage",
              component: ManageStatus,
            },
            {
              path: "add", // เพิ่มสถานะใหม่
              name: "status-add",
              component: ModalAddStatus,
            },
            {
              path: ":statusId/edit", // แก้ไขสถานะ
              name: "status-edit",
              component: ModalEditStatus,
            },
          ],
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

// การตรวจสอบสิทธิ์การเข้าถึงก่อนการนำทาง
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  // ถ้าไม่มี token และพยายามเข้าถึงหน้าอื่นที่ไม่ใช่หน้า login ให้ไปที่หน้า login
  if (!userStore.token && to.meta.requiresAuth) {
    return next({ name: 'login' });
  }

  // ถ้ามี token และพยายามเข้าถึงหน้า login ให้ redirect ไปที่หน้า board
  if (userStore.token && to.name === 'login') {
    return next({ name: 'board' });
  }

  // ตรวจสอบ token ว่ายังใช้งานได้หรือไม่
  if (userStore.token) {
    try {
      await userStore.checkAuth(); // ตรวจสอบ token
    } catch (error) {
      userStore.logout(); // ถ้า token หมดอายุหรือไม่ถูกต้อง ให้ logout ผู้ใช้
      return next({ name: 'login' });
    }
  }

  next(); // ถ้าผ่านการตรวจสอบทั้งหมด
});

export default router;
