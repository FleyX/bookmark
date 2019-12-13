import Vue from 'vue';
import App from './App';
import store from '../store';
import router from './router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import config from '../util/config';
import axios from 'axios';

global.browser = require('webextension-polyfill');
Vue.prototype.$browser = global.browser;
Vue.use(ElementUI);

/**
 * 配置axios
 */
axios.defaults.timeout = 15000;
axios.defaults.baseURL = config.baseUrl;
/**
 * 请求拦截器
 */
axios.interceptors.request.use(
  function(config) {
    config.headers['jwt-token'] = window.token;
    return config;
  },
  function(error) {
    console.error(error);
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  res => {
    if (res.data.code === -1) {
      localStorage.removeItem('token');
      window.vueInstance.$router.replace('/public/login');
    } else if (res.data.code === 1) {
      return res.data.data;
    } else {
      Promise.reject(res);
    }
  },
  error => {
    return Promise.reject(error);
  }
);

/* eslint-disable no-new */
window.vueInstance = new Vue({
  el: '#app',
  store,
  router,
  render: h => h(App),
});
