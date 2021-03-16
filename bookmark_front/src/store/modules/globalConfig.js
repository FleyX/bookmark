import localforage from "localforage";
import HttpUtil from "../../util/HttpUtil";
const USER_INFO = "userInfo";
const TOKEN = "token";

/**
 * 存储全局配置
 */
const state = {
  /**
   * 用户信息
   */
  [USER_INFO]: {},
  /**
   * token
   */
  [TOKEN]: null,
  /**
   * 是否已经初始化完成,避免多次重复初始化
   */
  isInit: false,
  /**
   * 是否移动端
   */
  isPhone: false
};

const getters = {};

const actions = {
  //初始化数据
  async init(context) {
    if (context.state.isInit) {
      return;
    }
    const token = await localforage.getItem(TOKEN);
    await context.dispatch("setToken", token);

    let userInfo = await localforage.getItem(USER_INFO);
    if (userInfo) {
      context.commit(USER_INFO, userInfo);
    }
    try {
      await context.dispatch("refreshUserInfo");
    } catch (err) {
      console.error(err);
    }
    context.commit("isInit", true);
    context.commit("isPhone", /Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent));
  },
  async refreshUserInfo({ commit }) {
    let userInfo = await HttpUtil.get("/user/currentUserInfo");
    await localforage.setItem(USER_INFO, userInfo);
    commit(USER_INFO, userInfo);
  },
  async setToken({ commit }, token) {
    await localforage.setItem(TOKEN, token);
    commit(TOKEN, token);
  },
  //登出清除数据
  async clear(context) {
    await localforage.removeItem("userInfo");
    await localforage.removeItem("token");
    context.commit(USER_INFO, null);
    context.commit(TOKEN, null);
    context.commit("isInit", false);
  }
};

const mutations = {
  userInfo(state, userInfo) {
    state.userInfo = userInfo;
  },
  token(state, token) {
    state.token = token;
  },
  isInit(state, isInit) {
    state.isInit = isInit;
  },
  isPhone(state, status) {
    state.isPhone = status;
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
