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
  width: 90%;
  min-width: 5rem;
  font-size: 0.14rem;
  background-color: white;
}
</style>
