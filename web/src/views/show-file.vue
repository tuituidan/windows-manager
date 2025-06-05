<template>
  <pre v-html="highlightedCode"></pre>
</template>

<script>
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';

export default {
  name: "show-file",
  data() {
    return {
      code: '',
    }
  },
  computed: {
    highlightedCode() {
      const result = hljs.highlightAuto(this.code);
      console.log(result.language)
      return result.value;
    },
  },
  mounted() {
    this.loadContent();
  },
  methods: {
    loadContent() {
      this.$http.get(`/api/v1/file/action/show?path=${encodeURIComponent(this.$route.params.path)}`)
        .then(res => {
          this.code = res;
        })
        .catch(err => {
          console.error(err);
          this.$modal.msgError('发生错误');
        })
    },
  }
}
</script>
