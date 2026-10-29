<template>
  <div class="adminLayout">
    <Side />
    <div class="content">
      <div class="contentInner">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import Side from "@/layout/admin/Side.vue";

export default {
  name: "AdminLayout",
  components: { Side },
};
</script>

<style lang="less" scoped>
@import (reference) "../../assets/global.less";

@pageBgColor: #f5f7fa;
@surfaceBgColor: #fff;
@contentMaxWidth: 14rem;

.adminLayout {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background-color: @pageBgColor;

  .content {
    flex: 1 1 auto;
    min-width: 0;
    display: flex;
    flex-direction: column;
    padding: 0.24rem;
    overflow: auto;
    font-size: 0.14rem;

    .contentInner {
      flex: 1 0 auto;
      width: 100%;
      max-width: @contentMaxWidth;
      margin: 0 auto;
      padding: 0.24rem;
      background-color: @surfaceBgColor;
      border-radius: 0.08rem;
      box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
    }
  }
}

@media screen and (max-width: 768px) {
  .adminLayout .content {
    padding: 0.12rem;

    .contentInner {
      padding: 0.16rem;
      border-radius: 0.06rem;
    }
  }
}
</style>
