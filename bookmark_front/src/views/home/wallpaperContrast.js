/**
 * 首页壁纸文字对比度采样。
 *
 * 元素通过 data-wallpaper-contrast 标记，结果写入 CSS 自定义属性。Pin 内的元素同时计算悬浮深色覆盖层下的配色。
 */
const CONTRAST_SELECTOR = "[data-wallpaper-contrast]";
const SAMPLE_MAX_SIZE = 1024;
const SAMPLE_COLUMNS = 5;
const SAMPLE_ROWS = 5;
const MIN_TEXT_CONTRAST = 4.5;
const FALLBACK_FOREGROUND = "#ffffff";
const FALLBACK_BACKGROUND = "rgba(12, 12, 12, 0.82)";
const TRANSPARENT_BACKGROUND = "rgba(0, 0, 0, 0)";
// 与 PinBookmarkItem.vue 中的悬浮背景保持一致
const PIN_HOVER_OVERLAY = { red: 66, green: 66, blue: 66, alpha: 0.4 };

function channelToLinear(value) {
  const channel = value / 255;
  return channel <= 0.04045 ? channel / 12.92 : Math.pow((channel + 0.055) / 1.055, 2.4);
}

function luminance(red, green, blue) {
  return 0.2126 * channelToLinear(red) + 0.7152 * channelToLinear(green) + 0.0722 * channelToLinear(blue);
}

function contrastRatio(firstLuminance, secondLuminance) {
  const lighter = Math.max(firstLuminance, secondLuminance);
  const darker = Math.min(firstLuminance, secondLuminance);
  return (lighter + 0.05) / (darker + 0.05);
}

function applyStyle(element, color, background, suffix = "") {
  element.style.setProperty(`--wallpaper-contrast${suffix}-color`, color);
  element.style.setProperty(`--wallpaper-contrast${suffix}-background`, background);
}

function fallbackStyle() {
  return { color: FALLBACK_FOREGROUND, background: FALLBACK_BACKGROUND };
}

export default class WallpaperContrast {
  constructor(container) {
    if (!container) {
      throw new Error("首页壁纸对比度采样缺少容器");
    }
    this.container = container;
    this.image = null;
    this.samples = undefined;
    this.frame = null;
    this.destroyed = false;
    this.handleLayoutChange = () => this.schedule();
    this.mutationObserver = new window.MutationObserver(() => this.schedule());
    this.mutationObserver.observe(container, { childList: true, characterData: true, subtree: true });
    this.resizeObserver = window.ResizeObserver ? new window.ResizeObserver(() => this.schedule()) : null;
    if (this.resizeObserver) {
      this.resizeObserver.observe(container);
    }
    window.addEventListener("resize", this.handleLayoutChange);
    this.resetElements();
  }

  /**
   * 设置当前实际显示壁纸的已加载图片；传空时恢复到安全兜底配色
   */
  setImage(image) {
    if (this.destroyed) {
      return;
    }
    this.image = image || null;
    this.samples = undefined;
    this.resetElements();
    this.schedule();
  }

  /**
   * 将本轮 DOM、窗口和壁纸变化合并到下一帧
   */
  schedule() {
    if (this.destroyed || this.frame !== null) {
      return;
    }
    this.frame = window.requestAnimationFrame(() => {
      this.frame = null;
      this.updateElements();
    });
  }

  destroy() {
    this.destroyed = true;
    if (this.frame !== null) {
      window.cancelAnimationFrame(this.frame);
      this.frame = null;
    }
    this.mutationObserver.disconnect();
    if (this.resizeObserver) {
      this.resizeObserver.disconnect();
    }
    window.removeEventListener("resize", this.handleLayoutChange);
    this.samples = null;
    this.image = null;
  }

  getElements() {
    return Array.from(this.container.querySelectorAll(CONTRAST_SELECTOR));
  }

  resetElements() {
    this.getElements().forEach((element) => {
      const fallback = fallbackStyle();
      applyStyle(element, fallback.color, fallback.background);
      applyStyle(element, fallback.color, fallback.background, "-hover");
    });
  }

  updateElements() {
    if (this.destroyed) {
      return;
    }
    const imageSamples = this.image ? this.getImageSamples() : null;
    if (!imageSamples) {
      this.resetElements();
      return;
    }
    const containerRect = this.container.getBoundingClientRect();
    if (containerRect.width <= 0 || containerRect.height <= 0) {
      this.resetElements();
      return;
    }
    this.getElements().forEach((element) => {
      const elementRect = element.getBoundingClientRect();
      const normal = this.resolveStyle(elementRect, containerRect, imageSamples, false);
      const hovered = element.closest(".pinBookmarkItem") ? this.resolveStyle(elementRect, containerRect, imageSamples, true) : normal;
      applyStyle(element, normal.color, normal.background);
      applyStyle(element, hovered.color, hovered.background, "-hover");
    });
  }

  getImageSamples() {
    if (this.samples !== undefined) {
      return this.samples;
    }
    this.samples = null;
    try {
      const scale = Math.min(1, SAMPLE_MAX_SIZE / Math.max(this.image.naturalWidth, this.image.naturalHeight));
      const width = Math.max(1, Math.round(this.image.naturalWidth * scale));
      const height = Math.max(1, Math.round(this.image.naturalHeight * scale));
      const canvas = document.createElement("canvas");
      canvas.width = width;
      canvas.height = height;
      const context = canvas.getContext("2d", { willReadFrequently: true });
      if (!context) {
        return null;
      }
      context.drawImage(this.image, 0, 0, width, height);
      this.samples = { width, height, pixels: context.getImageData(0, 0, width, height).data };
      return this.samples;
    } catch (err) {
      console.warn("无法读取首页壁纸像素，使用安全配色", err);
      return null;
    }
  }

  resolveStyle(elementRect, containerRect, imageSamples, hovered) {
    const visibleLeft = Math.max(elementRect.left, containerRect.left);
    const visibleTop = Math.max(elementRect.top, containerRect.top);
    const visibleRight = Math.min(elementRect.right, containerRect.right);
    const visibleBottom = Math.min(elementRect.bottom, containerRect.bottom);
    if (visibleRight <= visibleLeft || visibleBottom <= visibleTop) {
      return fallbackStyle();
    }

    const coverScale = Math.max(containerRect.width / imageSamples.width, containerRect.height / imageSamples.height);
    const imageLeft = Math.max(0, (visibleLeft - containerRect.left) / coverScale);
    const imageTop = Math.max(0, (visibleTop - containerRect.top) / coverScale);
    const imageRight = Math.min(imageSamples.width, (visibleRight - containerRect.left) / coverScale);
    const imageBottom = Math.min(imageSamples.height, (visibleBottom - containerRect.top) / coverScale);
    if (imageRight <= imageLeft || imageBottom <= imageTop) {
      return fallbackStyle();
    }

    let minimumBlackContrast = Number.POSITIVE_INFINITY;
    let minimumWhiteContrast = Number.POSITIVE_INFINITY;
    for (let row = 0; row < SAMPLE_ROWS; row += 1) {
      for (let column = 0; column < SAMPLE_COLUMNS; column += 1) {
        const x = Math.min(imageSamples.width - 1, Math.floor(imageLeft + ((column + 0.5) / SAMPLE_COLUMNS) * (imageRight - imageLeft)));
        const y = Math.min(imageSamples.height - 1, Math.floor(imageTop + ((row + 0.5) / SAMPLE_ROWS) * (imageBottom - imageTop)));
        const offset = (y * imageSamples.width + x) * 4;
        if (imageSamples.pixels[offset + 3] !== 255) {
          return fallbackStyle();
        }
        let red = imageSamples.pixels[offset];
        let green = imageSamples.pixels[offset + 1];
        let blue = imageSamples.pixels[offset + 2];
        if (hovered) {
          const wallpaperWeight = 1 - PIN_HOVER_OVERLAY.alpha;
          red = PIN_HOVER_OVERLAY.red * PIN_HOVER_OVERLAY.alpha + red * wallpaperWeight;
          green = PIN_HOVER_OVERLAY.green * PIN_HOVER_OVERLAY.alpha + green * wallpaperWeight;
          blue = PIN_HOVER_OVERLAY.blue * PIN_HOVER_OVERLAY.alpha + blue * wallpaperWeight;
        }
        const pixelLuminance = luminance(red, green, blue);
        minimumBlackContrast = Math.min(minimumBlackContrast, contrastRatio(pixelLuminance, 0));
        minimumWhiteContrast = Math.min(minimumWhiteContrast, contrastRatio(pixelLuminance, 1));
      }
    }

    const useBlack = minimumBlackContrast >= minimumWhiteContrast;
    const bestContrast = useBlack ? minimumBlackContrast : minimumWhiteContrast;
    if (bestContrast >= MIN_TEXT_CONTRAST) {
      return { color: useBlack ? "#000000" : "#ffffff", background: TRANSPARENT_BACKGROUND };
    }
    return fallbackStyle();
  }
}
