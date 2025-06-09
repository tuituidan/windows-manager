<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>

export default {
  name: "App",
  metaInfo() {
    return {
      title: this.$route?.meta?.title || 'Windows服务管理',
    }
  }
};
</script>
