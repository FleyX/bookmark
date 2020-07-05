const state = {
  isLogin: 0
};

const getters = {};

const actions = {
  asyncAdd({ commit }, data) {
    return new Promise(resolve => {
      setTimeout(() => {
        commit("setCount", data);
        resolve();
      }, 2000);
    });
  }
};

const mutations = {
  setCount(state1, data) {
    // eslint-disable-next-line no-param-reassign
    state1.count += data;
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
