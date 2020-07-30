import Vue from "vue";
import Vuex from "vuex";
import globalConfig from "./modules/globalConfig";
import treeData from "./modules/treeData";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {},
  mutations: {},
  actions: {},
  modules: {
    globalConfig,
    treeData
  }
});
