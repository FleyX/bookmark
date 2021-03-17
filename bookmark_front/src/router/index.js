import Vue from "vue";
import VueRouter from "vue-router";
import Main from "../views/main/Main.vue";
import UserInfo from "../views/main/pages/personSpace/UserInfo.vue";
import BookmarkManage from "../views/main/pages/things/BookmarkManage.vue";

import Public from "../views/public/Public.vue";
import Login from "../views/public/pages/Login.vue";
import Register from "../views/public/pages/Register.vue";
import ResetPassword from "../views/public/pages/ResetPassword.vue";
import GithubOauth from "../views/public/pages/oauth/Github.vue";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    component: Main,
    children: [
      {
        path: "",
        name: "BookmarkManage",
        component: BookmarkManage
      },
      {
        path: "personSpace/userInfo",
        name: "UserInfo",
        component: UserInfo
      }
    ]
  },
  {
    path: "/public",
    name: "Public",
    component: Public,
    children: [
      {
        path: "login",
        name: "Login",
        component: Login
      },
      {
        path: "register",
        name: "Register",
        component: Register
      },
      {
        path: "resetPassword",
        name: "ResetPassword",
        component: ResetPassword
      },
      {
        path: "oauth/github",
        name: "GithubRedirect",
        component: GithubOauth
      },
      {
        path: "notFound",
        name: "NOTFOUND",
        component: () => import("@/views/public/pages/NotFound")
      }
    ]
  },
  {
    path: "*",
    redirect: "/public/notFound"
  }
];

const router = new VueRouter({
  mode: "history",
  routes
});

export default router;
