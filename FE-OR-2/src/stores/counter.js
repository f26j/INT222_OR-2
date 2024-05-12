// import { ref, computed } from 'vue'
// import { defineStore } from 'pinia'

// export const useCounterStore = defineStore('counter', () => {
//   const tasks = ref([])

//   return { tasks }
// })
import { ref, computed, toRaw } from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', () => {
  const tasks = ref([])
    
  const updateTask = (updatedTask) => {
    console.log(updatedTask)
    console.log(toRaw(tasks.value.value))
    for (const task of tasks.value) {
      console.log(tasks)
    }
    const index = toRaw(tasks.value.value).findIndex(task => parseInt(task.id) === parseInt(updatedTask.id))
    if (index !== -1) {
      tasks.value.value[index] = updatedTask
    } else {
      console.error('Task not found in store')
    }
  }

  return { tasks, updateTask }
})