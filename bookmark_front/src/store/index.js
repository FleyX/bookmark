import Vue from "vue";
import Vuex from "vuex";
import * as globalConfig from "./modules/globalConfig";
import * as treeData from "./modules/treeData";

import { checkJwtValid } from "@/util/UserUtil";

Vue.use(Vuex);

let store = new Vuex.Store({
	state: {},
	mutations: {},
	actions: {},
	modules: {
		[globalConfig.GLOBAL_CONFIG]: globalConfig.store,
		[treeData.TREE_DATA]: treeData.store
	}
});

let noLoginFinish = false;

//执行各自的非登陆初始化
(async () => {
	await store.dispatch(globalConfig.GLOBAL_CONFIG + "/" + globalConfig.noLoginInit);
	//无需等待执行
	store.dispatch(treeData.TREE_DATA + "/" + treeData.noLoginInit);
	noLoginFinish = true;
})();

/**
 * 执行各模块的登陆后初始化
 */
export async function loginInit () {
	if (!noLoginFinish) {
		await finishNoLogin();
	}
	console.log(store.state[globalConfig.GLOBAL_CONFIG][globalConfig.TOKEN]);
	if (checkJwtValid(store.state[globalConfig.GLOBAL_CONFIG][globalConfig.TOKEN])) {
		//无需等待执行完毕
		store.dispatch(globalConfig.GLOBAL_CONFIG + "/" + globalConfig.loginInit);
		store.dispatch(treeData.TREE_DATA + "/" + treeData.loginInit);
	}
}

/**
 * 推出登陆时需要清理的
 */
export async function logoutClear () {
	await store.dispatch(globalConfig.GLOBAL_CONFIG + "/" + globalConfig.clear);
	await store.dispatch(treeData.TREE_DATA + "/" + treeData.clear);
}

/**
 * 确保未登录前要初始化的初始化完了 
 */
async function finishNoLogin () {
	return new Promise((resolve) => {
		let timer = setInterval(() => {
			if (noLoginFinish) {
				clearInterval(timer);
				resolve();
			}
		}, 100);
	})
}

export default store;

