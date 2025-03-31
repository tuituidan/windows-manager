<template>
  <el-dialog
    title="添加/移除服务"
    :visible.sync="visible"
    append-to-body
    :close-on-click-modal="false"
    width="1000px">
    <div class="search-panel">
      <el-input suffix-icon="el-icon-search"
                placeholder="输入服务名称或描述进行搜索..."
                clearable
                size="medium"
                v-model.trim="keywords"
                @input="searchHandler">
      </el-input>
      <el-button type="primary" @click="saveHandler">保 存</el-button>
    </div>
    <el-table stripe
              border
              v-loading="loading"
              :max-height="600"
              ref="dataTable"
              size="medium"
              @select="selectRowHandler"
              :data="searchDatas">
      <el-table-column align="center" type="selection" width="55"></el-table-column>
      <el-table-column label="序号" align="center" type="index" width="55"></el-table-column>
      <el-table-column prop="DisplayName" label="服务名称" show-overflow-tooltip></el-table-column>
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
    </el-table>
  </el-dialog>
</template>

<script>
export default {
  name: "show-file-dialog",
  data() {
    return {
      loading: false,
      visible: false,
      keywords: '',
      searchDatas: [],
      selectedItems: [],
      startMode: {
        'Auto': '自动',
        'Manual': '手动',
        'Disabled': '禁用'
      },
    }
  },
  methods: {
    open() {
      this.visible = true;
      this.loading = true;
      this.$http.get('/api/v1/win-service/all')
        .then(res => {
          this.searchDatas = this.datas = res;
          this.selectedItems = this.searchDatas.filter(it => it.selected).map(it => it.Name);
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
        this.tableSelection();
        return;
      }
      this.searchDatas = this.datas.filter(item =>
        this.filterField(item.DisplayName) || this.filterField(item.Description))
      this.tableSelection();
    },
    tableSelection() {
      this.$nextTick(() => {
        const selections = this.searchDatas.filter(it => this.selectedItems.includes(it.Name))
        for (let it of selections) {
          this.$refs.dataTable.toggleRowSelection(it, true);
        }
      })

    },
    filterField(txt) {
      if (!txt) {
        return false
      }
      return txt.toLocaleLowerCase().includes(this.keywords.toLocaleLowerCase())
    },
    selectRowHandler(selection, row) {
      if (selection.map(it => it.Name).includes(row.Name)) {
        this.selectedItems.push(row.Name)
      } else {
        this.selectedItems.splice(this.selectedItems.indexOf(row.Name), 1)
      }
    },
    saveHandler() {
      this.$http.post(`/api/v1/win-service`, this.selectedItems)
        .then(() => {
          this.$modal.msgSuccess('保存成功');
          this.$emit('refresh');
          this.visible = false;
        })
        .catch(err => {
          console.error(err.response);
          this.$modal.msgError(err.response.data.message);
        })
    },
  }
}
</script>

<style scoped lang="scss">
.el-table {
  margin-top: 15px;
}

.search-panel {
  display: flex;
  justify-content: space-between;

  .el-input {
    width: 500px;
  }
}
</style>
