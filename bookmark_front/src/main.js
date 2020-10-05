import Vue from "vue";
import {
  Button,
  FormModel,
  Input,
  Icon,
  message,
  Checkbox,
  Dropdown,
  Menu,
  Tree,
  Tooltip,
  Spin,
  notification,
  Empty,
  Modal,
  Radio,
  Upload,
  Popconfirm,
  AutoComplete,
  Select
} from "ant-design-vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";

const IconFont = Icon.createFromIconfontCN({
  scriptUrl: "//at.alicdn.com/t/font_1261825_3wf60i93sdm.js"
});
Vue.use(Button);
Vue.use(FormModel);
Vue.use(Input);
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
Vue.use(Popconfirm);
Vue.use(AutoComplete);
Vue.use(Select);
Vue.component("my-icon", IconFont);

Vue.prototype.$message = message;
Vue.prototype.$notification = notification;
Vue.prototype.$confirm = Modal.confirm;
Vue.config.productionTip = false;

window.vueInstance = new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
