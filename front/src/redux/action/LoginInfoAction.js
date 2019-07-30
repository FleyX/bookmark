// 定义登录信息在store中的名字
export const DATA_NAME = "loginInfo";

export function getInitData() {
  let token = localStorage.getItem("token");
  window.token = token;
  return {
    token,
    userInfo: null
  };
}

//定义修改token
export const CHANGE_TOKEN = "changeToken";

export const changeToken = token => {
  localStorage.setItem("token", token);
  window.token = token;
  return {
    type: CHANGE_TOKEN,
    data: {
      token
    }
  };
};

//定义修改userInfo
export const CHANGE_USER_INFO = "changeUserInfo";

export const changeUserInfo = userInfo => {
  return {
    type: CHANGE_USER_INFO,
    data: {
      userInfo
    }
  };
};

// 退出登录
export const LOGOUT = "logout";

export const logout = () => {
  delete window.token;
  localStorage.removeItem("token");
  return {
    type: LOGOUT,
    data: {
      token: null,
      userInfo: null
    }
  };
};
