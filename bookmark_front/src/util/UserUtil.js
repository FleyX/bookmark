const TOKEN = "token";
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
