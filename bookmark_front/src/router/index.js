import Vue from "vue";
import VueRouter from "vue-router";
import * as vuex from "../store/index.js";
import { GLOBAL_CONFIG, SUPPORT_NO_LOGIN, TOKEN } from "@/store/modules/globalConfig";
import { checkJwtValid } from "@/util/UserUtil";

Vue.use(VueRouter);

const routes = [
  { path: "/", component: () => import("@/views/home/index") },
  {
    path: "/manage",
    component: () => import("@/views/manage/index"),
    children: [
      { path: "", redirect: "/manage/bookmarkTree" },
      { path: "bookmarkTree", component: () => import("@/views/manage/bookmarkTree/index") },
      { path: "personSpace/userInfo", component: () => import("@/views/manage/personSpace/index") },
      { path: "sso", component: () => import("@/views/manage/sso/index") }
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
      { path: "about", component: () => import("@/views/public/about/index") },
      { path: "404", component: () => import("@/views/public/notFound/index") }
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
router.beforeEach(async (to, from, next) => {
  //进入主页面/管理页面时，确认已经进行初始化操作
  if (to.path === "/" || to.path.startsWith("/manage")) {
    await vuex.loginInit();
  }
  let supportNoLogin = to.path === "/" || to.path.startsWith("/public");
  vuex.default.commit(GLOBAL_CONFIG + "/" + SUPPORT_NO_LOGIN, supportNoLogin);
  if (!supportNoLogin && !checkJwtValid(vuex.default.state[GLOBAL_CONFIG][TOKEN])) {
    //如不支持未登录进入，切jwt已过期，直接跳转到登录页面,并清理缓存
    await vuex.default.dispatch("treeData/clear");
    await vuex.default.dispatch("globalConfig/clear");
    next({
      path: "/public/login?to=" + btoa(location.href),
      replace: true
    });
  } else {
    next();
  }
});

export default router;
