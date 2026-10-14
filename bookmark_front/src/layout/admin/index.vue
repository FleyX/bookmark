<template>
  <div class="main">
    <Side />
    <div class="content">
      <router-view />
    </div>
  </div>
</template>

<script>
import Side from "@/layout/admin/Side.vue";

export default {
  name: "adminLayout",
  components: { Side },
};
</script>

<style lang="less" scoped>
@import (reference) "../../assets/global.less";

.main {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;

  .content {
    flex: 1;
    padding: 0.2rem;
    overflow: auto;
    font-size: 0.14rem;
    background-color: @contentbgcolor;
  }
}
</style>
