import * as http from "axios";
import vuex from "../store/index.js";
import router from "../router/index";

/**
 * 请求
 * @param {*} url url
 * @param {*} method 方法
 * @param {*} params url参数
 * @param {*} body 请求体
 * @param {*} isForm 是否form
 * @param {*} redirect 接口返回未认证是否跳转到登陆
 * @returns 数据
 */
async function request(url, method, params, body, isForm, redirect) {
  let options = {
    url,
    baseURL: "/bookmark/api",
    method,
    params,
    headers: {
      "jwt-token": vuex.state.globalConfig.token
    }
  };
  if (isForm) {
    options.headers["Content-Type"] = "multipart/form-data";
  }
  if (body) {
    options.data = body;
  }
  let res;
  try {
    res = await http.default.request(options);
  } catch (err) {
    window.vueInstance.$message.error("发生了某些异常问题");
    console.error(err);
    return;
  }
  const { code, data, message } = res.data;
  if (code === 1) {
    return data;
  } else if (code === -1 && redirect) {
    // 跳转到登陆页
    window.vueInstance.$message.error("您尚未登陆，请先登陆");
    router.replace(`/public/login?redirect=${encodeURIComponent(router.currentRoute.fullPath)}`);
    throw new Error(message);
  } else if (code === 0) {
    //通用异常，使用
    window.vueInstance.$notification.error({
      message: "异常",
      description: message
    });
    throw new Error(message);
  } else if (code === -2) {
    //表单异常，使用message提示
    window.vueInstance.$message.error(message);
    throw new Error(message);
  }
}

/**
 * get方法
 * @param {*} url url
 * @param {*} params url参数
 * @param {*} redirect 未登陆是否跳转到登陆页
 */
async function get(url, params = null, redirect = true) {
  return request(url, "get", params, null, false, redirect);
}

/**
 * post方法
 * @param {*} url url
 * @param {*} params url参数
 * @param {*} body body参数
 * @param {*} isForm 是否表单数据
 * @param {*} redirect 是否重定向
 */
async function post(url, params, body, isForm = false, redirect = true) {
  return request(url, "post", params, body, isForm, redirect);
}

/**
 * put方法
 * @param {*} url url
 * @param {*} params url参数
 * @param {*} body body参数
 * @param {*} isForm 是否表单数据
 * @param {*} redirect 是否重定向
 */
async function put(url, params, body, isForm = false, redirect = true) {
  return request(url, "put", params, body, isForm, redirect);
}

/**
 * delete方法
 * @param {*} url url
 * @param {*} params url参数
 * @param {*} redirect 是否重定向
 */
async function deletes(url, params = null, redirect = true) {
  return request(url, "delete", params, null, redirect);
}

export default {
  get,
  post,
  put,
  delete: deletes
};
