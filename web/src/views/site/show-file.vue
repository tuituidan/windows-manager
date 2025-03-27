<template>
  <div>
    <pre v-text="content"></pre>
  </div>
</template>

<script>
export default {
  name: "show-file",
  data(){
    return {
      content: ''
    }
  },
  mounted() {
    this.$http.get(`/api/v1/file/action/show?path=${encodeURIComponent(this.$route.params.path)}`)
      .then(res => {
        this.content = res;
      })
      .catch(err => {
        console.error(err);
        this.$modal.msgError('发生错误');
      })
  },
}
</script>

<style scoped>

</style>
