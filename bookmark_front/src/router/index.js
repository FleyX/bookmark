import Vue from "vue";
import VueRouter from "vue-router";
import Main from "../views/main/Main.vue";
import UserInfo from "../views/main/pages/personSpace/UserInfo.vue";
import BookmarkTree from "../views/main/pages/things/BookmarkTree.vue";

import Public from "../views/public/Public.vue";
import Login from "../views/public/pages/Login.vue";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Main",
    component: Main,
    children: [
      {
        path: "",
        name: "BookmarkTree",
        component: BookmarkTree
      },
      {
        path: "personSpakce/userInfo",
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
      }
    ]
  }
];

const router = new VueRouter({
  routes
});

export default router;
