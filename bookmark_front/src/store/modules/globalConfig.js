import localforage from "localforage";
import HttpUtil from "../../util/HttpUtil";
export const GLOBAL_CONFIG = "globalConfig";
export const USER_INFO = "userInfo";
export const TOKEN = "token";
export const SERVER_CONFIG = "serverConfig";
export const SUPPORT_NO_LOGIN = "supportNoLogin";

/**
 * 存储全局配置
 */
const state = {
	/**
	 * 用户信息
	 */
	[USER_INFO]: {},
	/**
	 * token,null说明未获取登录凭证
	 */
	[TOKEN]: null,
	/**
	 * 是否已经初始化完成,避免多次重复初始化
	 */
	isInit: false,
	/**
	 * 是否移动端
	 */
	isPhone: false,
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
	//登陆后的，初始化数据
	async init (context) {
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
	async refreshUserInfo ({ commit }) {
		let userInfo = await HttpUtil.get("/user/currentUserInfo");
		await localforage.setItem(USER_INFO, userInfo);
		commit(USER_INFO, userInfo);
	},
	async setToken ({ commit }, token) {
		await localforage.setItem(TOKEN, token);
		commit(TOKEN, token);
	},
	//登出清除数据
	async clear (context) {
		await localforage.removeItem("userInfo");
		await localforage.removeItem("token");
		context.commit(USER_INFO, null);
		context.commit(TOKEN, null);
		context.commit("isInit", false);
	},
	/**
	 * 从服务器读取全局配置
	 */
	async refreshServerConfig ({ commit }) {
		commit(SERVER_CONFIG, await HttpUtil.get("/common/config/global"));
	}
};

const mutations = {
	userInfo (state, userInfo) {
		state.userInfo = userInfo;
	},
	token (state, token) {
		state.token = token;
	},
	isInit (state, isInit) {
		state.isInit = isInit;
	},
	isPhone (state, status) {
		state.isPhone = status;
	},
	[SERVER_CONFIG] (state, serverConfig) {
		state[SERVER_CONFIG] = serverConfig;
	},
	[SUPPORT_NO_LOGIN] (state, val) {
		state[SUPPORT_NO_LOGIN] = val;
	}
};


export default {
	namespaced: true,
	state,
	getters,
	actions,
	mutations
};
