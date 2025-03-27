<template>
  <div class="app-wrapper">
    <div class="fixed-header">
      <navbar/>
    </div>
    <app-main/>
  </div>
</template>

<script>
import { AppMain, Navbar } from './components'
export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
  },
}
</script>

<style lang="scss" scoped>
  @import "~@/assets/styles/mixin.scss";
  @import "~@/assets/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: 100%;
    transition: width 0.28s;
  }
</style>
