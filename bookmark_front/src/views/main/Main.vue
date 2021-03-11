<template>
  <div class="home">
    <Top />
    <Content>
      <router-view />
    </Content>
    <Bottom />
  </div>
</template>

<script>
import Content from "@/layout/main/Content.vue";
import Bottom from "@/layout/main/Bottom.vue";
import Top from "@//layout/main/Top.vue";

import httpUtil from "../../util/HttpUtil";
export default {
  name: "Home",
  components: { Top, Content, Bottom },
  data() {
    return {
      timer: null,
      //当前是第几轮
      count: 0,
      //计时经过多少轮后才处理
      total: 1,
      //弹窗是否处于展开状态
      isOpen: false,
    };
  },
  async mounted() {
    //数据初始化
    await this.$store.dispatch("globalConfig/init");
    console.log("globalConfig加载完毕");
    await this.$store.dispatch("treeData/init");
    console.log("treeData加载完毕");
    console.log("state数据:", this.$store.state);
    this.timer = setInterval(this.checkVersion, 60 * 1000);
  },
  destroyed() {
    if (this.timer != null) {
      clearInterval(this.timer);
    }
  },
  methods: {
    /**
     * 检查当前页面版本是否和服务器版本一致
     */
    async checkVersion() {
      this.count++;
      if (this.count < this.total || this.isOpen) {
        return;
      }
      this.count = 0;
      let version = await httpUtil.get("/user/version");
      const _this = this;
      if (this.$store.state.treeData.version < version) {
        this.isOpen = true;
        this.$confirm({
          title: "书签数据有更新，是否立即刷新？",
          content: "点击确定将刷新整个页面，请注意！",
          cancelText: "五分钟后再提醒",
          closable: false,
          keyboard: false,
          maskClosable: false,
          onOk() {
            _this.isOpen = false;
            return new Promise(async (resolve) => {
              await _this.$store.dispatch("treeData/clear");
              window.location.reload();
              resolve();
            });
          },
          onCancel() {
            _this.isOpen = false;
            _this.total = 5;
          },
          afterClose() {
            _this.isOpen = false;
          },
        });
      }
    },
  },
};
</script>

<style lang="less" scoped></style>
