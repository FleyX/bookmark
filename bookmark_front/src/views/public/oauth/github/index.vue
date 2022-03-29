<template>
  <div class="github">处理中，请稍候。。。</div>
</template>

<script>
export default {
  name: "GithubRedirect",
  data() {
    return {};
  },
  mounted() {
    let body = {
      type: "github",
      code: this.$route.query.code,
    };
    localStorage.setItem("oauthMessage", JSON.stringify(body));
  },
};
</script>

<style lang="less" scoped>
.github {
  width: 100%;
  height: 100%;
  text-align: center;
}
</style>