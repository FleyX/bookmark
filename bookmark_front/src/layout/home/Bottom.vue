<template>
  <div class="bottom">
    <router-link data-wallpaper-contrast to="/public/about">关于</router-link>
    <a-tooltip v-if="bgSrc">
      <template #title>点击后鼠标右键-将图像另存为</template>
      <a class="download" data-wallpaper-contrast :href="bgSrc" download="bing每日一图">下载壁纸</a>
    </a-tooltip>
  </div>
</template>

<script>
export default {
  name: "homeTop",
  props: ["bgSrc"],
};
</script>

<style lang="less" scoped>
.bottom {
  height: 0.4rem;
  padding: 0.1rem;
  text-align: center;

  [data-wallpaper-contrast] {
    padding: 0.03em 0.2em;
  }

  .download {
    margin-left: 1em;
  }
}
</style>
