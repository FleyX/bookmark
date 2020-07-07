/**
 * 存储全局配置
 */
const state = {
  /**
   * 是否处于登录、注册页
   */
  isLogReg: false
};

const getters = {};

const actions = {};

const mutations = {
  setCount(oState, isLogReg) {
    // eslint-disable-next-line no-param-reassign
    oState.isLogReg = isLogReg;
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
