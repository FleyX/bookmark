<template>
  <div class="main">
    <img class="ico" src="/static/img/bookmarkLogo.png" />
    <div class="main-body">
      <router-view />

    </div>
  </div>
</template>

<script>
export default {
  name: "Public",
  async created() {
    //进入注册、登录页需要清理掉所有的缓存数据
    await this.$store.dispatch("treeData/clear");
    await this.$store.dispatch("globalConfig/clear");
  },
};
</script>

<style lang="less" scoped>
@import "../../global.less";
.main {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  .ico {
    position: fixed;
    left: 5%;
    top: 5%;
    width: 2rem;
  }
  .main-body {
    min-width: 5rem;
    min-height: 3.5rem;
    background-color: @publicBgColor;
    border-radius: 5px;
    padding: 0.1rem;
  }
}
</style>
