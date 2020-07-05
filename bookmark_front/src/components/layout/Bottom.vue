<template>
  <div class="main">
    <div>bottom</div>
  </div>
</template>

<script>
export default {
  name: "Bottom"
};
</script>

<style lang="less" scoped>
@import "../../global";
.main {
  background-color: gray;
  height: @bottomHeight;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.14rem;
}
</style>
