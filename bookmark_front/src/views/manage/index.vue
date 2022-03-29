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
import Content from "@/layout/manage/Content.vue";
import Bottom from "@/layout/manage/Bottom.vue";
import Top from "@/layout/manage/Top.vue";

export default {
  name: "Home",
  components: { Top, Content, Bottom },
};
</script>

<style lang="less" scoped></style>
