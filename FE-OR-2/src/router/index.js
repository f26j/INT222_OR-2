import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Modal from '../views/ModalDetail.vue'
<<<<<<< Updated upstream
import ModalAdd from '../views/ModalAdd.vue'
=======
import ModalA from '../views/ModalAdd.vue'
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
          path: "/task/add",
          name: "task-add",
          component: ModalAdd,
=======
          path: "/task/:id",
          name: "task-add",
          component: ModalA,
>>>>>>> Stashed changes
        },
      ],
    }
  ]
})

export default router
