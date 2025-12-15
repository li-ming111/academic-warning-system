<template>
  <div class="admin-layout">
    <router-view></router-view>
  </div>
</template>

<script setup>
// AdminLayout 现在只用于路由嵌套，导航由 App.vue 统一管理
</script>

<style scoped>
.admin-layout {
  width: 100%;
  height: 100%;
}
</style>
