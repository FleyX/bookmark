<template>
  <a-spin :spinning="loading" :delay="300">
    <div class="search">
      <search :showActions="true" @location="location" />
    </div>
    <div class="actions">
      <div class="left">
        <span class="myBookmark">我的书签</span>
        <a-tooltip title="刷新书签缓存">
          <a-button @click="refresh(true)" type="primary" shape="circle" icon="sync" />
        </a-tooltip>
        <a-tooltip title="多选">
          <a-button type="primary" shape="circle" icon="check" @click="switchMul" />
        </a-tooltip>
        <a-tooltip v-if="
          (checkedKeys.length === 0 && (currentSelect == null || currentSelect.type === 1)) ||
          (checkedKeys.length === 1 && checkedNodes[0].type === 1)
        " title="添加书签">
          <a-button type="primary" shape="circle" icon="plus" @click="addData" />
        </a-tooltip>
        <a-tooltip v-if="currentSelect || checkedKeys.length === 1" title="编辑书签">
          <a-button type="primary" shape="circle" icon="edit" @click="editData" />
        </a-tooltip>
        <a-tooltip v-if="moveShow" title="移动书签">
          <a-button type="primary" shape="circle" icon="scissor" />
        </a-tooltip>
        <a-popconfirm v-if="checkedKeys.length > 0 || currentSelect" title="此操作同时也会删除子节点数据，确认？" ok-text="是" cancel-text="否" @confirm="deleteBookmarks">
          <a-tooltip title="删除书签">
            <a-button type="danger" shape="circle" icon="delete" />
          </a-tooltip>
        </a-popconfirm>
      </div>
      <div>
        <a-tooltip title="导出书签">
          <a-button @click="exportBookmark" type="primary" shape="circle" icon="export" />
        </a-tooltip>
      </div>
    </div>
    <a-empty v-if="treeData.length == 0" description="无数据，点击上方 + 新增"></a-empty>
    <a-tree v-else :tree-data="treeData" :loaded-keys="loadedKeys" :selected-keys="currentSelect ? [currentSelect.bookmarkId] : []" :load-data="loadData" :checked-keys="checkedKeys" :replace-fields="replaceFields" :expandedKeys="expandedKeys" @select="select" @expand="expand" @check="check" blockNode :checkable="mulSelect" checkStrictly :draggable="!isPhone" @drop="onDrop">
      <a-dropdown :trigger="['contextmenu']" slot="nodeTitle" slot-scope="rec">
        <div class="titleContext">
          <a-icon type="folder" v-if="!rec.dataRef.isLeaf" />
          <img v-else-if="rec.dataRef.icon.length>0" :src="rec.dataRef.icon" style="width:16px" />
          <a-icon type="book" v-else />
          <span @click.prevent style="display:inline-block;min-width:50%;padding-left:0.4em">
            {{rec.dataRef.name}}
          </span>
        </div>
        <a-menu slot="overlay" @click="rightClick($event,rec.dataRef)">
          <a-menu-item v-if="!rec.dataRef.isLeaf" key="add">新增</a-menu-item>
          <a-menu-item v-else key="copy" class="copy-to-board" :data="rec.dataRef.url">复制URL</a-menu-item>
          <a-menu-item key="edit">编辑</a-menu-item>
          <a-menu-item key="delete">删除</a-menu-item>
        </a-menu>
      </a-dropdown>

    </a-tree>
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
import { downloadFile } from "../../../../util/FileUtil";
import ClipboardJS from "clipboard";
import moment from "moment";
export default {
  name: "BookmarkManage",
  components: { AddBookmark, Search },
  data() {
    return {
      treeData: [],
      expandedKeys: [], // 已展开的keys
      checkedKeys: [], // 已选择的keys，多选框优先级高于树节点选择
      checkedNodes: [], // 选中的节点数据
      loadedKeys: [], // 已加载数据
      replaceFields: {
        title: "name",
        key: "bookmarkId",
      },
      mulSelect: false, // 多选框是否显示
      currentSelect: null, // 当前树的选择项
      loading: true, // 是否显示loading
      moveShow: false, // 是否显示移动节点
      // 新增书签弹窗相关
      addModal: {
        show: false,
        // 新增、修改目标数据，null说明向根节点增加数据
        targetNode: null,
        // 是否为新增动作
        isAdd: false,
      },
      copyBoard: null, //剪贴板对象
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
    //初始化clipboard
    this.copyBoard = new ClipboardJS(".copy-to-board", {
      text: function (trigger) {
        return trigger.attributes.data.nodeValue;
      },
    });
    this.copyBoard.on("success", (e) => {
      this.$message.success("复制成功");
      e.clearSelection();
    });
  },
  destroyed() {
    if (this.copyBoard != null) {
      this.copyBoard.destroy();
    }
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
        data.children = this.totalTreeData[newPath];
        this.loadedKeys.push(data.bookmarkId);
        resolve();
      });
    },
    async refresh(deleteCache) {
      if (deleteCache) {
        this.loading = true;
        await this.$store.dispatch("treeData/refresh");
      }
      this.treeData = this.totalTreeData[""];
      this.expandedKeys = [];
      this.checkedKeys = [];
      this.checkedNodes = [];
      this.loadedKeys = [];
      this.currentSelect = null;
      this.loading = false;
    },
    expand(expandedKeys, { expanded, node }) {
      if (expanded) {
        const item = node.dataRef;
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
    check(key, { checked, node }) {
      const item = node.dataRef;
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
      const item = node.dataRef;
      if (item.type === 1) {
        if (selected && this.mulSelect === false) {
          this.currentSelect = item;
        } else {
          this.currentSelect = null;
        }
        const index = this.expandedKeys.indexOf(item.bookmarkId);
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
    //切换多选
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
      // 删除，如果有多选，删除多选，否则删除树节点选中项
      const bookmarkIdList = [];
      const pathList = [];
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
      this.$store.dispatch("treeData/deleteData", { pathList, bookmarkIdList });
      //删除已经被删除的数据
      pathList.forEach((item) => {
        const id = parseInt(item.split(".").reverse()[0]);
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
     * @param isUpload 说明为上传书签文件，需要刷新缓存数据
     */
    async close(isUpload) {
      if (isUpload) {
        this.refresh(true);
      } else {
        this.treeData.__ob__.dep.notify();
      }
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
      const body = await this.$store.dispatch("treeData/moveNode", info);
      try {
        await HttpUtil.post("/bookmark/moveNode", null, body);
        this.$message.success("移动完成");
        this.treeData.__ob__.dep.notify();
      } catch (error) {
        console.error(error);
        this.$message.error("后台移动失败，将于2s后刷新页面，以免前后台数据不一致");
        setTimeout(() => this.refresh(true), 2000);
      } finally {
        this.loading = false;
      }
    },
    //右键点击
    async rightClick({ key }, item) {
      if (key === "copy") {
        return;
      }
      //清楚多选状态，并设置当前选中
      this.mulSelect = false;
      this.checkedKeys = [];
      this.checkedNodes = [];
      this.currentSelect = item;
      if (key === "add") {
        this.addData();
      } else if (key === "delete") {
        this.$confirm({
          title: "确认删除？",
          content: "将删除当前节点和所有子节点，且不可恢复",
          onOk: () => {
            return new Promise(async (resolve, reject) => {
              await this.deleteBookmarks();
              resolve();
            });
          },
        });
      } else if (key === "edit") {
        this.editData();
      }
    },
    /**
     * 书签文件导出
     */
    exportBookmark() {
      let map = this.totalTreeData;
      let root = document.createElement("DL");
      this.dealList(root, map[""], map);
      let content =
        `<!DOCTYPE NETSCAPE-Bookmark-file-1>
<!-- This is an automatically generated file.
     It will be read and overwritten.
     DO NOT EDIT! -->
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<TITLE>Bookmarks</TITLE>
<H1>签签世界导出</H1>` + root.outerHTML;
      downloadFile(moment().format("YYYY-MM-DD") + "导出书签.html", content);
    },
    dealList(root, list, totalMap) {
      if (!list || list.length == undefined) {
        return;
      }
      list.forEach((item) => {
        let node = document.createElement("DT");
        root.appendChild(node);
        if (item.type === 0) {
          //说明为书签
          let url = document.createElement("A");
          url.setAttribute("HREF", item.url);
          url.setAttribute("ADD_DATE", parseInt(Date.now() / 1000));
          url.innerText = item.name;
          if (item.icon.length > 0) {
            url.setAttribute("ICON", item.icon);
          }
          node.appendChild(url);
        } else {
          //说明为文件夹
          let header = document.createElement("H3");
          header.setAttribute("ADD_DATE", parseInt(Date.now() / 1000));
          header.innerText = item.name;
          node.appendChild(header);
          let children = document.createElement("DL");
          node.appendChild(children);
          this.dealList(children, totalMap[item.path + "." + item.bookmarkId], totalMap);
        }
      });
    },
  },
};
</script>

<style lang="less" scoped>
.actions {
  height: 0.42rem;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .left {
    display: flex;
    justify-items: center;
  }
}
.myBookmark {
  font-size: 0.25rem;
  font-weight: 600;
}
.titleContext {
  display: flex;
  align-items: center;
}
</style>
