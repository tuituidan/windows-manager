<template>
  <div class="app-container">
    <div class="search-panel">
      <el-input suffix-icon="el-icon-search"
                placeholder="输入服务名称进行搜索..."
                clearable
                size="medium"
                v-model.trim="keywords"
                @input="searchHandler">
      </el-input>
      <el-button type="success" icon="el-icon-plus">新增</el-button>
    </div>
    <el-table stripe
              border
              size="medium"
              :data="searchDatas">
      <el-table-column label="序号" align="center" type="index" width="55"></el-table-column>
      <el-table-column prop="Name" label="服务名称"></el-table-column>
      <el-table-column prop="DisplayName" label="显示名称" show-overflow-tooltip></el-table-column>
      <el-table-column prop="Description" label="描述" show-overflow-tooltip></el-table-column>
      <el-table-column prop="PathName" label="执行文件路径" show-overflow-tooltip></el-table-column>
      <el-table-column prop="StartMode" label="启动类型" show-overflow-tooltip></el-table-column>
      <el-table-column prop="State" align="center" label="服务状态" width="100">
        <template slot-scope="scope">
          <el-tag type="info" size="small" v-if="scope.row.State==='Stopped'">已停止</el-tag>
          <el-tag type="success" effect="dark" size="small" v-else>已启动</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button type="text" size="small"
                     v-if="scope.row.State==='Stopped'"
                     @click="serviceState(scope.row.id, 'start')">启动网站</el-button>
          <el-button type="text" size="small"
                     v-else @click="serviceState(scope.row.id, 'stop')">停止网站</el-button>
          <el-button type="text" size="small"
                     @click="showFile(scope.row)">管理</el-button>
        </template>
      </el-table-column>
    </el-table>
    <show-file-dialog ref="showFileDialog"></show-file-dialog>
  </div>
</template>

<script>

export default {
  name: "index",
  components: {
    'show-file-dialog': () => import('@/views/site/show-file-dialog'),
  },
  data(){
    return {
      keywords: '',
      searchDatas: [],
      datas: [],
    }
  },
  mounted() {
    //this.init();
  },
  methods: {
    init() {
      this.$http.get('/api/v1/site')
        .then(res => {
          this.searchDatas = this.datas = res;
          this.searchHandler();
        })
        .catch(err => {
          console.error(err.response);
          this.$modal.msgError(err.response.data.message);
        })
    },
    searchHandler() {
      if (!this.keywords) {
        this.searchDatas = this.datas;
        return;
      }
      this.searchDatas = this.datas.filter(item => item.siteName.toLocaleLowerCase()
        .includes(this.keywords.toLocaleLowerCase()))
    },
    serviceState(id, state) {
      this.$http.patch(`/api/v1/win-service/${id}/actions/${state}`)
        .then(() => {
          this.$modal.msgSuccess('执行成功');
          this.init();
        })
        .catch(err => {
          console.error(err.response);
          this.$modal.msgError(err.response.data.message);
        })
    },
    showFile(row) {
      this.$refs.showFileDialog.open(row);
    },
  }
}
</script>

<style scoped lang="scss">
.search-panel {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  .el-input {
    width: 40%;
  }
}
</style>
