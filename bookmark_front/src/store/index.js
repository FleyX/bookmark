import Vue from "vue";
import Vuex from "vuex";
import count from "./modules/count";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {},
  mutations: {},
  actions: {},
  modules: {
    count
  }
});
