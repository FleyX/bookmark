import * as types from "../type";

const state = {
  [types.COUNT]: 0
};

const getters = {};

const actions = {
  asyncAdd({ commit }, data) {
    return new Promise(resolve => {
      setTimeout(() => {
        commit(types.TEST, data);
        resolve();
      }, 2000);
    });
  }
};

const mutations = {
  [types.COUNT](state1, data) {
    // eslint-disable-next-line no-param-reassign
    state1[types.COUNT] += data;
  }
};

module.exports = {
  state,
  getters,
  actions,
  mutations
};
