<template>
  <div class="main">
    <div class="tip">您没有访问该页面的权限</div>
    <a-button type="primary" @click="toHome">返回首页</a-button>
  </div>
</template>

<script>
export default {
  name: "NoPermission",
  methods: {
    toHome() {
      this.$router.replace("/");
    },
  },
};
</script>

<style lang="less" scoped>
.main {
  padding: 0.3rem;
  display: flex;
  flex-direction: column;
  align-items: center;

  .tip {
    margin-bottom: 0.2rem;
    font-size: 0.18rem;
  }
}
</style>
