import * as loginAction from "../action/LoginInfoAction.js";

function getInitData() {
  let token, userInfo;
  try {
    token = localStorage.getItem("token");
    userInfo = JSON.parse(localStorage.getItem("userInfo"));
  } catch (e) {
    console.error(e);
    token = null;
    userInfo = null;
  }
  window.token = token;
  window.userInfo = userInfo;
  return {
    token,
    userInfo
  };
}

const LoginStatusReducer = (state = getInitData(), action) => {
  switch (action.type) {
    case loginAction.CHANGE_LOGIN_INFO:
      return { ...state, ...action.data };
    default:
      return state;
  }
};

export default LoginStatusReducer;
