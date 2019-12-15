<template>
  <div class="main">
    <el-tree :props="props" :load="loadNode" @node-click="nodeClick" lazy> </el-tree>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'BookmarkTree',
  data() {
    return {
      props: {
        label: 'name',
        children: 'children',
        isLeaf: 'leaf',
      },
      treeObj: null,
    };
  },
  methods: {
    async loadNode(node, resolve) {
      if (window.treeData == null) {
        // eslint-disable-next-line no-undef
        if ((window.treeData = await localforage.getItem('treeData')) == null) {
          window.treeData = await axios.get('/bookmark/currentUser');
          // eslint-disable-next-line no-undef
          await localforage.setItem('treeData', window.treeData);
        }
        console.log(window.treeData);
      }
      console.log(node, resolve);
      let list;
      if (node.level === 0) {
        list = window.treeData[''];
      } else {
        list = window.treeData[node.data.path + '.' + node.data.bookmarkId];
      }
      list.forEach(item => (item.leaf = item.type === 0));
      console.log(list);
      return resolve(list);
    },
    nodeClick(data) {
      if (data.type === 1) {
        return;
      }
      let url = data.url;
      if (!url.startsWith('http')) {
        url = 'http://' + url;
      }
      console.log(url);
      window.open(data.url);
    },
  },
};
</script>

<style scoped>
.main {
  width: 80%;
  margin: 0 auto;
}
</style>
