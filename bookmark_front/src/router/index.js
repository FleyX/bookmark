import Vue from "vue";
import VueRouter from "vue-router";
import vuex from "../store/index.js";
import { GLOBAL_CONFIG, SUPPORT_NO_LOGIN, TOKEN } from "@/store/modules/globalConfig";

Vue.use(VueRouter);

const routes = [
	{ path: "/", component: () => import("@/views/home/index") },
	{
		path: "/manage",
		component: () => import("@/views/manage/index"),
		children: [
			{ path: "/bookmarkTree", component: () => import("@/views/manage/bookmarkTree/index") },
			{ path: "personSpace/userInfo", component: () => import("@/views/manage/personSpace/index") },
		]
	},
	{
		path: "/public",
		component: () => import("@/views/public/index"),
		children: [
			{ path: "login", component: () => import("@/views/public/login/index") },
			{ path: "register", component: () => import("@/views/public/register/index") },
			{ path: "resetPassword", component: () => import("@/views/public/passwordReset/index") },
			{ path: "oauth/github", component: () => import("@/views/public/oauth/github/index") },
			{ path: "404", component: () => import("@/views/public/notFound/index") },
		]
	},
	{ path: "*", redirect: "/public/404" }
];

const router = new VueRouter({
	mode: "history",
	routes
});

/**
 * 在此进行登录信息判断，以及重定向到登录页面
 */
router.beforeEach((to, from, next) => {
	let supportNoLogin = to.path === '/' || to.path.startsWith("/public");
	vuex.commit(GLOBAL_CONFIG + "/" + SUPPORT_NO_LOGIN, supportNoLogin);
	if (!supportNoLogin && !checkJwtValid()) {
		//如不支持未登录进入，切jwt已过期，直接跳转到登录页面
		next({
			path: "/public/login?to=" + btoa(location.href),
			replace: true
		});
	} else {
		next();
	}
})

/**
 * 检查jwt是否有效
 */
function checkJwtValid () {
	let token = vuex.state[GLOBAL_CONFIG][TOKEN];
	try {
		if (token && token.trim().length > 0) {
			//检查token是否还有效
			let content = window.atob(token.split(".")[1]);
			if (content.exp > Date.now() / 1000) {
				return true;
			}
		}
	} catch (err) {
		console.error(err);
	}
	return false;
}

export default router;
