<script>
export default {
  props: {
    status: String,
  },
};
</script>
<template>
  <div
    :class="{
      'status-badge': true,
      'status-No': status === 'No Status',
      'status-To': status === 'To Do',
      'status-Doing': status === 'Doing',
      'status-Done': status === 'Done',
    }"
  >
    {{ status }}
  </div>
</template>

<style>
.status-badge {
  text-align: center;
  display: inline-block;
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
