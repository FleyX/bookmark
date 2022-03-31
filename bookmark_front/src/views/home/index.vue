<template>
  <div class="main" :style="{ backgroundImage: backgroundImg }">
    <top />
    <div class="content">
      <search :style="{ width: isPhone ? '100%' : '60%' }" />
      <div :style="{ width: isPhone ? '100%' : '70%' }"><pin-bookmark /></div>
    </div>
    <bottom />
  </div>
</template>

<script>
import Top from "@/layout/home/Top.vue";
import Bottom from "@/layout/home/Bottom.vue";
import Search from "@/components/main/Search.vue";
import PinBookmark from "./PinBookmark.vue";
import { mapState } from "vuex";
import { GLOBAL_CONFIG, IS_PHONE, SERVER_CONFIG } from "@/store/modules/globalConfig";
export default {
  name: "HOME",
  components: {
    Top,
    Bottom,
    Search,
    PinBookmark,
  },
  data() {
    return {};
  },
  computed: {
    ...mapState(GLOBAL_CONFIG, [IS_PHONE, SERVER_CONFIG]),
    backgroundImg() {
      let url = this.serverConfig.bingImgSrc ? this.serverConfig.bingImgSrc : "/static/img/homeBg.jpg";
      return `url("${url}")`;
    },
  },
  methods: {},
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
