<template>
  <div>
    <a-tree
      :tree-data="treeData"
      :load-data="loadData"
      :replace-fields="replaceFields"
      :expandedKeys="expandedKeys"
      @select="select"
      @expand="expand"
      blockNode
    />
  </div>
</template>

<script>
import { mapState } from "vuex";
export default {
  name: "BookmarkManage",
  data() {
    return {
      treeData: [],
      expandedKeys: [],
      replaceFields: {
        title: "name",
        key: "bookmarkId"
      }
    };
  },
  computed: {
    ...mapState("treeData", ["totalTreeData"])
  },
  async beforeCreate() {
    await this.$store.dispatch("treeData/init");
    if (!this.totalTreeData[""]) {
      this.totalTreeData[""] = [];
    }
    this.totalTreeData[""].forEach(item => (item.isLeaf = item.type === 0));
    this.treeData = this.totalTreeData[""];
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
        resolve();
      });
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
    select(key, { node }) {
      let item = node.dataRef;
      if (item.type === 1) {
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
        window.open(item.url);
      }
    }
  }
};
</script>

<style></style>
