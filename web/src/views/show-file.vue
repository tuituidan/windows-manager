<template>
  <div>
    <codemirror ref="codemirror" v-model="content" :style="codemirrorStyle" :options="cmOptions"></codemirror>
  </div>
</template>

<script>
import {codemirror} from 'vue-codemirror';
import 'codemirror/lib/codemirror.css'
import 'codemirror/addon/lint/lint'
import 'codemirror/addon/lint/lint.css'
import 'codemirror/addon/lint/json-lint'
import 'codemirror/addon/lint/javascript-lint'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/theme/base16-dark.css'
import 'codemirror/addon/fold/foldgutter.css'
import 'codemirror/addon/fold/foldcode'
import 'codemirror/addon/fold/foldgutter'
import 'codemirror/addon/fold/brace-fold'
import 'codemirror/addon/fold/comment-fold'
import 'codemirror/addon/fold/markdown-fold'
import 'codemirror/addon/fold/xml-fold'
import 'codemirror/addon/fold/indent-fold'
import 'codemirror/addon/hint/show-hint.css'
import 'codemirror/addon/hint/show-hint.js'
import 'codemirror/addon/hint/javascript-hint'
import 'codemirror/addon/hint/xml-hint'
import 'codemirror/addon/hint/sql-hint'
import 'codemirror/addon/hint/anyword-hint'
import 'codemirror/addon/search/match-highlighter'
import 'codemirror/addon/edit/matchbrackets'
import 'codemirror/addon/edit/closebrackets'
import 'codemirror/mode/css/css.js'
import 'codemirror/mode/vue/vue.js'
import {autoGetMode} from "@/utils/mode-mappings";

export default {
  name: "show-file",
  components: {
    codemirror
  },
  data() {
    return {
      content: '',
      cmOptions: {
        // 是否可编辑，设特殊值【nocursor】则无光标
        readOnly: true,
        mode: 'text/plain',
        gutters: ['CodeMirror-lint-markers', 'CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
        lineNumbers: true,
        line: true,
        lint: true,
        lineWrapping: true,
        autofocus: true,
        autoCloseBrackets: true,
        foldGutter: true, // 块槽
        hintOptions: {completeSingle: true},
        matchTags: {bothTags: true},
        matchBrackets: true,
        showCursorWhenSelecting: true,
        styleSelectedText: true,
        styleActiveLine: true,
        autoRefresh: true,
        highlightSelectionMatches: {
          minChars: 2,
          trim: true,
          style: "matchhighlight",
          showToken: false
        },
      },
      codemirrorStyle: {
        fontSize: '18px',
        lineHeight: '150%',
        border: '1px solid #EBEEF5'
      },
      clientHeight: 0,
      editor: null,
    }
  },
  mounted() {
    //设置codemirror高度，这种方法可以区域始终和浏览器窗口同步
    // this.setCodeHeight();
    console.log(this.$refs.codemirror)
    this.loadContent();
  },
  methods: {
    setCodeHeight() {
      this.clientHeight = `${document.documentElement.clientHeight}`;
      this.editor = this.$refs.codemirror.codemirror;
      const fixedHeight = 2;
      // // 设置codemirror高度
      this.editor.setSize('auto', this.clientHeight - fixedHeight);
      window.onresize = () => {
        this.clientHeight = `${document.documentElement.clientHeight}`;
        this.editor.setSize('auto', this.clientHeight - fixedHeight);
      }
    },
    loadContent() {
      this.$http.get(`/api/v1/file/action/show?path=${encodeURIComponent(this.$route.params.path)}`)
        .then(res => {
          this.content = res;
          const mode = autoGetMode(res);
          console.log('mode', mode)
          //this.$refs.codemirror.codemirror.setOption('mode', mode);
          if (this.cmOptions.mode !== mode) {
            this.cmOptions.mode = mode
          }
        })
        .catch(err => {
          console.error(err);
          this.$modal.msgError('发生错误');
        })
    },
  }
}
</script>

<style scoped lang="scss">
::v-deep .CodeMirror {
  height: auto !important;
}
</style>
