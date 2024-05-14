import { ref, toRaw } from 'vue'
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

export const useStatusStore = defineStore('counterStatus', () => {
  const statuses = ref([])
    
  const updateStatus = (updatedStatus) => {
    console.log(updatedStatus)
    console.log(toRaw(statuses.value.value))
    for (const status of statuses.value) {
      console.log(statuses)
    }
    const index = toRaw(statuses.value.value).findIndex(status => parseInt(status.id) === parseInt(updatedStatus.id))
    if (index !== -1) {
      statuses.value.value[index] = updatedStatus
    } else {
      console.error('Status not found in store')
    }
  }

  return { statuses, updateStatus }
})