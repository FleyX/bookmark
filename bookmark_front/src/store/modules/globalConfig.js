import localforage, { clear } from "localforage";
/**
 * 存储全局配置
 */
const state = {
  /**
   * 用户信息
   */
  userInfo: {},
  /**
   * token
   */
  token: null,
  /**
   * 是否已经初始化完成,避免多次重复初始化
   */
  isInit: false
};

const getters = {};

const actions = {
  //初始化数据
  async init(context) {
    if (context.state.isInit) {
      return;
    }
    context.commit("setUserInfo", await localforage.getItem("userInfo"));
    const token = await localforage.getItem("token");
    window.token = token;
    context.commit("setToken", token);
    context.commit("isInit", true);
  },
  //登出清除数据
  async clear(context) {
    await localforage.removeItem("userInfo");
    await localforage.removeItem("token");
    delete window.token;
    context.commit("setUserInfo", {});
    context.commit("setToken", null);
  }
};

const mutations = {
  setUserInfo(state, userInfo) {
    localforage.setItem("userInfo", userInfo);
    state.userInfo = userInfo;
  },
  setToken(state, token) {
    localforage.setItem("token", token);
    window.token = token;
    state.token = token;
  },
  isInit(state, isInit) {
    state.isInit = isInit;
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
