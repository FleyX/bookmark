<template>
  <div ref="home" class="main" :style="{ backgroundImage: backgroundImg }">
    <top />
    <div class="content">
      <main-search :style="{ width: isPhone ? '100%' : '60%' }" />
      <div :style="{ width: isPhone ? '100%' : '70%' }"><pin-bookmark /></div>
    </div>
    <bottom :bgSrc="bgDownloadSrc" />
  </div>
</template>

<script>
import { mapState } from "pinia";
import Top from "@/layout/home/Top.vue";
import Bottom from "@/layout/home/Bottom.vue";
import MainSearch from "@/components/main/Search.vue";
import PinBookmark from "./PinBookmark.vue";
import { useGlobalConfigStore } from "@/store/globalConfig";
import WallpaperContrast from "./wallpaperContrast";

/**
 * 前端内置背景图，后端壁纸不可用时使用
 */
const DEFAULT_BG = "/static/img/homeBg.jpg";

export default {
  name: "HOME",
  components: {
    Top,
    Bottom,
    MainSearch,
    PinBookmark,
  },
  data() {
    return {
      /**
       * 当前实际展示的背景地址
       */
      backgroundSrc: DEFAULT_BG,
      /**
       * 当前实际展示背景对应的已加载图片
       */
      backgroundImage: null,
      /**
       * 背景切换轮次，用于忽略旧地址迟到的加载结果
       */
      bgRequestId: 0,
    };
  },
  computed: {
    ...mapState(useGlobalConfigStore, ["isPhone", "serverConfig"]),
    backgroundImg() {
      return `url("${this.backgroundSrc}")`;
    },
    /**
     * 底栏下载地址，只在实际展示后端壁纸时提供
     */
    bgDownloadSrc() {
      return this.backgroundSrc === DEFAULT_BG ? "" : this.backgroundSrc;
    },
  },
  watch: {
    "serverConfig.bingImgSrc": {
      handler(src) {
        this.switchBackground(src);
      },
      immediate: true,
    },
  },
  beforeCreate() {
    this.defaultBackgroundImage = null;
    this.defaultBackgroundLoading = false;
    this.wallpaperContrast = null;
    this.destroyed = false;
  },
  mounted() {
    this.wallpaperContrast = new WallpaperContrast(this.$refs.home);
    this.wallpaperContrast.setImage(this.backgroundImage);
    if (this.backgroundSrc === DEFAULT_BG) {
      this.loadDefaultBackground();
    }
  },
  beforeUnmount() {
    this.destroyed = true;
    this.bgRequestId += 1;
    if (this.wallpaperContrast) {
      this.wallpaperContrast.destroy();
      this.wallpaperContrast = null;
    }
  },
  methods: {
    switchBackground(src) {
      const url = typeof src === "string" ? src.trim() : "";
      const requestId = ++this.bgRequestId;
      if (!url) {
        this.useDefaultBackground();
        return;
      }
      const img = new Image();
      img.onload = () => {
        if (requestId === this.bgRequestId) {
          this.backgroundSrc = url;
          this.setBackgroundImage(img);
        }
      };
      img.onerror = () => {
        if (requestId === this.bgRequestId) {
          this.useDefaultBackground();
        }
      };
      img.src = url;
    },
    /**
     * 切换到内置壁纸，并单独加载它供实际选色使用
     */
    useDefaultBackground() {
      this.backgroundSrc = DEFAULT_BG;
      this.setBackgroundImage(this.defaultBackgroundImage);
      this.loadDefaultBackground();
    },
    loadDefaultBackground() {
      if (this.defaultBackgroundImage || this.defaultBackgroundLoading) {
        return;
      }
      this.defaultBackgroundLoading = true;
      const image = new Image();
      image.onload = () => {
        if (this.destroyed) {
          return;
        }
        this.defaultBackgroundLoading = false;
        this.defaultBackgroundImage = image;
        if (this.backgroundSrc === DEFAULT_BG) {
          this.setBackgroundImage(image);
        }
      };
      image.onerror = () => {
        this.defaultBackgroundLoading = false;
      };
      image.src = DEFAULT_BG;
    },
    setBackgroundImage(image) {
      this.backgroundImage = image;
      if (this.wallpaperContrast) {
        this.wallpaperContrast.setImage(image);
      }
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
