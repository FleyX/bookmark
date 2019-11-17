import { notification, message } from "antd";
import axios from "axios";

//定义http实例
const instance = axios.create({
  baseURL: "/bookmark/api",
  timeout: 60000
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
    const data = res.data;
    if (data.code === 1) {
      return data.data;
    } else if (data.code === -2) {
      message.error(data.message);
      return Promise.reject(data.message);
    } else {
      showError(data, res.config.url.replace(res.config.baseURL, ""));
      return Promise.reject(data.message);
    }
  },
  function(error) {
    console.log(error);
    showError(error.response);
    return Promise.reject(JSON.stringify(error));
  }
);

function showError(response, url) {
  if (url === "/user/currentUserInfo") {
    return;
  }
  let description,
    message = "出问题啦";
  if (response) {
    description = response.message;
    if (response.code === -1) {
      let redirect = encodeURIComponent(window.location.pathname + window.location.search);
      window.reactHistory.replace("/public/login?redirect=" + redirect);
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
