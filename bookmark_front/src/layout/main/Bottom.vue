<template>
  <div class="main">
    <div>开源地址:<a href="https://github.com/FleyX/bookmark">github.com/FleyX/bookmark</a> &emsp;<a href="https://github.com/FleyX/bookmark/issues">反馈/建议</a></div>
  </div>
</template>

<script>
export default {
  name: "Bottom",
};
</script>

<style lang="less" scoped>
@import "../../global";
.main {
  background-color: rgb(207, 198, 198);
  height: @bottomHeight;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.14rem;
}
</style>
