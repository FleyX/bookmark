import * as http from "axios";
import { getToken } from "./UserUtil";
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
  const headers = {
    "jwt-token": await getToken()
  };
  if (isForm) {
    headers["Content-Type"] = "multipart/form-data";
  }
  try {
    const res = await http.default.request({
      url,
      baseURL: "/bookmark/api",
      method,
      params,
      data: body,
      headers
    });
    const { code, data, message } = res.data;
    if (code === 1) {
      return data;
    }
    if (code === -1 && redirect) {
      // 跳转到登陆页
      router.replace(`/public/login?redirect=${encodeURIComponent(router.currentRoute.fullPath)}`);
      return null;
    }
    window.vueInstance.$message.error(message);
    throw new Error(message);
  } catch (err) {
    throw new Error(err);
  }
}

/**
 * get方法
 * @param {*} url url
 * @param {*} params url参数
 * @param {*} redirect 未登陆是否跳转到登陆页
 */
async function get(url, params = null, redirect = true) {
  return request(url, "get", params, null, redirect);
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
