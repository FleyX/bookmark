import js from "@eslint/js";
import pluginVue from "eslint-plugin-vue";
import prettier from "eslint-config-prettier";

export default [
  { ignores: ["dist/**", "node_modules/**"] },
  js.configs.recommended,
  ...pluginVue.configs["flat/essential"],
  prettier,
  {
    // 路由页面入口组件按页面目录命名（home、login 等），与旧工程保持一致
    files: ["src/views/**/*.vue"],
    rules: {
      "vue/multi-word-component-names": "off",
    },
  },
  {
    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",
      globals: {
        window: "readonly",
        document: "readonly",
        navigator: "readonly",
        console: "readonly",
        setInterval: "readonly",
        clearInterval: "readonly",
        setTimeout: "readonly",
        clearTimeout: "readonly",
        btoa: "readonly",
        atob: "readonly",
        Blob: "readonly",
        FormData: "readonly",
        URL: "readonly",
        Image: "readonly",
      },
    },
  },
];
