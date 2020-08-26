import localforage from "localforage";
import httpUtil from "../../util/HttpUtil";

const TOTAL_TREE_DATA = "totalTreeData";

/**
 * 书签树相关配置
 */
const state = {
  //全部书签数据
  [TOTAL_TREE_DATA]: {},
  isInit: false
};

const getters = {};

const actions = {
  //从缓存初始化数据
  async init(context) {
    if (context.state.isInit) {
      return;
    }
    let data = await localforage.getItem(TOTAL_TREE_DATA);
    if (data == null) {
      await context.dispatch("refresh");
    } else {
      context.commit(TOTAL_TREE_DATA, data);
    }
  },
  //刷新缓存数据
  async refresh(context) {
    let treeData = await httpUtil.get("/bookmark/currentUser");
    if (!treeData[""]) {
      treeData[""] = [];
    }
    treeData[""].forEach(item => (item.isLeaf = item.type === 0));
    context.commit(TOTAL_TREE_DATA, treeData);
    localforage.setItem(TOTAL_TREE_DATA, treeData);
  },
  //清除缓存数据
  async clear(context) {
    context.commit(TOTAL_TREE_DATA, {});
    await localforage.removeItem(TOTAL_TREE_DATA);
  }
};

const mutations = {
  totalTreeData(state, totalTreeData) {
    localforage.setItem(TOTAL_TREE_DATA, totalTreeData);
    state.totalTreeData = totalTreeData;
  },
  isInit(state, isInit) {
    state.isInit = isInit;
  },
  deleteData(state, { pathList, bookmarkIdList }) {
    //待删除的书签
    let bookmarkIdSet = new Set();
    bookmarkIdList.forEach(item => bookmarkIdSet.add(item));
    //删除子节点
    pathList.forEach(item => {
      delete state[TOTAL_TREE_DATA][item];
      Object.keys(state[TOTAL_TREE_DATA])
        .filter(key => key.startsWith(item + "."))
        .forEach(key => delete state[TOTAL_TREE_DATA][key]);
      bookmarkIdSet.add(parseInt(item.split(".").reverse()));
    });
    //删除直接选中的节点
    Object.keys(state.totalTreeData).forEach(item => {
      let list = state.totalTreeData[item];
      for (let i = list.length - 1; i >= 0; i--) {
        if (bookmarkIdSet.has(list[i].bookmarkId)) {
          list.splice(i, 1);
        }
      }
    });
    localforage.setItem(TOTAL_TREE_DATA, state[TOTAL_TREE_DATA]);
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
