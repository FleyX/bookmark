import Vue from "vue";
import Vuex from "vuex";
import globalConfig from "./modules/globalConfig";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {},
  mutations: {},
  actions: {},
  modules: {
    globalConfig
  }
});
