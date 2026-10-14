import { defineStore } from "pinia";
import HttpUtil from "../util/HttpUtil";

/**
 * 进行中的用户信息请求，避免守卫和登陆初始化并发重复拉取
 */
let loadPromise = null;

export const useUserStore = defineStore("user", {
  state: () => ({
    /**
     * 当前用户信息，含后端下发的resources字段
     */
    userInfo: null,
    /**
     * 当前用户已展开的资源code列表，超级管理员由后端下发全量
     */
    resources: [],
    /**
     * 是否已经初始化完成,避免多次重复初始化
     */
    isInit: false,
  }),
  getters: {
    /**
     * 是否拥有指定资源
     */
    hasResource: (state) => (code) => state.resources.includes(code),
    /**
     * 是否拥有任一管理后台页面资源
     */
    hasAnyAdminResource: (state) => state.resources.some((code) => code.startsWith("VIEW:/admin")),
  },
  actions: {
    //登陆后的，初始化数据
    async loginInit() {
      if (this.isInit) {
        return;
      }
      if (!loadPromise) {
        loadPromise = HttpUtil.get("/user/currentUserInfo")
          .then((data) => {
            this.userInfo = data;
            this.resources = data.resources || [];
            this.isInit = true;
          })
          .finally(() => {
            loadPromise = null;
          });
      }
      return loadPromise;
    },
    clear() {
      this.userInfo = null;
      this.resources = [];
      this.isInit = false;
    },
  },
});
