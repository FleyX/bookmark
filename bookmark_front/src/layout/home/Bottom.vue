<template>
  <div class="bottom">
    <router-link style="color: white" to="/public/about"><span class="text">关于</span></router-link>
    <router-link v-if="hasAnyAdminResource" style="color: white; margin-left: 1em" to="/admin"><span class="text">管理后台</span></router-link>
    <a-tooltip v-if="bgSrc">
      <template #title>点击后鼠标右键-将图像另存为</template>
      <a style="color: white; margin-left: 1em" :href="bgSrc" download="bing每日一图"><span class="text">下载壁纸</span></a>
    </a-tooltip>
  </div>
</template>

<script>
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";

export default {
  name: "homeTop",
  props: ["bgSrc"],
  computed: {
    ...mapState(useUserStore, ["hasAnyAdminResource"]),
  },
};
</script>

<style lang="less" scoped>
.bottom {
  height: 0.4rem;
  padding: 0.1rem;
  text-align: center;
  color: black;

  .text {
    color: rgba(255, 255, 255, 0.9);
  }
}
</style>
