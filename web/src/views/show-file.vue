<template>
  <pre v-html="highlightedCode">
  </pre>
</template>

<script>
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';

export default {
  name: "show-file",
  data() {
    return {
      code: '',
      queryParam: {
        position: 0,
        size: 1000,
        hasMore: true,
      },
      loading: false,
      observer: null,
    }
  },
  computed: {
    highlightedCode() {
      const result = hljs.highlightAuto(this.code);
      console.log(result.language)
      return result.value + (this.queryParam.hasMore ?
        '<div class="loading-container"><i class="el-icon-loading"></i></div>' : '');
    },
  },
  mounted() {
    this.observer = new IntersectionObserver(entries => {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          this.loadContent();
        }
      }
    }, {threshold: 0.5})
    this.observeLoading();
  },
  methods: {
    observeLoading(){
      this.observer.observe(document.querySelector('.el-icon-loading'));
    },
    loadContent() {
      if (this.loading || !this.queryParam.hasMore) {
        return;
      }
      this.loading = true;
      this.observer.disconnect();
      const pos = this.queryParam.position;
      const size = this.queryParam.size;
      const path = this.$route.params.path;
      this.$http.get(`/api/v1/file/${pos}/${size}/action/page?path=${encodeURIComponent(path)}`)
        .then(res => {
          // 有些json会自动转对象
          if (typeof res.data === 'string') {
            this.code += res.data;
          } else {
            this.code += JSON.stringify(res.data);
          }
          this.queryParam.position = res.position;
          this.queryParam.hasMore = res.hasMore;
          this.$nextTick(() => {
            this.observeLoading();
          });
        })
        .catch(err => {
          console.error(err);
          this.$modal.msgError('发生错误');
        })
        .finally(() => {
          this.loading = false;
        })

    },
  }
}
</script>
<style scoped lang="scss">
::v-deep .loading-container {
  text-align: center;

  .el-icon-loading {
    font-size: 26px;
  }
}

</style>
