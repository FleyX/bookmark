/**
 * 登陆/注册/重置密码 通用布局
 */
import React from "react";
import styles from "./index.module.less";
import { Link } from "react-router-dom";

export const LOGIN_TYPE = "login";
export const REGISTER_TYPE = "register";
export const RESET_PASSWORD_TYPE = "reset_password";

/**
 * 纯UI组件，使用单函数形式
 * @param type 当前类别：上面的三个常亮
 * @param children 子组件
 */
const LoginOrRegister = ({ type }) => {
  console.log(type);
  return (
    <div className={styles.head}>
      <Link to="/public/login" className={`${styles.item} ${type === LOGIN_TYPE ? styles.active : ""}`}>
        登录
      </Link>
      <div className={styles.dot} />
      <Link to="/public/register" className={`${styles.item} ${type === REGISTER_TYPE ? styles.active : ""}`}>
        注册
      </Link>
    </div>
  );
};

export function LoginLayout({ type, children }) {
  return (
    <div className={"fullScreen " + styles.main}>
      <div className={styles.LOGO}>
        <img src="/img/bookmarkLogo.png" alt="logo" />
      </div>
      <div className={styles.content}>
        {type === RESET_PASSWORD_TYPE ? <div style={{ fontSize: "0.3rem" }}>重置密码</div> : <LoginOrRegister type={type} />}
        {children}
        {type === RESET_PASSWORD_TYPE ? <Link to="/public/login">返回登录注册</Link> : null}
      </div>
    </div>
  );
}
