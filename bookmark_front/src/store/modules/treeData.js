import localforage from "localforage";
import HttpUtil from "../../util/HttpUtil";

const TOTAL_TREE_DATA = "totalTreeData";
const VERSION = "version";

/**
 * 书签树相关配置
 */
const state = {
  //全部书签数据
  [TOTAL_TREE_DATA]: {},
  [VERSION]: null,
  isInit: false,
  /**
   * 是否正在加载数据
   */
  isIniting: false
};

const getters = {
  /**
   * 通过id获取节点数据
   */
  getById: state => id => {
    let arr = Object.values(state[TOTAL_TREE_DATA]);
    for (let i in arr) {
      for (let j in arr[i]) {
        if (arr[i][j].bookmarkId === id) {
          return arr[i][j];
        }
      }
    }
    return null;
  }
};

const actions = {
  //从缓存初始化数据
  async init(context) {
    if (context.state.isInit || context.state.isIniting) {
      return;
    }
    try {
      context.commit("isIniting", true);
      let realVersion = await HttpUtil.get("/user/version");
      let data = await localforage.getItem(TOTAL_TREE_DATA);
      let version = await localforage.getItem(VERSION);
      if (!data || realVersion > version) {
        await context.dispatch("refresh");
      } else {
        context.commit(TOTAL_TREE_DATA, data);
        context.commit(VERSION, version);
      }
      context.commit("isInit", true);
    } finally {
      context.commit("isIniting", false);
    }
  },
  /**
   * 确保数据加载完毕
   */
  ensureDataOk(context) {
    return new Promise((resolve, reject) => {
      let timer = setInterval(() => {
        try {
          if (context.state.isInit && context.state.isIniting == false) {
            clearInterval(timer);
            resolve();
          }
        } catch (err) {
          reject(err);
        }
      }, 100);
    });
  },
  //刷新缓存数据
  async refresh(context) {
    let treeData = await HttpUtil.get("/bookmark/currentUser");
    if (!treeData[""]) {
      treeData[""] = [];
    }
    Object.values(treeData).forEach(item =>
      item.forEach(item1 => {
        item1.isLeaf = item1.type === 0;
        item1.class = "treeNodeItem";
        item1.scopedSlots = { title: "nodeTitle" };
      })
    );
    let version = await HttpUtil.get("/user/version");
    await context.dispatch("updateVersion", version);
    context.commit(TOTAL_TREE_DATA, treeData);
    await localforage.setItem(TOTAL_TREE_DATA, treeData);
  },
  //清除缓存数据
  async clear(context) {
    context.commit(TOTAL_TREE_DATA, null);
    context.commit(VERSION, null);
    context.commit("isInit", false);
    context.commit("isIniting", false);
    await localforage.removeItem(TOTAL_TREE_DATA);
    await localforage.removeItem(VERSION);
  },
  /**
   * 移动节点
   */
  async moveNode(context, info) {
    let data = context.state[TOTAL_TREE_DATA];
    const target = info.node.dataRef;
    const current = info.dragNode.dataRef;
    //从原来位置中删除当前节点
    let currentList = data[current.path];
    currentList.splice(
      currentList.findIndex(item => item.bookmarkId === current.bookmarkId),
      1
    );
    //请求体
    const body = {
      bookmarkId: current.bookmarkId,
      sourcePath: current.path,
      targetPath: "",
      //-1 表示排在最后
      sort: -1
    };
    if (info.dropToGap) {
      body.targetPath = target.path;
      //移动到目标节点的上面或者下面
      let targetList = data[target.path];
      //目标节点index
      let index = targetList.indexOf(target);
      //移动节点相对于目标节点位置的增量
      let addIndex = info.dropPosition > index ? 1 : 0;
      body.sort = target.sort + addIndex;
      targetList.splice(index + addIndex, 0, current);
      for (let i = index + 1; i < targetList.length; i++) {
        targetList[i].sort += 1;
      }
    } else {
      //移动到一个文件夹下面
      body.targetPath = target.path + "." + target.bookmarkId;
      let targetList = data[body.targetPath];
      if (!targetList) {
        targetList = [];
        data[body.targetPath] = targetList;
      }
      body.sort = targetList.length > 0 ? targetList[targetList.length - 1].sort + 1 : 1;
      targetList.push(current);
    }
    //更新节点的path和对应子节点path
    current.path = body.targetPath;
    current.sort = body.sort;
    //如果为文件夹还要更新所有子书签的path
    if (body.sourcePath !== body.targetPath) {
      let keys = Object.keys(data);
      //旧路径
      let oldPath = body.sourcePath + "." + current.bookmarkId;
      //新路径
      let newPath = body.targetPath + "." + current.bookmarkId;
      keys.forEach(item => {
        if (!item.startsWith(oldPath)) {
          return;
        }
        let newPathStr = item.replace(oldPath, newPath);
        let list = data[item];
        delete data[item];
        data[newPathStr] = list;
        list.forEach(item1 => (item1.path = newPathStr));
      });
    }
    context.commit(TOTAL_TREE_DATA, context.state[TOTAL_TREE_DATA]);
    await context.dispatch("updateVersion", null);
    await localforage.setItem(TOTAL_TREE_DATA, state[TOTAL_TREE_DATA]);
    return body;
  },
  /**
   * 更新版本数据
   */
  async updateVersion({ commit, state }, version) {
    commit(VERSION, version == null ? state[VERSION] + 1 : version);
    await localforage.setItem(VERSION, state[VERSION]);
  },
  /**
   * 新增书签、文件夹
   */
  async addNode(context, { sourceNode, targetNode }) {
    if (sourceNode === null) {
      if (context.state[TOTAL_TREE_DATA][""] === undefined) {
        context.state[TOTAL_TREE_DATA][""] = [];
      }
      context.state[TOTAL_TREE_DATA][""].push(targetNode);
    } else {
      if (sourceNode.children === undefined) {
        sourceNode.children = [];
      }
      sourceNode.children.push(targetNode);
    }
    if (targetNode.type === 0) {
      context.state[TOTAL_TREE_DATA][targetNode.path + "." + targetNode.bookmarkId] = [];
    }
    targetNode.isLeaf = targetNode.type === 0;
    targetNode.class = "treeNodeItem";
    targetNode.scopedSlots = { title: "nodeTitle" };
    context.commit(TOTAL_TREE_DATA, context.state[TOTAL_TREE_DATA]);
    await context.dispatch("updateVersion", null);
    await localforage.setItem(TOTAL_TREE_DATA, state[TOTAL_TREE_DATA]);
  },
  /**
   * 删除节点数据
   */
  async deleteData(context, { pathList, bookmarkIdList }) {
    //待删除的书签
    let bookmarkIdSet = new Set();
    bookmarkIdList.forEach(item => bookmarkIdSet.add(item));
    //删除子节点
    pathList.forEach(item => {
      delete state[TOTAL_TREE_DATA][item];
      Object.keys(context.state[TOTAL_TREE_DATA])
        .filter(key => key.startsWith(item + "."))
        .forEach(key => delete state[TOTAL_TREE_DATA][key]);
      bookmarkIdSet.add(parseInt(item.split(".").reverse()));
    });
    //删除直接选中的节点
    Object.keys(context.state[TOTAL_TREE_DATA]).forEach(item => {
      let list = context.state[TOTAL_TREE_DATA][item];
      for (let i = list.length - 1; i >= 0; i--) {
        if (bookmarkIdSet.has(list[i].bookmarkId)) {
          list.splice(i, 1);
        }
      }
    });
    context.commit(TOTAL_TREE_DATA, context.state[TOTAL_TREE_DATA]);
    await context.dispatch("updateVersion", null);
    await localforage.setItem(TOTAL_TREE_DATA, state[TOTAL_TREE_DATA]);
  },
  /**
   * 编辑书签节点
   */
  async editNode({ dispatch, state, commit }, { node, newName, newUrl, newIcon }) {
    node.name = newName;
    node.url = newUrl;
    node.icon = newIcon;
    commit(TOTAL_TREE_DATA, state[TOTAL_TREE_DATA]);
    await dispatch("updateVersion", null);
    await localforage.setItem(TOTAL_TREE_DATA, state[TOTAL_TREE_DATA]);
  }
};

const mutations = {
  [TOTAL_TREE_DATA]: (state, totalTreeData) => {
    state.totalTreeData = totalTreeData;
  },
  isInit(state, isInit) {
    state.isInit = isInit;
  },
  isIniting(state, isIniting) {
    state.isIniting = isIniting;
  },

  [VERSION]: (state, version) => {
    state[VERSION] = version;
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
