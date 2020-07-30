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
  async beforeCreate() {
    //数据初始化
    await this.$store.dispatch("globalConfig/init");
    //更新用户基本信息
    this.$store.commit("globalConfig/setUserInfo", await httpUtil.get("/user/currentUserInfo"));
  }
};
</script>

<style lang="less" scoped></style>
