<template>
  <div class="app-container">
    <div class="search-panel">
      <el-input suffix-icon="el-icon-search"
                placeholder="输入服务名称或描述进行搜索..."
                clearable
                size="medium"
                v-model.trim="keywords"
                @input="searchHandler">
      </el-input>
      <el-button type="primary" icon="el-icon-plus" @click="openAddDialog">添加/移除服务</el-button>
    </div>
    <el-table stripe
              border
              size="medium"
              v-loading="loading"
              :data="searchDatas">
      <el-table-column label="序号" align="center" type="index" width="55"></el-table-column>
      <el-table-column prop="Name" label="服务名称" show-overflow-tooltip></el-table-column>
      <el-table-column prop="DisplayName" label="显示名称" show-overflow-tooltip></el-table-column>
      <el-table-column prop="Description" label="描述" show-overflow-tooltip></el-table-column>
      <el-table-column prop="PathName" label="执行文件路径" show-overflow-tooltip></el-table-column>
      <el-table-column prop="StartMode" align="center" label="启动类型" show-overflow-tooltip width="100">
        <template slot-scope="scope">
          <span v-text="startMode[scope.row.StartMode] || scope.row.StartMode"></span>
        </template>
      </el-table-column>
      <el-table-column prop="State" align="center" label="服务状态" width="100">
        <template slot-scope="scope">
          <el-tag type="info" size="small" v-if="scope.row.State==='Stopped'">已停止</el-tag>
          <el-tag type="success" effect="dark" size="small" v-else>已启动</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120">
        <template slot-scope="scope">
          <el-button type="text" size="small"
                     v-if="scope.row.State==='Stopped'"
                     @click="serviceState(scope.row.Name, 'start')">启动
          </el-button>
          <el-button type="text" size="small"
                     v-else @click="serviceState(scope.row.Name, 'stop')">停止
          </el-button>
          <el-button v-if="scope.row.PathName" type="text" size="small"
                     @click="showFileDialog(scope.row)">管理
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <add-dialog ref="refAddDialog" @refresh="init"></add-dialog>
    <show-file-dialog ref="refShowFileDialog"></show-file-dialog>
  </div>
</template>

<script>

export default {
  name: "index",
  components: {
    'add-dialog': () => import('./add-dialog.vue'),
    'show-file-dialog': () => import('@/views/site/show-file-dialog'),
  },
  data() {
    return {
      loading: false,
      keywords: '',
      searchDatas: [],
      datas: [],
      startMode: {
        'Auto': '自动',
        'Manual': '手动',
        'Disabled': '禁用'
      },
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.loading = true;
      this.$http.get('/api/v1/win-service')
        .then(res => {
          this.searchDatas = this.datas = res;
          this.searchHandler();
        })
        .catch(err => {
          console.error(err.response);
          this.$modal.msgError(err.response.data.message);
        })
        .finally(() => {
          this.loading = false;
        })
    },
    searchHandler() {
      if (!this.keywords) {
        this.searchDatas = this.datas;
        return;
      }
      this.searchDatas = this.datas.filter(item =>
        this.filterField(item.DisplayName) || this.filterField(item.Description))
    },
    filterField(txt) {
      if (!txt) {
        return false
      }
      return txt.toLocaleLowerCase().includes(this.keywords.toLocaleLowerCase())
    },
    serviceState(Name, state) {
      this.$http.patch(`/api/v1/win-service/${window.btoa(Name)}/actions/${state}`)
        .then(() => {
          this.$modal.msgSuccess('执行成功');
          this.init();
        })
        .catch(err => {
          console.error(err.response);
          this.$modal.msgError(err.response.data.message);
        })
    },
    openAddDialog() {
      this.$refs.refAddDialog.open();
    },
    showFileDialog(row) {
      if (!row.PathName) {
        return
      }
      let path = row.PathName.substring(0, row.PathName.indexOf('.exe'));
      if (path) {
        path = path.substring(0, path.lastIndexOf('\\'));
      } else {
        path = row.PathName.substring(0, row.PathName.lastIndexOf('\\'));
      }
      if (!path) {
        return;
      }
      this.$refs.refShowFileDialog.open(path);
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
