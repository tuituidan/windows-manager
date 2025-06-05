<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="140px" @submit.native.prevent>
      <el-form-item label="本地IP" prop="localIp">
        <span slot="label">
          <el-tooltip content='IIS网站未配置IP时，会自动获取当前服务器IP，特殊情况下获取的不是实际需要的时候可在这里配置，一般不需要配置'>
          <i class="el-icon-question"></i>
          </el-tooltip>
          本地IP
        </span>
        <el-input v-model="form.localIp"></el-input>
      </el-form-item>
      <el-divider content-position="left">配置可在线预览的文件</el-divider>
      <el-form-item label="图片预览" prop="fileExtImg">
        <span slot="label">
          <el-tooltip content='配置文件名正则表达式来确定哪些文件通过图片形式在线查看，多个逗号隔开'>
          <i class="el-icon-question"></i>
          </el-tooltip>
          图片预览
        </span>
        <el-input type="textarea" v-model="form.fileExtImg"></el-input>
      </el-form-item>
      <el-form-item label="文本预览" prop="fileExtTxt">
        <span slot="label">
          <el-tooltip content='配置文件名正则表达式来确定哪些文件通过文本形式在线查看，多个逗号隔开，如【*.txt,edit-*】'>
          <i class="el-icon-question"></i>
          </el-tooltip>
          文本预览
        </span>
        <el-input type="textarea" v-model="form.fileExtTxt"></el-input>
      </el-form-item>
      <el-form-item label="浏览器预览" prop="fileExtBrowser">
        <span slot="label">
          <el-tooltip content='配置文件名正则表达式来确定哪些文件通过浏览器预览，注意必须是浏览器原生支持预览的文件类型，否则就是下载，多个逗号隔开，如【*.pdf,*.mp4】'>
          <i class="el-icon-question"></i>
          </el-tooltip>
          浏览器预览
        </span>
        <el-input type="textarea" v-model="form.fileExtBrowser"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitHandler">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {camelToKebab, kebabToCamel} from '@/utils'

export default {
  name: "index",
  data() {
    return {
      loading: false,
      form: {
        localIp: '',
        fileExtImg: '',
        fileExtTxt: '',
        fileExtBrowser: '',
      },
      // 表单校验
      rules: {
        localIp: [
          {
            required: false,
            pattern: /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
            message: '请输入正确的IP地址',
            trigger: 'blur'
          }
        ],
      },
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.$http.get('/api/v1/setting')
        .then(res => {
          this.form = kebabToCamel(res);
        })
        .catch(err => {
          console.error(err.response);
          this.$modal.msgError(err.response.data.message);
        })
    },
    submitHandler() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http.post('/api/v1/setting', camelToKebab(this.form))
            .then(() => {
              this.$modal.msgSuccess('保存成功');
            })
            .catch(err => {
              console.error(err.response);
              this.$modal.msgError(err.response.data.message);
            })
        }
      });
    }
  }
}
</script>
