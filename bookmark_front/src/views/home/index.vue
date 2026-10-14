<template>
  <div class="main" :style="{ backgroundImage: backgroundImg }">
    <top />
    <div class="content">
      <main-search :style="{ width: isPhone ? '100%' : '60%' }" />
      <div :style="{ width: isPhone ? '100%' : '70%' }"><pin-bookmark /></div>
    </div>
    <bottom :bgSrc="serverConfig.bingImgSrc" />
  </div>
</template>

<script>
import { mapState } from "pinia";
import Top from "@/layout/home/Top.vue";
import Bottom from "@/layout/home/Bottom.vue";
import MainSearch from "@/components/main/Search.vue";
import PinBookmark from "./PinBookmark.vue";
import { useGlobalConfigStore } from "@/store/globalConfig";
export default {
  name: "HOME",
  components: {
    Top,
    Bottom,
    MainSearch,
    PinBookmark,
  },
  computed: {
    ...mapState(useGlobalConfigStore, ["isPhone", "serverConfig"]),
    backgroundImg() {
      let url = this.serverConfig.bingImgSrc ? this.serverConfig.bingImgSrc : "/static/img/homeBg.jpg";
      return `url("${url}")`;
    },
  },
};
</script>

<style lang="less" scoped>
.main {
  width: 100%;
  height: 100vh;
  background-repeat: no-repeat;
  background-size: cover;

  .content {
    height: calc(~"100vh" - 1.21rem);
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
  }
}
</style>
