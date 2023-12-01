<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: "App",
  mounted() {
    window.qieziStatisticKey = "b74c4b571b644782a837433209827874";
    let script = document.createElement("script");
    script.type = "text/javascript";
    script.defer = true;
    script.src = "https://qiezi.fleyx.com/qiezijs/1.0/qiezi_statistic.min.js";
    document.getElementsByTagName("head")[0].appendChild(script);
  }
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
