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
import Top from "@/components/layout/Top.vue";
import Content from "@/components/layout/Content.vue";
import Bottom from "@/components/layout/Bottom.vue";

export default {
  name: "Home",
  components: { Top, Content, Bottom }
};
</script>

<style lang="less" scoped></style>
