<template>
  <a-spin :spinning="loading" :delay="300">
    <div>
      <span class="myBookmark">我的书签</span>
      <a-tooltip title="刷新书签缓存">
        <a-button @click="refresh(true)" type="primary" shape="circle" icon="sync" />
      </a-tooltip>
      <a-tooltip title="多选">
        <a-button type="primary" shape="circle" icon="check" @click="switchMul" />
      </a-tooltip>
      <a-tooltip v-if="checkedKeys.length === 0 || checkedKeys.length === 1" title="添加书签">
        <a-button type="primary" shape="circle" icon="plus" />
      </a-tooltip>
      <a-tooltip v-if="currentSelect || checkedKeys.length === 1" title="编辑书签">
        <a-button type="primary" shape="circle" icon="edit" />
      </a-tooltip>
      <a-tooltip v-if="moveShow" title="移动书签">
        <a-button type="primary" shape="circle" icon="scissor" />
      </a-tooltip>
      <a-tooltip v-if="checkedKeys.length > 0 || currentSelect" title="删除书签" @click="deleteBookmarks">
        <a-button type="danger" shape="circle" icon="delete" />
      </a-tooltip>
    </div>
    <a-tree
      :tree-data="treeData"
      :loaded-keys="loadedKeys"
      :selected-keys="currentSelect ? [currentSelect.bookmarkId] : []"
      :load-data="loadData"
      :checked-keys="checkedKeys"
      @check="check"
      :replace-fields="replaceFields"
      :expandedKeys="expandedKeys"
      @select="select"
      @expand="expand"
      blockNode
      :checkable="mulSelect"
      checkStrictly
    />
  </a-spin>
</template>

<script>
import AddBookmark from "../../../../components/main/things/AddBookmark.vue";
import HttpUtil from "../../../../util/HttpUtil.js";
import { mapState, mapActions } from "vuex";
export default {
  name: "BookmarkManage",
  components: { AddBookmark },
  data() {
    return {
      treeData: [],
      expandedKeys: [], //已展开的keys
      checkedKeys: [], //已选择的keys，多选框优先级高于树节点选择
      checkedNodes: [], //选中的节点数据
      loadedKeys: [], //已加载数据
      replaceFields: {
        title: "name",
        key: "bookmarkId"
      },
      mulSelect: false, //多选框是否显示
      currentSelect: null, //当前树的选择项
      loading: true, //是否显示loading
      moveShow: false //是否显示移动节点
    };
  },
  computed: {
    ...mapState("treeData", ["totalTreeData"])
  },
  async beforeCreate() {
    await this.$store.dispatch("treeData/init");
    this.treeData = this.totalTreeData[""];
    this.loading = false;
  },
  methods: {
    loadData(treeNode) {
      return new Promise(resolve => {
        const data = treeNode.dataRef;
        let newPath = data.path + "." + data.bookmarkId;
        if (!this.totalTreeData[newPath]) {
          this.totalTreeData[newPath] = [];
        }
        this.totalTreeData[newPath].forEach(item => (item.isLeaf = item.type === 0));
        data.children = this.totalTreeData[newPath];
        this.treeData = [...this.treeData];
        this.loadedKeys.push(data.bookmarkId);
        resolve();
      });
    },
    async refresh(deleteCache) {
      this.loading = true;
      if (deleteCache) {
        await this.$store.dispatch("treeData/refresh");
      }
      this.treeData = [...this.totalTreeData[""]];
      this.expandedKeys = [];
      this.checkedKeys = [];
      this.checkedNodes = [];
      this.loadedKeys = [];
      this.currentSelect = null;
      this.loading = false;
    },
    expand(expandedKeys, { expanded, node }) {
      if (expanded) {
        let item = node.dataRef;
        this.expandedKeys = [
          ...item.path
            .split(".")
            .filter(item => item.length > 0)
            .map(item => parseInt(item)),
          item.bookmarkId
        ];
      } else {
        this.expandedKeys.pop();
      }
    },
    check(key, { checked, node }) {
      let item = node.dataRef;
      if (checked) {
        this.checkedKeys.push(item.bookmarkId);
        this.checkedNodes.push(item);
      } else {
        this.checkedKeys.splice(this.checkedKeys.indexOf(item.bookmarkId), 1);
        this.checkedNodes.splice(
          this.checkedNodes.findIndex(item1 => item1.bookmarkId === item.bookmarkId),
          1
        );
      }
    },
    select(key, { selected, node }) {
      let item = node.dataRef;
      if (item.type === 1) {
        if (selected && this.mulSelect === false) {
          this.currentSelect = item;
        } else {
          this.currentSelect = null;
        }
        let index = this.expandedKeys.indexOf(item.bookmarkId);
        if (index > -1) {
          this.expandedKeys.splice(index, 1);
        } else {
          this.expandedKeys = [
            ...item.path
              .split(".")
              .filter(item => item.length > 0)
              .map(item => parseInt(item)),
            item.bookmarkId
          ];
        }
      } else {
        HttpUtil.post("/bookmark/visitNum", { id: item.bookmarkId }, null);
        window.open(item.url);
      }
    },
    switchMul() {
      if (this.mulSelect) {
        this.mulSelect = false;
        this.checkedKeys = [];
      } else {
        this.mulSelect = true;
        this.currentSelect = null;
      }
    },
    async deleteBookmarks() {
      //删除，如果有多选，删除多选，否则删除树节点选中项
      const bookmarkIdList = [],
        pathList = [];
      if (this.checkedNodes) {
        this.checkedNodes.forEach(item => (item.type === 1 ? pathList.push(item.path + "." + item.bookmarkId) : bookmarkIdList.push(item.bookmarkId)));
      }
      if (this.currentSelect) {
        this.currentSelect.type === 1 ? pathList.push(this.currentSelect.path + "." + this.currentSelect.bookmarkId) : bookmarkIdList.push(this.currentSelect.bookmarkId);
      }
      if (pathList.length === 0 && bookmarkIdList.length === 0) {
        this.$message.warn("请选择后再进行操作");
        return;
      }
      this.loading = true;
      await HttpUtil.post("/bookmark/batchDelete", null, { pathList, bookmarkIdList });
      this.$store.commit("treeData/deleteData", { pathList, bookmarkIdList });
      //删除已经被删除的数据
      pathList.forEach(item => {
        let id = parseInt(item.split(".").reverse()[0]);
        let index = this.loadedKeys.indexOf(id);
        if (index > -1) {
          this.loadedKeys.splice(index, 1);
        }
        index = this.expandedKeys.indexOf(id);
        if (index > -1) {
          this.expandedKeys.splice(index, 1);
        }
      });
      this.checkedNodes = [];
      this.checkedKeys = [];
      this.currentSelect = null;
      this.loading = false;
      this.mulSelect = false;
    }
  }
};
</script>

<style lang="less" scoped>
.myBookmark {
  font-size: 0.25rem;
  font-weight: 600;
}
</style>
