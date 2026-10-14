import { createRouter, createWebHistory } from "vue-router";
import { pinia, loginInit } from "../store/index";
import { useGlobalConfigStore } from "../store/globalConfig";
import { useTreeDataStore } from "../store/treeData";
import { checkJwtValid } from "@/util/UserUtil";

const routes = [
  { path: "/", component: () => import("@/views/home/index.vue") },
  { path: "/noHead/addBookmark", component: () => import("@/views/noHead/addBookmark/index.vue") },
  {
    path: "/manage",
    component: () => import("@/views/manage/index.vue"),
    children: [
      { path: "", redirect: "/manage/bookmarkTree" },
      { path: "bookmarkTree", component: () => import("@/views/manage/bookmarkTree/index.vue") },
      { path: "personSpace/userInfo", component: () => import("@/views/manage/personSpace/index.vue") },
      { path: "sso/auth", component: () => import("@/views/manage/sso/auth/index.vue") },
    ],
  },
  {
    path: "/public",
    component: () => import("@/views/public/index.vue"),
    children: [
      { path: "login", component: () => import("@/views/public/login/index.vue") },
      { path: "register", component: () => import("@/views/public/register/index.vue") },
      { path: "resetPassword", component: () => import("@/views/public/passwordReset/index.vue") },
      { path: "oauth/github", component: () => import("@/views/public/oauth/github/index.vue") },
      { path: "about", component: () => import("@/views/public/about/index.vue") },
      { path: "404", component: () => import("@/views/public/notFound/index.vue") },
    ],
  },
  { path: "/:pathMatch(.*)*", redirect: "/public/404" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

/**
 * 在此进行登录信息判断，以及重定向到登录页面
 */
router.beforeEach(async (to, from, next) => {
  const globalConfig = useGlobalConfigStore(pinia);
  const treeData = useTreeDataStore(pinia);
  if (to.query.token && checkJwtValid(to.query.token)) {
    console.log("获取到页面token", to.query.token);
    await globalConfig.setToken(to.query.token);
  }
  //进入除/public以外的路由，确认已经进行初始化操作
  if (!to.path.startsWith("/public")) {
    await loginInit();
  }
  let supportNoLogin = to.path === "/" || to.path.startsWith("/public");
  globalConfig.supportNoLogin = supportNoLogin;
  if (!supportNoLogin && !checkJwtValid(globalConfig.token)) {
    //如不支持未登录进入，切jwt已过期，直接跳转到登录页面,并清理缓存
    await treeData.clear();
    await globalConfig.clear();
    next({
      path: "/public/login",
      query: { to: btoa(to.fullPath) },
      replace: true,
    });
  } else {
    next();
  }
});

export default router;
