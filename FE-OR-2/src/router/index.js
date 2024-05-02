import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Modal from '../views/ModalDetail.vue'
import ModalAdd from '../views/ModalAdd.vue'

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
          path: "/task/:id",
          name: "task-detail",
          component: Modal,
        },
      ],
    },
    {
      path: "/task",
      name: "task",
      component: HomeView,
      children: [
        {
          path: "/task/add",
          name: "task-add",
          component: ModalAdd,
        },
      ],
    }
  ]
})

export default router
