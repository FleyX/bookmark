import localforage from "localforage";
import httpUtil from "../../util/HttpUtil";

/**
 * 书签树相关配置
 */
const state = {
  //全部书签数据
  totalTreeData: {},
  isInit: false
};

const getters = {};

const actions = {
  //从缓存初始化数据
  async init(context) {
    if (context.state.isInit) {
      return;
    }
    let data = await localforage.getItem("totalTreeData");
    if (data == null) {
      await context.dispatch("refresh");
    } else {
      context.commit("totalTreeData", data);
    }
  },
  //刷新缓存数据
  async refresh(context) {
    let treeData = await httpUtil.get("/bookmark/currentUser");
    context.commit("totalTreeData", treeData);
    localforage.setItem("totalTreeData", treeData);
  },
  //清除缓存数据
  async clear(context) {
    context.commit("totalTreeData", {});
    await localforage.removeItem("totalTreeData");
  }
};

const mutations = {
  treeData(state, treeData) {
    localforage.setItem("treeData", treeData);
    state.treeData = treeData;
  },
  totalTreeData(state, totalTreeData) {
    localforage.setItem("totalTreeData", totalTreeData);
    state.totalTreeData = totalTreeData;
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
