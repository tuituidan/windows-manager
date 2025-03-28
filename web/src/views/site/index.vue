<template>
  <div class="app-container">
    <div class="search-panel">
      <el-input suffix-icon="el-icon-search"
                placeholder="输入网站名称进行搜索..."
                clearable
                size="medium"
                v-model.trim="keywords"
                @input="searchHandler">
      </el-input>
    </div>
    <el-table stripe
              border
              size="medium"
              v-loading="loading"
              :data="searchDatas">
      <el-table-column label="序号" align="center" type="index" width="55"></el-table-column>
      <el-table-column prop="siteName" label="网站"></el-table-column>
      <el-table-column prop="bindings" label="绑定" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary"
                   v-if="scope.row.url"
                   :title="scope.row.url"
                   target="_blank"
                   :href="scope.row.url"
                   v-text="scope.row.bindings"></el-link>
          <span v-else
                :title="scope.row.bindings" v-text="scope.row.bindings"></span>
        </template>
      </el-table-column>
      <el-table-column prop="physicalPath" label="虚拟路径" show-overflow-tooltip></el-table-column>
      <el-table-column prop="siteState" align="center" label="网站状态" width="100">
        <template slot-scope="scope">
          <el-tag type="info" size="small" v-if="scope.row.siteState==='Stopped'">已停止</el-tag>
          <el-tag type="success" effect="dark" size="small" v-else>已启动</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="apppoolState" align="center" label="应用池状态" width="100">
        <template slot-scope="scope">
          <el-tag type="info" size="small" v-if="scope.row.apppoolState==='Stopped'">已停止</el-tag>
          <el-tag type="success" effect="dark" size="small" v-else>已启动</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="340">
        <template slot-scope="scope">
          <el-button type="text" size="small"
                     v-if="scope.row.siteState==='Stopped'"
                     @click="siteState(scope.row.id, 'start')">启动网站
          </el-button>
          <el-button type="text" size="small"
                     v-else @click="siteState(scope.row.id, 'stop')">停止网站
          </el-button>
          <el-button type="text" size="small"
                     @click="siteState(scope.row.id, 'restart')">重启网站
          </el-button>

          <el-button type="text" size="small"
                     v-if="scope.row.apppoolState==='Stopped'"
                     @click="apppoolState(scope.row.id, 'start')">启动应用池
          </el-button>
          <el-button type="text" size="small"
                     v-else @click="apppoolState(scope.row.id, 'stop')">停止应用池
          </el-button>
          <el-button type="text" size="small"
                     @click="apppoolState(scope.row.id, 'recycle')">回收应用池
          </el-button>

          <el-button type="text" size="small"
                     @click="showFile(scope.row)">管理
          </el-button>
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
  data() {
    return {
      loading: false,
      keywords: '',
      searchDatas: [],
      datas: [],
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.loading = true;
      this.$http.get('/api/v1/site')
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
      this.searchDatas = this.datas.filter(item => item.siteName.toLocaleLowerCase()
        .includes(this.keywords.toLocaleLowerCase()))
    },
    siteState(id, state) {
      this.$http.patch(`/api/v1/site/${id}/actions/${state}`)
        .then(() => {
          this.$modal.msgSuccess('执行成功');
          this.init();
        })
        .catch(err => {
          console.error(err.response);
          this.$modal.msgError(err.response.data.message);
        })
    },
    apppoolState(id, state) {
      this.$http.patch(`/api/v1/apppool/${id}/actions/${state}`)
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
      this.$refs.showFileDialog.open(row.physicalPath);
    },
  }
}
</script>

<style scoped lang="scss">
.search-panel {
  margin-bottom: 20px;

  .el-input {
    width: 40%;
  }
}
</style>
