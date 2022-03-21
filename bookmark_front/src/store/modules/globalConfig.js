import localforage from "localforage";
import HttpUtil from "../../util/HttpUtil";

export const GLOBAL_CONFIG = "globalConfig";
export const USER_INFO = "userInfo";
export const TOKEN = "token";
export const SERVER_CONFIG = "serverConfig";
export const SUPPORT_NO_LOGIN = "supportNoLogin";
export const IS_INIT = "isInit";

export const noLoginInit = "noLoginInit";
export const loginInit = "loginInit";
/**
 * 存储全局配置
 */
const state = {
	/**
	 * 用户信息
	 */
	[USER_INFO]: null,
	/**
	 * token,null说明未获取登录凭证
	 */
	[TOKEN]: null,
	/**
	 * 是否已经初始化完成,避免多次重复初始化
	 */
	[IS_INIT]: false,
	/**
	 * 是否移动端
	 */
	isPhone: /Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent),
	/**
	 * 是否支持未登录进入页面
	 */
	[SUPPORT_NO_LOGIN]: false,
	/**
	 * 服务端全局配置
	 */
	[SERVER_CONFIG]: {}
};

const getters = {};

const actions = {
	//未登录需要进行的初始化
	async [noLoginInit] ({ commit }) {
		commit(SERVER_CONFIG, await HttpUtil.get("/common/config/global"));
		let token = await localforage.getItem(TOKEN);
		if (token) {
			commit(TOKEN, token);
			window.jwtToken = token;
		}
	},
	//登陆后的，初始化数据
	async [loginInit] (context) {
		if (context.state.isInit) {
			return;
		}
		let userInfo = await HttpUtil.get("/user/currentUserInfo");
		context.commit(USER_INFO, userInfo);
		context.commit(IS_INIT, true);
	},
	async setToken ({ commit }, token) {
		await localforage.setItem(TOKEN, token);
		window.jwtToken = token;
		commit(TOKEN, token);
	},
	//登出清除数据
	async clear (context) {
		await localforage.removeItem(TOKEN);
		context.commit(USER_INFO, null);
		context.commit(TOKEN, null);
		context.commit(IS_INIT, false);
	},
};

const mutations = {
	[USER_INFO] (state, userInfo) {
		state[USER_INFO] = userInfo;
	},
	[TOKEN] (state, token) {
		state[TOKEN] = token;
	},
	[IS_INIT] (state, isInit) {
		state[IS_INIT] = isInit;
	},
	[SERVER_CONFIG] (state, serverConfig) {
		state[SERVER_CONFIG] = serverConfig;
	},
	[SUPPORT_NO_LOGIN] (state, val) {
		state[SUPPORT_NO_LOGIN] = val;
	}
};


export const store = {
	namespaced: true,
	state,
	getters,
	actions,
	mutations
};
