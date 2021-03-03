<template>
  <a-spin :spinning="loading" :delay="300">
    <div class="search">
      <search :showActions="true" @location="location" />
    </div>
    <div class="actions">
      <span class="myBookmark">我的书签</span>
      <a-tooltip title="刷新书签缓存">
        <a-button @click="refresh(true)" type="primary" shape="circle" icon="sync" />
      </a-tooltip>
      <a-tooltip title="多选">
        <a-button type="primary" shape="circle" icon="check" @click="switchMul" />
      </a-tooltip>
      <a-tooltip
        v-if="
          (checkedKeys.length === 0 && (currentSelect == null || currentSelect.type === 1)) ||
          (checkedKeys.length === 1 && checkedNodes[0].type === 1)
        "
        title="添加书签"
      >
        <a-button type="primary" shape="circle" icon="plus" @click="addData" />
      </a-tooltip>
      <a-tooltip v-if="currentSelect || checkedKeys.length === 1" title="编辑书签">
        <a-button type="primary" shape="circle" icon="edit" @click="editData" />
      </a-tooltip>
      <a-tooltip v-if="moveShow" title="移动书签">
        <a-button type="primary" shape="circle" icon="scissor" />
      </a-tooltip>
      <a-popconfirm
        v-if="checkedKeys.length > 0 || currentSelect"
        title="此操作同时也会删除子节点数据，确认？"
        ok-text="是"
        cancel-text="否"
        @confirm="deleteBookmarks"
      >
        <a-tooltip title="删除书签">
          <a-button type="danger" shape="circle" icon="delete" />
        </a-tooltip>
      </a-popconfirm>
    </div>
    <a-empty v-if="treeData.length == 0 && loading == false" description="无数据，点击上方 + 新增"></a-empty>
    <a-tree
      v-else
      :tree-data="treeData"
      :loaded-keys="loadedKeys"
      :selected-keys="currentSelect ? [currentSelect.bookmarkId] : []"
      :load-data="loadData"
      :checked-keys="checkedKeys"
      :replace-fields="replaceFields"
      :expandedKeys="expandedKeys"
      @select="select"
      @expand="expand"
      @check="check"
      blockNode
      :checkable="mulSelect"
      checkStrictly
      :draggable="!isPhone"
      @drop="onDrop"
      @rightClick="rightClick"
    />
    <!-- 新增、修改 -->
    <a-modal v-model="addModal.show" :title="addModal.isAdd ? '新增' : '编辑'" :footer="null">
      <add-bookmark v-if="addModal.show" :isAdd="addModal.isAdd" :targetNode="addModal.targetNode" @close="close" />
    </a-modal>
  </a-spin>
</template>

<script>
import AddBookmark from "../../../../components/main/things/AddBookmark.vue";
import Search from "../../../../components/main/Search.vue";
import HttpUtil from "../../../../util/HttpUtil.js";
import { mapState, mapActions } from "vuex";
export default {
  name: "BookmarkManage",
  components: { AddBookmark, Search },
  data() {
    return {
      treeData: [],
      expandedKeys: [], //已展开的keys
      checkedKeys: [], //已选择的keys，多选框优先级高于树节点选择
      checkedNodes: [], //选中的节点数据
      loadedKeys: [], //已加载数据
      replaceFields: {
        title: "name",
        key: "bookmarkId",
      },
      mulSelect: false, //多选框是否显示
      currentSelect: null, //当前树的选择项
      loading: true, //是否显示loading
      moveShow: false, //是否显示移动节点
      //新增书签弹窗相关
      addModal: {
        show: false,
        //新增、修改目标数据，null说明向根节点增加数据
        targetNode: null,
        //是否为新增动作
        isAdd: false,
      },
    };
  },
  computed: {
    ...mapState("treeData", ["totalTreeData"]),
    ...mapState("globalConfig", ["isPhone"]),
  },
  async mounted() {
    await this.$store.dispatch("treeData/ensureDataOk");
    this.treeData = this.totalTreeData[""];
    this.loading = false;
  },
  methods: {
    /**
     * 加载数据，兼容treeNode为id
     */
    loadData(treeNode) {
      return new Promise((resolve) => {
        const data = typeof treeNode === "number" ? this.$store.getters["treeData/getById"](treeNode) : treeNode.dataRef;
        let newPath = data.path + "." + data.bookmarkId;
        if (!this.totalTreeData[newPath]) {
          this.totalTreeData[newPath] = [];
        }
        this.totalTreeData[newPath].forEach((item) => (item.isLeaf = item.type === 0));
        data.children = this.totalTreeData[newPath];
        this.treeData = [...this.treeData];
        this.loadedKeys.push(data.bookmarkId);
        resolve();
      });
    },
    async refresh(deleteCache) {
      if (deleteCache) {
        this.loading = true;
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
            .filter((item) => item.length > 0)
            .map((item) => parseInt(item)),
          item.bookmarkId,
        ];
      } else {
        this.expandedKeys.pop();
      }
    },
    rightClick({ node }) {
      if (this.currentSelect === node.dataRef) {
        this.currentSelect = null;
      } else {
        this.currentSelect = node.dataRef;
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
          this.checkedNodes.findIndex((item1) => item1.bookmarkId === item.bookmarkId),
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
              .filter((item) => item.length > 0)
              .map((item) => parseInt(item)),
            item.bookmarkId,
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
        this.checkedNodes.forEach((item) =>
          item.type === 1 ? pathList.push(item.path + "." + item.bookmarkId) : bookmarkIdList.push(item.bookmarkId)
        );
      }
      if (this.currentSelect) {
        this.currentSelect.type === 1
          ? pathList.push(this.currentSelect.path + "." + this.currentSelect.bookmarkId)
          : bookmarkIdList.push(this.currentSelect.bookmarkId);
      }
      if (pathList.length === 0 && bookmarkIdList.length === 0) {
        this.$message.warn("请选择后再进行操作");
        return;
      }

      this.loading = true;
      await HttpUtil.post("/bookmark/batchDelete", null, {
        pathList,
        bookmarkIdList,
      });
      this.$store.commit("treeData/deleteData", { pathList, bookmarkIdList });
      //删除已经被删除的数据
      pathList.forEach((item) => {
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
      this.$store.commit("treeData/version", null);
      this.checkedNodes = [];
      this.checkedKeys = [];
      this.currentSelect = null;
      this.loading = false;
      this.mulSelect = false;
    },
    async editData() {
      this.$set(this.addModal, "show", true);
      this.$set(this.addModal, "isAdd", false);
      this.$set(this.addModal, "targetNode", this.currentSelect || (this.checkedNodes.length > 0 ? this.checkedNodes[0] : null));
    },
    async addData() {
      this.$set(this.addModal, "show", true);
      this.$set(this.addModal, "isAdd", true);
      this.$set(this.addModal, "targetNode", this.currentSelect || (this.checkedNodes.length > 0 ? this.checkedNodes[0] : null));
    },
    async location(item) {
      console.log(item);
      this.refresh(false);
      this.expandedKeys = item.path
        .split(".")
        .filter((one) => one.length > 0)
        .map((one) => parseInt(one));
      this.loadedKeys = item.path
        .split(".")
        .filter((one) => one.length > 0)
        .map((one) => parseInt(one));
      this.expandedKeys.forEach(async (one) => await this.loadData(one));
      this.currentSelect = item;
    },
    /**
     * 关闭弹窗
     * @param data data为null说明需要刷新书签树,不为浪即为修改/新增的对象
     */
    async close(data) {
      console.log(data);
      if (this.addModal.isAdd) {
        //新增
        if (data == null) {
          //上传书签文件
          this.refresh(true);
        } else {
          //单个新增
          this.$store.commit("treeData/addNode", { sourceNode: this.addModal.targetNode, targetNode: data });
          this.treeData = [...this.totalTreeData[""]];
        }
      } else {
        //编辑
        this.treeData = [...this.totalTreeData[""]];
      }
      this.$store.commit("treeData/version", null);
      this.addModal = {
        show: false,
        targetNode: null,
        isAdd: false,
      };
    },
    async onDrop(info) {
      const target = info.node.dataRef;
      if (!info.dropToGap && target.type === 0) {
        this.$message.error("无法移动到书签内部");
        return;
      }
      this.loading = true;
      let body = await this.$store.dispatch("treeData/moveNode", info);
      try {
        await HttpUtil.post("/bookmark/moveNode", null, body);
        this.$message.success("移动完成");
        this.treeData = [...this.totalTreeData[""]];
        this.$store.commit("treeData/version", null);
      } catch (error) {
        console.error(error);
        this.$message.error("后台移动失败，将于2s后刷新页面，以免前后台数据不一致");
        // setTimeout(() => window.location.reload(), 2000);
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style lang="less" scoped>
.search {
}
.actions {
  height: 0.42rem;
  display: flex;
  align-items: center;
}
.myBookmark {
  font-size: 0.25rem;
  font-weight: 600;
}
</style>
