<template>
  <div class="home">
    <Top />
    <Content>
      <router-view />
    </Content>
    <Bottom />
  </div>
</template>

<script>
import Content from "@/layout/main/Content.vue";
import Bottom from "@/layout/main/Bottom.vue";
import Top from "@//layout/main/Top.vue";

export default {
  name: "Home",
  components: { Top, Content, Bottom }
};
</script>

<style lang="less" scoped></style>
