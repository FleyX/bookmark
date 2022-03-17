import Vue from "vue";
import VueRouter from "vue-router";

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
			{ path: "/login", component: () => import("@/views/public/login/index") },
			{ path: "/register", component: () => import("@/views/public/register/index") },
			{ path: "/resetPassword", component: () => import("@/views/public/passwordReset/index") },
			{ path: "/oauth/github", component: () => import("@/views/public/oauth/github/index") },
			{ path: "/404", component: () => import("@/views/public/notFound/index") }
		]
	},
	{ path: "*", redirect: "/public/404" }
];

const router = new VueRouter({
	mode: "history",
	routes
});

export default router;
