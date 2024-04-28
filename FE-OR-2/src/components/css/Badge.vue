<script setup>
import { computed,defineProps } from 'vue';

const props = defineProps({
  status : String
}) 

const className = computed(() => { 
  switch (props.status) {
        case "To Do":
          return "status-To";
        case "Done":
          return "status-Done";
        case "Doing":
          return "status-Doing";
        case "No Status":
          return "status-No";
        default:
          return "";
      }
});
</script>
<template>
  <div :class="{ 'status-badge': true, [className]: status }">
    <span v-if="status === 'To Do'" class="flex-align-items">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="1em"
        height="1em"
        viewBox="0 0 12 12"
      >
        <path
          fill="currentColor"
          d="M4.855.708c.5-.896 1.79-.896 2.29 0l4.675 8.351a1.312 1.312 0 0 1-1.146 1.954H1.33A1.313 1.313 0 0 1 .183 9.058ZM7 7V3H5v4Zm-1 3a1 1 0 1 0 0-2a1 1 0 0 0 0 2"
        ></path>
      </svg>
      To Do
    </span>
    <span v-if="status === 'Done'" class="flex-align-items">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="1em"
        height="1em"
        viewBox="0 0 16 16"
      >
        <path
          fill="currentColor"
          d="M8 16A8 8 0 1 1 8 0a8 8 0 0 1 0 16m3.78-9.72a.751.751 0 0 0-.018-1.042a.751.751 0 0 0-1.042-.018L6.75 9.19L5.28 7.72a.751.751 0 0 0-1.042.018a.751.751 0 0 0-.018 1.042l2 2a.75.75 0 0 0 1.06 0Z"
        ></path>
      </svg>
      Done
    </span>
    <span v-if="status === 'Doing'" class="flex-align-items">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="1em"
        height="1em"
        viewBox="0 0 16 16"
      >
        <path
          fill="currentColor"
          d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8.575-3.25a.825.825 0 1 0-1.65 0v3.5c0 .337.205.64.519.766l2.5 1a.825.825 0 0 0 .612-1.532l-1.981-.793Z"
        >Doing</path>
      </svg>
      Doing
    </span>
    <span v-if="status === 'No Status'" class="flex-align-items">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="1em"
        height="1em"
        viewBox="0 0 2048 2048"
      >
        <path
          fill="currentColor"
          d="M960 0q132 0 255 34t229 97t194 150t150 194t97 230t35 255q0 133-34 255t-97 229t-150 194t-194 150t-230 97t-255 35q-133 0-255-34t-229-97t-194-150t-150-194t-97-230T0 960q0-132 34-255t97-229t150-194t194-150t230-97T960 0m576 1024V896H384v128z"
        ></path>
      </svg>
      No Status
    </span>
  </div>
</template>
<style>
.status-badge {
  text-align: center;
  align-items: center;
  justify-content: space-between;
  padding: 4px 8px;
  border-radius: 50px;
  color: white;
  font-size: 14px;
  font-weight: bold;
  width: 6rem;
  height: 24px;
}

.status-No {
  color: #666b74;
  background-color: #f1f5f9;
  animation: neon-white 1.5s ease-in-out infinite alternate;
}

.status-To {
  color: #a9312e;
  background-color: #ffe6e8;
  animation: neon-white 1.5s ease-in-out infinite alternate;
}

.status-Doing {
  color: #a45312;
  background-color: #fff4d1;
  animation: neon-white 1.5s ease-in-out infinite alternate;
}

.status-Done {
  color: #00725b;
  background-color: #d9fbea;
  animation: neon-white 1.5s ease-in-out infinite alternate;
}

.status-badge svg {
  margin-right: 8px; /* Add spacing between icon and text */
}

.flex-align-items {
  display: flex; 
  align-items: center; 
  justify-content: center;
}

@keyframes neon-white {
  from {
    box-shadow: 0 0 5px rgba(255, 255, 255, 0.5);
  }
  to {
    box-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
  }
}

@keyframes neon-red {
  from {
    box-shadow: 0 0 5px rgba(255, 0, 0, 0.5);
  }
  to {
    box-shadow: 0 0 10px rgba(255, 0, 0, 0.8);
  }
}

@keyframes neon-orange {
  from {
    box-shadow: 0 0 5px rgba(255, 165, 0, 0.5);
  }
  to {
    box-shadow: 0 0 10px rgba(255, 165, 0, 0.8);
  }
}
@keyframes neon-yellow {
  from {
    box-shadow: 0 0 5px rgba(255, 255, 0, 0.5);
  }
  to {
    box-shadow: 0 0 10px rgba(255, 255, 0, 0.8);
  }
}

@keyframes neon-light-green {
  from {
    box-shadow: 0 0 5px rgba(173, 255, 47, 0.5);
  }
  to {
    box-shadow: 0 0 10px rgba(173, 255, 47, 0.8);
  }
}
</style>
