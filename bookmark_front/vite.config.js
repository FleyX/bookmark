import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    port: 4531,
    proxy: {
      "/bookmark/api": {
        target: "http://macbookpro.lan:8088",
        changeOrigin: true,
      },
    },
  },
});
