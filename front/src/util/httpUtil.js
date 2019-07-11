import { notification } from "antd";
import { createBrowserHistory } from "history";
import axios from "axios";

const history = createBrowserHistory();

//定义http实例
const instance = axios.create({
  baseURL: "/bookmark/api",
  timeout: 5000
});

//实例添加请求拦截器
instance.interceptors.request.use(
  req => {
    req.headers["jwt-token"] = window.token;
    return req;
  },
  error => {
    console.log(error);
  }
);

//实例添加响应拦截器
instance.interceptors.response.use(
  function(res) {
    console.log(res);
    const data = res.data;
    if (data.code === 1) {
      return data.data;
    } else if (data.code === -2) {
      return Promise.reject(data.message);
    } else {
      showError(data);
      return Promise.reject(data.message);
    }
  },
  function(error) {
    console.log(error);
    showError(error.response);
    return Promise.reject(JSON.stringify(error));
  }
);

function showError(response) {
  let description,
    message = "出问题啦";
  if (response) {
    description = response.message;
    if (response.code === -1) {
      setTimeout(() => {
        let redirect = encodeURIComponent(window.location.pathname + window.location.search);
        history.replace("/public/login?redirect=" + redirect);
      }, 1000);
    }
  } else {
    description = "无网络连接";
  }
  notification.open({
    message,
    description,
    duration: 2
  });
}

export default instance;
