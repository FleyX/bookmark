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
