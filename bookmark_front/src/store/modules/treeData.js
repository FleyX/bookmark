import localforage from "localforage";
import httpUtil from "../../util/HttpUtil";

const TOTAL_TREE_DATA = "totalTreeData";

/**
 * 书签树相关配置
 */
const state = {
  //全部书签数据
  [TOTAL_TREE_DATA]: {},
  version: null,
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
    let userInfo = await httpUtil.get("/user/currentUserInfo");
    context.commit("version", userInfo.version);
  },
  //清除缓存数据
  async clear(context) {
    context.commit(TOTAL_TREE_DATA, {});
    await localforage.removeItem(TOTAL_TREE_DATA);
  },
  async moveNode(context, info) {
    debugger;
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
    return body;
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
  },
  /**
   * 新增书签、文件夹
   */
  addNode(state, { sourceNode, targetNode }) {
    if (sourceNode === null) {
      if (state[TOTAL_TREE_DATA][""] === undefined) {
        state[TOTAL_TREE_DATA][""] = [];
      }
      state[TOTAL_TREE_DATA][""].push(targetNode);
    } else {
      if (sourceNode.children === undefined) {
        sourceNode.children = [];
      }
      sourceNode.children.push(targetNode);
    }
    if (targetNode.type === 0) {
      state[TOTAL_TREE_DATA][targetNode.path + "." + targetNode.bookmarkId] = [];
    }
    localforage.setItem(TOTAL_TREE_DATA, state[TOTAL_TREE_DATA]);
  },
  version(state, version) {
    if (version == null) {
      state.version = state.version + 1;
    } else {
      state.version = version;
    }
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
