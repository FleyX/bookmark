import Vue from "vue";
import { Button, FormModel, Input, Icon, message, Checkbox, Dropdown, Menu, Tree, Tooltip, Spin, notification, Empty, Modal, Radio, Upload } from "ant-design-vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";

Vue.component(Button.name, Button);
Vue.use(FormModel);
Vue.component(Input.name, Input);
Vue.component(Icon.name, Icon);
Vue.use(Checkbox);
Vue.use(Dropdown);
Vue.use(Menu);
Vue.use(Tree);
Vue.use(Tooltip);
Vue.use(Spin);
Vue.use(Empty);
Vue.use(Modal);
Vue.use(Radio);
Vue.use(Upload);

Vue.prototype.$message = message;
Vue.prototype.$notification = notification;
Vue.config.productionTip = false;

window.vueInstance = new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
