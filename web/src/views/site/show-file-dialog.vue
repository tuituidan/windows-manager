<template>
  <el-dialog
    title="部署文件管理"
    :visible.sync="visible"
    append-to-body
    :close-on-click-modal="false"
    width="800px">
    <div>
      <div style="padding-bottom: 15px">当前上传路径：{{ uploadPath }}</div>
      <el-row>
        <el-col :span="2.5">
          <el-tooltip content="上传zip压缩包并自动解压文件">
            <el-upload :action="fileUploadUrl"
                       :data="{zip:true, uploadPath:uploadPath}"
                       :show-file-list="false"
                       :before-upload="beforeUpload"
                       :on-success="uploadSuccess">
              <el-button size="mini" type="success" plain>上传压缩包</el-button>
            </el-upload>
          </el-tooltip>
        </el-col>
        <el-col :span="2.5" :offset="1">
          <el-tooltip content="直接替换上传文件">
            <el-upload ref="uploader"
                       :action="fileUploadUrl"
                       :data="{uploadPath:uploadPath}"
                       :show-file-list="false"
                       :before-upload="beforeUpload"
                       :on-success="uploadSuccess"
                       multiple>
              <el-button size="mini" type="success" plain>上传文件</el-button>
            </el-upload>
          </el-tooltip>
        </el-col>
        <el-col :span="2.5" :offset="1">
          <el-tooltip content="勾选文件进行删除">
            <el-button type="danger" size="mini" plain @click="fileDeleteHandler">文件删除</el-button>
          </el-tooltip>
        </el-col>
        <el-col :span="2.5" :offset="1">
          <el-button type="primary" size="mini" plain @click="refreshTree">重新加载</el-button>
        </el-col>
        <el-col :span="2.5" :offset="1">
          <el-checkbox v-model="showHiddenFile" @change="refreshTree">显示隐藏文件</el-checkbox>
        </el-col>
      </el-row>
      <el-row style="margin-top: 10px">
        <el-col :span="23">
          <el-input size="small"
                    placeholder="输入关键字进行过滤"
                    suffix-icon="el-icon-search"
                    v-model="filterText">
          </el-input>
        </el-col>
        <el-col :span="1">
          <el-tooltip content="当前为懒加载树，未展开的节点无法搜索到，选中树节点支持复制粘贴上传文件到对应目录">
            <i class="el-icon-warning-outline"></i>
          </el-tooltip>
        </el-col>
      </el-row>
      <el-tree ref="fileTree"
               :props="props"
               highlight-current
               show-checkbox
               node-key="path"
               :load="loadNode"
               @current-change="treeCurrentChange"
               :filter-node-method="treeFilterNode"
               @paste.native="handlePaste"
               lazy>
        <div slot-scope="{ node, data }" style="display: flex;width: 100%;justify-content: space-between">
          <el-link :underline="false">
            <i v-if="data.type==='folder'" class="el-icon-folder el-icon--left"></i>
            <i v-else-if="data.type==='img'" class="el-icon-picture-outline el-icon--left"></i>
            <i v-else-if="data.type==='txt'" class="el-icon-document el-icon--left"></i>
            <i v-else class="el-icon-tickets el-icon--left"></i>
            {{ node.label }}
          </el-link>
          <div>
            <span v-if="node.isLeaf" v-text="data.fileSize" style="padding-right: 10px"></span>
            <span v-if="node.isLeaf" v-text="data.lastModifyTime" style="padding-right: 10px"></span>
            <el-button v-if="node.isLeaf"
                       :disabled="!operateFileExt.includes(data.type)"
                       type="text" size="small"
                       @click="showFileHandler(data)" v-text="data.type==='zip'?'解压':'查看'">
            </el-button>
            <el-button type="text"
                       size="small"
                       @click="downloadHander(data)">下载
            </el-button>
          </div>
        </div>
      </el-tree>
    </div>

    <el-image
      ref="imagePreview"
      v-show="false"
      :src="imgPreview.url"
      :preview-src-list="imgPreview.urlList"
    ></el-image>
  </el-dialog>
</template>

<script>
export default {
  name: "show-file-dialog",
  data() {
    return {
      visible: false,
      showHiddenFile: false,
      rootNode: null,
      uploadPath: '',
      rootPath: '',
      filterText: '',
      operateFileExt: ['txt', 'img', 'browser', 'zip'],
      props: {
        isLeaf: 'leaf'
      },
      imgPreview: {
        url: '',
        urlList: []
      },
      fileUploadUrl: `${process.env.VUE_APP_BASE_API}/api/v1/file/actions/upload`,
    }
  },
  watch: {
    filterText(val) {
      this.$refs.fileTree.filter(val);
    }
  },
  mounted() {
    this.initSetting();
  },
  methods: {
    initSetting() {
      this.$http.get('/api/v1/setting')
        .then(res => {
          this.showHiddenFile = res['show-hidden-file'] === 'true';
        })
    },
    treeCurrentChange(data) {
      if (data.leaf) {
        this.uploadPath = data.path.substring(0, data.path.lastIndexOf('\\'));
      } else {
        this.uploadPath = data.path;
      }
    },
    treeFilterNode(value, data) {
      if (!value) return true;
      return data.label.toLowerCase().indexOf(value.toLowerCase()) !== -1;
    },
    handlePaste(event) {
      const files = event.clipboardData && event.clipboardData.files;
      if (!(files && files.length)) {
        this.$modal.msgError('未获取到需要粘贴的文件，确保您已进行文件复制操作，且浏览器支持粘贴上传，否则请点击上面的文件上传按钮进行上传！');
        return;
      }
      for (const file of files) {
        this.$refs.uploader.handleStart(file);
      }
      this.$refs.uploader.submit();
    },
    open(path) {
      this.rootPath = path;
      this.visible = true;
      this.refreshTree();
    },
    loadNode(node, resolve) {
      if (node.level === 0) {
        this.rootNode = node;
      }
      const path = node.data ? encodeURIComponent(node.data.path) : '';
      this.$http.get(`/api/v1/site/files?rootPath=${encodeURIComponent(this.rootPath)}&path=${path}&showHiddenFile=${this.showHiddenFile}`)
        .then(res => {
          return resolve(res);
        })
    },
    refreshTree() {
      this.reloadTree();
      this.uploadPath = this.rootPath;
    },
    reloadTree() {
      if (this.rootNode) {
        this.rootNode.loaded = false;
        this.rootNode.expand();
      }
    },
    beforeUpload(){
      this.$modal.loading('文件上传中...');
      return true;
    },
    uploadSuccess() {
      this.$modal.msgSuccess('上传成功');
      this.$modal.closeLoading();
      this.reloadTree();
    },
    fileDeleteHandler() {
      const nodes = this.$refs.fileTree.getCheckedNodes();
      if (nodes.length <= 0) {
        this.$modal.msgWarning('请勾选文件进行删除');
        return;
      }
      this.$http.post('/api/v1/file/delete', nodes.map(item => item.path))
        .then(() => {
          this.$modal.msgSuccess('删除成功');
          this.reloadTree();
        })
        .catch(err => {
          console.log(err)
        });
    },
    downloadHander(data) {
      window.open(this.downloadUrl('download', data.path), '_blank');
    },
    showFileHandler(data) {
      if (data.type === 'img') {
        this.imgPreview.url = this.downloadUrl('download', data.path);
        this.imgPreview.urlList = [this.imgPreview.url];
        this.$refs.imagePreview.showViewer = true;
        return;
      }
      if (data.type === 'txt') {
        const url = this.$router.resolve({path: `/show-file/${encodeURIComponent(data.path)}`});
        window.open(url.href, '_blank')
        return;
      }
      if (data.type === 'browser') {
        window.open(this.downloadUrl('preview', data.path), '_blank');
        return;
      }
      if (data.type === 'zip') {
        this.$http.post('/api/v1/file/actions/unzip', data).then(() => {
          this.$modal.msgSuccess('解压成功');
          this.reloadTree();
        })
      }
    },
    downloadUrl(type, path) {
      return `${process.env.VUE_APP_BASE_API}/api/v1/file/action/${type}?path=${encodeURIComponent(path)}`;
    },
  }
}
</script>

<style scoped lang="scss">
.el-tree {
  background-color: #f5f5f5;
  padding-right: 8px;
  margin-top: 15px;
  height: 510px;
  overflow: auto
}

.el-icon-warning-outline {
  margin: 6px;
  font-size: 20px;
  color: #2d8cf0;
}
</style>
