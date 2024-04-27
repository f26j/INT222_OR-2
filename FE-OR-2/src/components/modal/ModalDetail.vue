<script>
export default {
  data() {
    return {
      tasks: [
        /* ... */
      ],
      selectedTaskTitle: "",
      showModal: false,
    };
  },
  methods: {
    openModal(taskTitle) {
      this.selectedTaskTitle = taskTitle;
      this.showModal = true;
    },
  },
};
</script>
<template>
  <div class="absolute inset-0 overflow-y-auto bg-black bg-opacity-50">
    <div class="flex items-start justify-center min-h-screen mt-24 text-center">
        <div class="bg-white text-black rounded-lg text-center shadow-xlp-6 w-64" role="dialog" aria-modal="true">
            <div class="modal-content">
                <div class="modal-header">
                    <div class="itbkk-title text-left"><h1>Title</h1></div>
                    <hr />
                </div>
                <div class="modal-body">
                    <div class="itbkk-description text-left">
                        <div class="flex flex-wrap -mx-3 mb-2">
                            <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                                    for="grid-city">
                                    Description
                                </label>
                                <input class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                                    id="description" type="text" placeholder="Albuquerque" />
                            </div>
                        </div>
                    </div>
                    <div class="itbkk-assignees text-right">
                        <div class="flex flex-wrap -mx-3 mb-2">
                            <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                                <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                                    for="grid-city">
                                    Assignees
                                </label>
                                <input class="appearance-none block w-full bg-gray-200 text-gray-700 border border-gray-200 rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                                    id="grid-city" type="text" placeholder="Assignees" />
                            </div>
                        </div>
                    </div>
                    <div class="itbkk-status text-right">
                        <div class="w-full md:w-1/3 px-3 mb-6 md:mb-0">
                            <label class="block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
                                for="grid-state">
                                Status
                            </label>
                            <div class="relative">
                                <select class="block appearance-none w-full bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
                                    id="grid-state">
                                    <option>No Status</option>
                                    <option>To Do</option>
                                    <option>Doing</option>
                                    <option>Done</option>
                                </select>
                                <div
                                    class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                                    <svg class="fill-current h-4 w-4"
                                        xmlns="http://www.w3.org/2000/svg"
                                        viewBox="0 0 20 20">
                                        <path
                                            d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z" />
                                    </svg>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="itbkk-timezone text-right"><p>Timezone</p></div>
                    <div class="itbkk-created-on text-right"><p>CreatedOn</p></div>
                    <div class="itbkk-updated-on text-right"><p>UpdatedOn</p></div>
                </div>
                <div class="modal-footer">
                    <div class="itbkk-button">
                        <hr />
                        <button @click="($event) => $emit('close')"
                            class="border border-black bg-white text-black mr-4">
                            OK
                        </button>
                        <button @click="($event) => $emit('close')"
                            class="border border-black bg-white text-black mr-4">
                            Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</template>
<style></style>
