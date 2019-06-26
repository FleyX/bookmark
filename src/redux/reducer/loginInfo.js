import * as loginAction from "../action/LoginInfoAction";

const initData = {
  token: null,
  userInfo: null
};

const LoginStatusReducer = (state = initData, action) => {
  switch (action.type) {
    case loginAction.CHANGE_LOGIN_INFO:
      return { ...action.data };
    default:
      return state;
  }
};

export default LoginStatusReducer;
