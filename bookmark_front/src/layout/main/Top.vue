<template>
  <div class="main">
    <img class="ico" src="/static/img/bookmarkLogo.png" />
    <div class="user">fanxb</div>
  </div>
</template>

<script>
export default {
  name: "Top"
};
</script>

<style lang="less" scoped>
@import "../../global";
.main {
  position: fixed;
  top: 0;
  width: calc(100% - 10px);
  height: @topHeight - (2 * @topPadding);
  padding: @topPadding;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(197, 190, 198, 0.4);
  .ico {
    height: 100%;
  }
  .user {
    font-size: 0.3rem;
  }
}
</style>
