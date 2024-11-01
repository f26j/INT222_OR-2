<script setup>
import { computed, defineProps } from "vue";
const props = defineProps({
  status: Object,
});
const className = computed(() => {
  switch (props.status.name) {
    case "No Status":
      return "status-No";
    case "To Do":
      return "status-To";
    case "Doing":
      return "status-Doing";
    case "Done":
      return "status-Done";
    default:
      return "status-New";
  }
});
</script>
<template>
  <div :class="{ 'status-badge': true, [className]: status }">
    <span v-if="status.name === 'No Status'" class="a flex-align-items">
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
      <p>No Status</p>
    </span>

    <span v-else-if="status.name === 'To Do'" class="a flex-align-items">
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
      <p>To Do</p>
    </span>

    <span v-else-if="status.name === 'Doing'" class="a flex-align-items">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="1em"
        height="1em"
        viewBox="0 0 16 16"
      >
        <path
          fill="currentColor"
          d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8.575-3.25a.825.825 0 1 0-1.65 0v3.5c0 .337.205.64.519.766l2.5 1a.825.825 0 0 0 .612-1.532l-1.981-.793Z"
        ></path>
      </svg>
      <p>Doing</p>
    </span>

    <span v-else-if="status.name === 'Done'" class="a flex-align-items">
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
      <p>Done</p>
    </span>

    <!-- กรณีที่ชื่อสถานะไม่ตรงกับเงื่อนไขที่กำหนด -->
    <span v-else class="a flex-align-items">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="1em"
        height="1em"
        viewBox="0 0 20 20"
      >
        <path
          fill="currentColor"
          d="m9.89 17.514l-4.21 2.257l-.099.044c-.715.27-1.39.216-1.903-.242c-.482-.43-.657-1.046-.557-1.755l.704-4.86l-3.18-3.342c-.55-.56-.765-1.248-.58-1.968c.205-.799.88-1.258 1.851-1.412l4.227-.638l2.213-4.585C8.7.366 9.236-.017 9.911.001c.66.017 1.183.422 1.593 1.143l2.14 4.486l4.74.658c.753.13 1.308.522 1.53 1.176c.22.653.01 1.313-.557 1.987l-3.44 3.51l.772 4.856c.122.84-.025 1.505-.586 1.9c-.506.357-1.139.357-1.867.107l-.12-.053z"
        ></path>
      </svg>
      <p>{{ status.name || "Unknown Status" }}</p>
    </span>
  </div>
</template>
<style>
.a p {
  width: 67px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

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

.status-New {
  color: #4574d3;
  background-color: #e6f3ff;
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
