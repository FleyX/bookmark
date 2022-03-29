<template>
  <div class="bottom">
    <router-link to="/public/about">关于</router-link>
  </div>
</template>

<script>
import { mapState } from "vuex";
export default {
  name: "homeTop",
  data() {
    return {};
  },
};
</script>

<style lang="less" scoped>
.bottom {
  height: 0.4rem;
  padding: 0.1rem;
  text-align: right;
}
</style>
