<template>
  <div class="main">这是主页面</div>
</template>

<script>
import header from "./header.vue";
export default {
  name: "HOME",
  components: {
    header,
  },
  data() {},
  methods: {},
};
</script>

<style lang="less" scoped>
.main {
  width: 100%;
  height: 100vh;
}
</style>
