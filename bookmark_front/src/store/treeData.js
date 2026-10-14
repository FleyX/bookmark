import { defineStore } from "pinia";
import localforage from "localforage";
import { Modal } from "ant-design-vue";
import HttpUtil from "../util/HttpUtil";
import { checkJwtValid } from "@/util/UserUtil";
import { useGlobalConfigStore } from "./globalConfig";

/**书签版本检查间隔 */
const CHECK_INTERVAL = 5 * 60 * 1000;
/**本地缓存数据对比间隔 */
const LOCAL_DATA_CHECK_INTERVAL = 2000;
const TOTAL_TREE_DATA = "totalTreeData";
const VERSION = "version";

/**
 * 写入本地缓存前深拷贝,Pinia 响应式代理无法被 IndexedDB 结构化克隆
 */
function toStorable(value) {
  return value == null ? value : JSON.parse(JSON.stringify(value));
}

/**
 * 版本检查定时调度
 */
let timer = null;
/**
 * 检查本地版本是否有更新
 */
let checkLocalDataTimer = null;
/**
 * 刷新书签确认弹窗是否展示
 */
let toastShow = false;

export const useTreeDataStore = defineStore("treeData", {
  state: () => ({
    //全部书签数据
    totalTreeData: {},
    //版本
    version: null,
    //是否已经初始化书签数据
    isInit: false,
    // 是否正在加载数据
    isIniting: false,
    showRefreshToast: false,
    // 首页固定的书签
    homePinList: [],
    // 首页固定书签id Map
    homePinBookmarkIdMap: {},
  }),
  getters: {
    /**
     * 通过id获取书签数据
     */
    getById: (state) => (id) => {
      let arr = Object.values(state.totalTreeData);
      for (let i in arr) {
        for (let j in arr[i]) {
          if (arr[i][j].bookmarkId === id) {
            return arr[i][j];
          }
        }
      }
      return null;
    },
  },
  actions: {
    //登录前初始化
    noLoginInit() {},
    //登陆后初始化
    async loginInit() {
      if (this.isInit || this.isIniting) {
        return;
      }
      await this.refreshHomePinList();
      this.isIniting = true;
      this.totalTreeData = await localforage.getItem(TOTAL_TREE_DATA);
      this.version = await localforage.getItem(VERSION);
      await this.treeDataCheck(true);
      this.isInit = true;
      this.isIniting = false;
      timer = setInterval(() => this.treeDataCheck(false), CHECK_INTERVAL);
      checkLocalDataTimer = setInterval(() => this.checkLocalData(), LOCAL_DATA_CHECK_INTERVAL);
    },
    /**
     * 确保数据加载完毕
     */
    ensureDataOk() {
      return new Promise((resolve, reject) => {
        let checkTimer = setInterval(() => {
          try {
            if (this.isInit && this.isIniting === false) {
              clearInterval(checkTimer);
              resolve();
            }
          } catch (err) {
            reject(err);
          }
        }, 50);
      });
    },
    //刷新缓存数据
    async refresh() {
      let treeData = await HttpUtil.get("/bookmark/currentUser");
      if (!treeData[""]) {
        treeData[""] = [];
      }
      Object.values(treeData).forEach((item) =>
        item.forEach((item1) => {
          item1.isLeaf = item1.type === 0;
          item1.class = "treeNodeItem";
        }),
      );
      let version = await HttpUtil.get("/user/version");
      await this.updateVersion(version);
      await this.refreshHomePinList();
      this.totalTreeData = treeData;
      await localforage.setItem(TOTAL_TREE_DATA, treeData);
    },
    //清除缓存数据
    async clear() {
      this.totalTreeData = null;
      this.version = null;
      this.showRefreshToast = false;
      this.isInit = false;
      this.isIniting = false;
      this.homePinList = [];
      if (timer != null) {
        clearInterval(timer);
      }
      if (checkLocalDataTimer != null) {
        clearInterval(checkLocalDataTimer);
      }
      await localforage.removeItem(TOTAL_TREE_DATA);
      await localforage.removeItem(VERSION);
    },
    /**
     * 移动节点
     */
    async moveNode(info) {
      let data = this.totalTreeData;
      const target = info.node.dataRef;
      const current = info.dragNode.dataRef;
      //从原来位置中删除当前节点
      let currentList = data[current.path];
      currentList.splice(
        currentList.findIndex((item) => item.bookmarkId === current.bookmarkId),
        1,
      );
      //请求体
      const body = {
        bookmarkId: current.bookmarkId,
        sourcePath: current.path,
        targetPath: "",
        //-1 表示排在最后
        sort: -1,
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
        keys.forEach((item) => {
          if (!item.startsWith(oldPath)) {
            return;
          }
          let newPathStr = item.replace(oldPath, newPath);
          let list = data[item];
          delete data[item];
          data[newPathStr] = list;
          list.forEach((item1) => (item1.path = newPathStr));
        });
      }
      await this.updateVersion(null);
      await localforage.setItem(TOTAL_TREE_DATA, toStorable(this.totalTreeData));
      return body;
    },
    async refreshHomePinList() {
      let list = await HttpUtil.get("/home/pin");
      this.homePinList = list;
      let map = {};
      list.filter((item) => item.id).forEach((item) => (map[item.bookmarkId] = true));
      this.homePinBookmarkIdMap = map;
    },
    /**
     * 更新版本数据
     */
    async updateVersion(version) {
      this.version = version == null ? this.version + 1 : version;
      await localforage.setItem(VERSION, this.version);
    },
    /**
     * 新增书签、文件夹
     */
    async addNode({ sourceNode, targetNode }) {
      if (sourceNode === null) {
        if (this.totalTreeData[""] === undefined) {
          this.totalTreeData[""] = [];
        }
        this.totalTreeData[""].push(targetNode);
      } else {
        let path = sourceNode.path + "." + sourceNode.bookmarkId;
        if (!this.totalTreeData[path]) {
          this.totalTreeData[path] = [];
        }
        if (sourceNode.children === undefined) {
          sourceNode.children = this.totalTreeData[path];
        }
        sourceNode.children.push(targetNode);
      }
      if (targetNode.type === 1) {
        this.totalTreeData[targetNode.path + "." + targetNode.bookmarkId] = [];
      }
      targetNode.isLeaf = targetNode.type === 0;
      targetNode.class = "treeNodeItem";
      await this.updateVersion(null);
      await localforage.setItem(TOTAL_TREE_DATA, toStorable(this.totalTreeData));
    },
    /**
     * 删除节点数据
     */
    async deleteData({ pathList, bookmarkIdList }) {
      //待删除的书签
      let bookmarkIdSet = new Set();
      bookmarkIdList.forEach((item) => bookmarkIdSet.add(item));
      //删除子节点
      pathList.forEach((item) => {
        delete this.totalTreeData[item];
        Object.keys(this.totalTreeData)
          .filter((key) => key.startsWith(item + "."))
          .forEach((key) => delete this.totalTreeData[key]);
        bookmarkIdSet.add(parseInt(item.split(".").reverse()));
      });
      //删除直接选中的节点
      Object.keys(this.totalTreeData).forEach((item) => {
        let list = this.totalTreeData[item];
        for (let i = list.length - 1; i >= 0; i--) {
          if (bookmarkIdSet.has(list[i].bookmarkId)) {
            list.splice(i, 1);
          }
        }
      });
      await this.updateVersion(null);
      await localforage.setItem(TOTAL_TREE_DATA, toStorable(this.totalTreeData));
    },
    /**
     * 编辑书签节点
     */
    async editNode({ node, newName, newUrl, newIcon }) {
      node.name = newName;
      node.url = newUrl;
      node.icon = newIcon;
      await this.updateVersion(null);
      await localforage.setItem(TOTAL_TREE_DATA, toStorable(this.totalTreeData));
    },
    /**
     * 检查书签缓存是否最新
     */
    async treeDataCheck(isFirst) {
      if (toastShow || !checkJwtValid(useGlobalConfigStore().token)) {
        return;
      }
      let realVersion = await HttpUtil.get("/user/version");
      if (realVersion !== this.version) {
        if (this.showRefreshToast && !isFirst) {
          const store = this;
          //如果在书签管理页面需要弹窗提示
          Modal.confirm({
            title: "书签数据有更新，是否立即刷新？",
            cancelText: "稍后提醒",
            closable: false,
            keyboard: false,
            maskClosable: false,
            async onOk() {
              toastShow = false;
              await store.refresh();
            },
            onCancel() {
              toastShow = false;
            },
          });
          toastShow = true;
        } else {
          await this.refresh();
        }
      }
    },
    /**
     * 检查本地缓存数据是否有更新
     */
    async checkLocalData() {
      let data = await localforage.getItem(TOTAL_TREE_DATA);
      let version = await localforage.getItem(VERSION);
      if (!data || !version) {
        return;
      }
      if (version > this.version) {
        this.totalTreeData = data;
        this.version = version;
      }
    },
  },
});
