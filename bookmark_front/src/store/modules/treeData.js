import localforage from "localforage";
import { checkJwtValid } from "@/util/UserUtil";
import HttpUtil from "../../util/HttpUtil";

export const TREE_DATA = "treeData";
export const TOTAL_TREE_DATA = "totalTreeData";
export const VERSION = "version";
/** 是否展示刷新书签数据弹窗 */
export const SHOW_REFRESH_TOAST = "showRefreshToast";
export const IS_INIT = "isInit";
export const IS_INITING = "isIniting";
/** 首页固定的书签 */
export const HOME_PIN_LIST = "homePinList";
/** 首页固定书签id Map */
export const HOME_PIN_BOOKMARK_ID_MAP = "homePinBookmarkIdMap";
/** 刷新首页固定标签*/
export const refreshHomePinList = "refreshHomePinList";
/**
 * 通过id获取书签数据
 */
export const getById = "getById";
export const noLoginInit = "noLoginInit";
export const loginInit = "loginInit";
export const refresh = "refresh";
export const clear = "clear";
/**
 * 删除书签数据
 */
export const deleteData = "deleteData";

/**
 * 版本检查定时调度
 */
let timer = null;
/**
 * 刷新书签确认弹窗是否展示
 */
let toastShow = false;

/**
 * 书签树相关配置
 */
const state = {
	//全部书签数据
	[TOTAL_TREE_DATA]: {},
	//版本
	[VERSION]: null,
	//是否已经初始化书签数据
	[IS_INIT]: false,
	// 是否正在加载数据
	[IS_INITING]: false,
	[SHOW_REFRESH_TOAST]: false,
	[HOME_PIN_LIST]: [],
	[HOME_PIN_BOOKMARK_ID_MAP]: {}
};

const getters = {
	[getById]: state => id => {
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
	async [noLoginInit] () {

	},
	async [loginInit] (context) {
		if (context.state.isInit || context.state.isIniting) {
			return;
		}
		await context.dispatch(refreshHomePinList);
		context.commit(IS_INITING, true);
		context.commit(TOTAL_TREE_DATA, await localforage.getItem(TOTAL_TREE_DATA));
		context.commit(VERSION, await localforage.getItem(VERSION));
		await treeDataCheck(context, true);
		context.commit(IS_INIT, true);
		context.commit(IS_INITING, false);
		timer = setInterval(() => treeDataCheck(context, false), 5 * 60 * 1000);
		// timer = setInterval(() => treeDataCheck(context, false), 5 * 1000);
	},
	/**
	 * 确保数据加载完毕
	 */
	ensureDataOk (context) {
		return new Promise((resolve, reject) => {
			let timer = setInterval(() => {
				try {
					if (context.state[IS_INIT] && context.state[IS_INITING] == false) {
						clearInterval(timer);
						resolve();
					}
				} catch (err) {
					reject(err);
				}
			}, 50);
		});
	},
	//刷新缓存数据
	async [refresh] (context) {
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
	async [clear] (context) {
		context.commit(TOTAL_TREE_DATA, null);
		context.commit(VERSION, null);
		context.commit(SHOW_REFRESH_TOAST, false);
		context.commit(IS_INIT, false);
		context.commit(IS_INITING, false);
		context.commit(HOME_PIN_LIST, []);
		if (timer != null) {
			clearInterval(timer);
		}
		await localforage.removeItem(TOTAL_TREE_DATA);
		await localforage.removeItem(VERSION);
	},
	/**
	 * 移动节点
	 */
	async moveNode (context, info) {
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
	async [refreshHomePinList] ({ commit }) {
		let list = await HttpUtil.get("/home/pin");
		commit(HOME_PIN_LIST, list);
		let map = {};
		list.filter(item => item.id).forEach(item => map[item.bookmarkId] = true);
		commit(HOME_PIN_BOOKMARK_ID_MAP, map);

	},
	/**
	 * 更新版本数据
	 */
	async updateVersion ({ commit, state }, version) {
		commit(VERSION, version == null ? state[VERSION] + 1 : version);
		await localforage.setItem(VERSION, state[VERSION]);
	},
	/**
	 * 新增书签、文件夹
	 */
	async addNode (context, { sourceNode, targetNode }) {
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
	async [deleteData] (context, { pathList, bookmarkIdList }) {
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
	async editNode ({ dispatch, state, commit }, { node, newName, newUrl, newIcon }) {
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
	[IS_INIT] (state, isInit) {
		state.isInit = isInit;
	},
	[IS_INITING] (state, isIniting) {
		state.isIniting = isIniting;
	},
	[VERSION]: (state, version) => {
		state[VERSION] = version;
	},
	[SHOW_REFRESH_TOAST]: (state, val) => {
		state[SHOW_REFRESH_TOAST] = val;
	},
	[HOME_PIN_LIST]: (state, val) => {
		state[HOME_PIN_LIST] = val;
	},
	[HOME_PIN_BOOKMARK_ID_MAP]: (state, val) => {
		state[HOME_PIN_BOOKMARK_ID_MAP] = val;
	}
};


/**
 * 检查书签缓存是否最新
 * 
 * @param {*} context 
 * @param {*} isFirst 
 * @returns 
 */
async function treeDataCheck (context, isFirst) {
	if (toastShow || !checkJwtValid(context.rootState.globalConfig.token)) {
		return;
	}
	let realVersion = await HttpUtil.get("/user/version");
	if (realVersion !== context.state[VERSION]) {
		if (SHOW_REFRESH_TOAST && !isFirst) {
			//如果在书签管理页面需要弹窗提示
			window.vueInstance.$confirm({
				title: "书签数据有更新，是否立即刷新？",
				cancelText: "稍后提醒",
				closable: false,
				keyboard: false,
				maskClosable: false,
				onOk () {
					toastShow = false;
					return new Promise(async (resolve) => {
						await context.dispatch("refresh");
						resolve();
					});
				},
				onCancel () {
					toastShow = false;
				}
			});
			toastShow = true;
		} else {
			await context.dispatch(refresh);
		}
	}
}

export const store = {
	namespaced: true,
	state,
	getters,
	actions,
	mutations
};

