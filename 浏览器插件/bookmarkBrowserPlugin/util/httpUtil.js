import config from './config';
import axios from 'axios';

axios.defaults.timeout = 15000;
axios.defaults.baseURL = config.baseUrl;

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
      if (window.envType === 'background') {
        window.open(config.ssoUrl);
      } else {
        window.vueInstance.$router.replace('/public/login');
      }
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
export default axios;
