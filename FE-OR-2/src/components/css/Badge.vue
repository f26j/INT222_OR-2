<script>
export default {
  props: {
    status: String,
  },
};
</script>
<template>
  <div :class="{ 'status-badge': true, 'status-No': status === 'No Status', 'status-To': status === 'To Do', 'status-Doing': status === 'Doing', 'status-Done': status === 'Done' }">
    {{ status }}
  </div>
</template>

<style>
.status-badge {
  text-align: center;
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
  font-weight: bold;
  width: 5rem;
  height: 20px;
}

.status-No {
  background-color: #e15b64;
}

.status-To {
  background-color: #f27f62;
}

.status-Doing {
  background-color: #f2e35e;
}

.status-Done {
  background-color: #95ca9f;
}
</style>
