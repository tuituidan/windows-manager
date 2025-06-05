<template>
  <div class="navbar">
    <el-menu
      :default-active="activeMenu"
      mode="horizontal"
      background-color="#545c64"
      text-color="#fff"
      router
      active-text-color="#ffd04b">
      <el-menu-item index="/">IIS管理</el-menu-item>
      <el-menu-item index="/win-service">服务管理</el-menu-item>
      <el-menu-item index="/setting">系统设置</el-menu-item>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'Navbar',
  data(){
    return {}
  },
  computed: {
    activeMenu() {
      return this.$route.path;
    },
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 61px;
  overflow: hidden;
  position: relative;
  background: #545c64;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  .el-menu {
    margin: 0 200px;
  }
}
</style>
