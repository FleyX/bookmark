<template>
  <div class="main">
    <div>
      开源地址:
      <a href="https://github.com/FleyX/bookmark" target="_blank">github.com/FleyX/bookmark</a>
      &emsp;
      <router-link to="/public/about">关于本站</router-link>
    </div>
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
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 0.14rem;
}
</style>
