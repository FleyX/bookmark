<template>
  <div id="app">
    <Top />
    <Content>
      <router-view />
    </Content>
    <Bottom />
  </div>
</template>

<script>
import Top from "@/components/layout/Top.vue";
import Content from "@/components/layout/Content.vue";
import Bottom from "@/components/layout/Bottom.vue";

export default {
  name: "App",
  components: { Top, Content, Bottom }
};
</script>

<style lang="less">
html,
body {
  margin: 0;
  padding: 0;
  font-size: 100px;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
