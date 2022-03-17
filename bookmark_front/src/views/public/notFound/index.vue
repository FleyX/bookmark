<template>
  <div class="main">
    页面走丢了，3s后返回首页
  </div>
</template>

<script>
export default {
  name: "NotFound",
  created() {
    setTimeout(() => {
      this.$router.replace("/");
    }, 3000);
  },
};
</script>

<style lang="less" scoped>
.main {
  width: 100%;
  height: 100%;
}
</style>