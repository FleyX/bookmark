import localStore from "localforage";
// import HttpUtil from './HttpUtil.js';
const TOKEN = "token";
// consts USER_INFO = "userInfo";

/**
 * 获取用户token
 */
export async function getToken() {
  if (!window.token) {
    window.token = await localStore.getItem(TOKEN);
  }
  return window.token;
}

/**
 * 清除用户token
 */
export async function clearToken() {
  delete window.token;
  await localStore.removeItem(TOKEN);
}

/**
 * 本地获取用户信息
 */
export async function getUesrInfo() {
  return window.vueInstance.$store.state.globalConfig.userInfo;
}

/**
 * 从服务器获取用户信息
 */
export async function getOnlineUserInfo() {
  return null;
}
