<template>
  <div class="main">
    <top />
    <div class="content">
      <search :style="{ width: isPhone ? '100%' : '60%' }" />
      <div>书签收藏区</div>
    </div>
    <bottom />
  </div>
</template>

<script>
import Top from "@/layout/home/Top.vue";
import Bottom from "@/layout/home/Bottom.vue";
import Search from "@/components/main/Search.vue";
import { mapState } from "vuex";
import { GLOBAL_CONFIG, IS_PHONE } from "@/store/modules/globalConfig";
export default {
  name: "HOME",
  components: {
    Top,
    Bottom,
    Search,
  },
  data() {
    return {};
  },
  computed: {
    ...mapState(GLOBAL_CONFIG, [IS_PHONE, IS_PHONE]),
  },
  methods: {},
};
</script>

<style lang="less" scoped>
.main {
  width: 100%;
  height: 100vh;
  background: url("/static/img/homeBg.jpg") no-repeat center center;
  background-size: cover;
  background-attachment: fixed;

  .content {
    height: calc(~"100vh" - 1.01rem);
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
  }
}
</style>
