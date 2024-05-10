import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ModalDetail from '../views/ModalDetail.vue'
import ModalAdd from '../views/ModalAdd.vue'
import ModalEdit from '../views/ModalEdit.vue'
import StatusView from '@/views/StatusView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/task",
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
      path: "/statuses",
      name: "statusView",
      component: StatusView,
    },
  ]
})

export default router
