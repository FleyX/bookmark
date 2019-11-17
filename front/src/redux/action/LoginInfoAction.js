// 定义登录信息在store中的名字
export const DATA_NAME = "loginInfo";

export function getInitData() {
  let token = localStorage.getItem("token");
  window.token = token;
  return {
    token
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
    data: Object.assign({}, userInfo)
  };
};

//更新一个数据
export const UPDATE_ONE = "updateOne";

export const updateOne = (key, value) => {
  let data = {};
  data[key] = value;
  return { type: UPDATE_ONE, data };
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
