import * as loginAction from "../action/LoginInfoAction.js";

const LoginStatusReducer = (state = loginAction.getInitData(), action) => {
  switch (action.type) {
    case loginAction.CHANGE_TOKEN:
    case loginAction.CHANGE_USER_INFO:
    case loginAction.UPDATE_ONE:
    case loginAction.LOGOUT:
      return { ...state, ...action.data };
    default:
      return state;
  }
};

export default LoginStatusReducer;
