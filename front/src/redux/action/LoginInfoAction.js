// 定义登录信息在store中的名字
export const DATA_NAME = "loginInfo";

//定义修改loginInfo type
export const CHANGE_LOGIN_INFO = "changeLoginStatus";

export const changeLoginInfo = (token, userInfo) => {
  return {
    type: CHANGE_LOGIN_INFO,
    data: {
      token,
      userInfo
    }
  };
};
