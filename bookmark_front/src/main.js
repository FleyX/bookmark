import { createApp } from "vue";
import Antd, { message, notification, Modal } from "ant-design-vue";
import { createFromIconfontCN } from "@ant-design/icons-vue";
import "ant-design-vue/dist/reset.css";
import App from "./App.vue";
import router from "./router";
import { pinia } from "./store";
import "@/assets/global.less";

const IconFont = createFromIconfontCN({
  scriptUrl: "//at.alicdn.com/t/c/font_1261825_v7m0rilm4hm.js",
});

const app = createApp(App);
app.use(pinia);
app.use(router);
app.use(Antd);
app.component("my-icon", IconFont);

app.config.globalProperties.$message = message;
app.config.globalProperties.$notification = notification;
app.config.globalProperties.$confirm = Modal.confirm;
//旧工具类通过 window.vueInstance.$store.state.模块名 访问状态，此处兼容
app.config.globalProperties.$store = {
  get state() {
    return pinia.state.value;
  },
};

window.vueInstance = app.mount("#app");
