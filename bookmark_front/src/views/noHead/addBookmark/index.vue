<template>
  <div class="ssoAddBookmark">
    正在添加，请稍后！！！
    <button @click="closeIframe">关闭</button>
  </div>
</template>

<script>
import HttpUtil from "@/util/HttpUtil";
export default {
  data() {
    return {
      form: {
        icon: null,
        name: null,
        url: null,
        type: 0,
        path: ""
      }
    };
  },
  mounted() {
    window.addEventListener("message", event => {
      if (!event.data.code) {
        return;
      }
      console.log("收到content消息", event);
      if (event.data.code == "addBookmarkAction") {
        console.log("新增书签");
        this.form.icon = event.data.data.icon ? event.data.data.icon : null;
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
      // setTimeout(this.closeIframe, 2000);
    }
  }
};
</script>

<style lang="less" scoped>
.ssoAddBookmark {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
