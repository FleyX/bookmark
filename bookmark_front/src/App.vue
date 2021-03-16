<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: "App",
};
</script>

<style lang="less">
@import "./global.less";
html,
body {
  margin: 0;
  padding: 0;
  font-size: 100px;
  background-color: @bgColor;
  height: initial;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  font-size: 0.16rem;
}
</style>
