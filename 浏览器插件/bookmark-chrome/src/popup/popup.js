import Vue from 'vue';
import App from './App';
import store from '../store';
import router from './router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios';

global.browser = require('webextension-polyfill');
Vue.prototype.$browser = global.browser;
Vue.use(ElementUI);

/* eslint-disable no-new */
window.vueInstance = new Vue({
  el: '#app',
  store,
  router,
  render: h => h(App),
});

/**
 * 配置axios
 */
axios.defaults.timeout = 15000;

/**
 * 请求拦截器
 */
axios.interceptors.request.use(
  function(config) {
    console.log(config);
    return config;
  },
  function(error) {
    console.error(error);
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  res => {
    console.log(res);
    return res.data;
  },
  error => {
    return Promise.reject(error);
  }
);
