import { defineStore } from "pinia";
import localforage from "localforage";
import HttpUtil from "../util/HttpUtil";

/** localforage 中保存token的键，旧版登录态共用 */
const TOKEN = "token";

export const useGlobalConfigStore = defineStore("globalConfig", {
  state: () => ({
    /**
     * 用户信息
     */
    userInfo: null,
    /**
     * token,null说明未获取登录凭证
     */
    token: null,
    /**
     * 是否已经初始化完成,避免多次重复初始化
     */
    isInit: false,
    /**
     * 是否移动端
     */
    isPhone: /Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent),
    /**
     * 是否支持未登录进入页面
     */
    supportNoLogin: false,
    /**
     * 服务端全局配置
     */
    serverConfig: {},
  }),
  actions: {
    //未登录需要进行的初始化
    async noLoginInit() {
      this.serverConfig = await HttpUtil.get("/common/config/global");
      let token = await localforage.getItem(TOKEN);
      if (token) {
        this.token = token;
        window.jwtToken = token;
      }
    },
    //登陆后的，初始化数据
    async loginInit() {
      if (this.isInit) {
        return;
      }
      this.userInfo = await HttpUtil.get("/user/currentUserInfo");
      this.isInit = true;
    },
    async setToken(token) {
      await localforage.setItem(TOKEN, token);
      window.jwtToken = token;
      this.token = token;
    },
    async clear() {
      await localforage.removeItem(TOKEN);
      window.jwtToken = null;
      this.userInfo = null;
      this.token = null;
      this.isInit = false;
    },
  },
});
