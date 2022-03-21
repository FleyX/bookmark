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
	await store.dispatch(treeData.TREE_DATA + "/" + treeData.noLoginInit);
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
		await store.dispatch(globalConfig.GLOBAL_CONFIG + "/" + globalConfig.loginInit);
		await store.dispatch(treeData.TREE_DATA + "/" + treeData.loginInit);
	}
}

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

