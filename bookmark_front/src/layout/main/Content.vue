<template>
  <div class="main">
    <slot></slot>
  </div>
</template>

<script>
export default {};
</script>

<style lang="less" scoped>
@import "../../global.less";

@sum: @topHeight + @bottomHeight;

.main {
  position: relative;
  margin: 0 auto;
  padding: 0.08rem;
  margin-top: @topHeight;
  min-height: calc(~"100vh" - @sum);
  font-size: 0.14rem;
  background-color: white;
}

@media screen and (max-width: 500px) {
  .main {
    width: 96%;
  }
}

@media screen and (min-width: 500px) {
  .main {
    width: 90%;
  }
}
</style>
