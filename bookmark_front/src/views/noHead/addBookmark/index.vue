<template>
  <div class="ssoAddBookmark">
    正在添加，请稍后！！！
    <!-- <button @click="closeIframe">关闭</button> -->
  </div>
</template>

<script>
import HttpUtil from "@/util/HttpUtil";
import { TREE_DATA, addNode } from "@/store/modules/treeData";
export default {
  data() {
    return {
      form: {
        name: null,
        url: null,
        type: 0,
        path: ""
      }
    };
  },
  mounted() {
    //接受父节点传递的书签信息
    window.addEventListener("message", event => {
      if (!event.data.code) {
        return;
      }
      console.log("收到content消息", event);
      if (event.data.code == "addBookmarkAction") {
        console.log("新增书签");
        this.form.name = event.data.data.name;
        this.form.url = event.data.data.url;
        this.addBookmark();
      }
    });
    console.log("向父节点获取数据");
    window.parent.postMessage({ code: "getBookmarkData", receiver: "content" }, "*");
  },
  methods: {
    closeIframe() {
      window.parent.postMessage({ code: "closeIframe", receiver: "content" }, "*");
    },
    //新增书签
    async addBookmark() {
      let res = await HttpUtil.put("/bookmark", null, this.form);
      this.$message.success("添加成功");
      await this.$store.dispatch(TREE_DATA + "/" + addNode, { sourceNode: null, targetNode: res });
      setTimeout(this.closeIframe, 500);
    }
  }
};
</script>

<style lang="less" scoped>
.ssoAddBookmark {
  display: flex;
  justify-content: center;
  align-items: center;
  background: white;
  width: 100%;
  height: 100vh;
}
</style>
