import React from "react";
import IconFont from "../../../components/IconFont";
import { Button, Input, message } from "antd";
import {
  LoginLayout,
  REGISTER_TYPE,
  RESET_PASSWORD_TYPE
} from "../../../layout/LoginLayout";
import styles from "./index.module.less";
import axios from "../../../util/httpUtil";

const pattern = {
  username: {
    pattern: /^[\da-zA-Z]{1,10}$/,
    text: "用户名为1-10位的数字或者字母的组合"
  },
  email: {
    pattern: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    text: "请输入合法的邮箱"
  },
  password: {
    pattern: /^(?=.*?\d+.*?)(?=.*?[a-zA-Z]+.*?)[\da-zA-Z]{6,20}$/,
    text: "密码为6-20位数字字母组合"
  },
  authCode: {
    pattern: /^[\d]{6,6}$/,
    text: "验证码为6位数字"
  }
};

export default class Register extends React.Component {
  constructor(props) {
    super(props);
    console.log(props);
    let type = props.location.pathname.split("/").reverse()[0];
    this.state = {
      current: type === "register" ? REGISTER_TYPE : RESET_PASSWORD_TYPE,
      username: "",
      email: "",
      password: "",
      repassword: "",
      authCode: "",
      authCodeText: "获取验证码",
      isCountDown: false,
      errorText: "",
      isLoading: false
    };
  }

  componentWillUnmount() {
    if (this.timer != null) {
      clearInterval(this.timer);
    }
  }

  /**
   * 获取验证码
   */
  getCode = () => {
    if (this.state.isCountDown) {
      return;
    }
    let count = 60;
    this.setState({ authCodeText: `${count}s后重试`, isCountDown: true });
    this.timer = setInterval(() => {
      count--;
      if (count === 0) {
        this.setState({ isCountDown: false, authCodeText: "获取验证码" });
        clearInterval(this.timer);
        return;
      }
      this.setState({ authCodeText: `${count}s后重试` });
    }, 1000);
    axios.get("/user/authCode?email=" + this.state.email).then(() => {
      message.success("发送成功，请注意查收（检查垃圾箱）");
    });
  };

  changeData = e => {
    const name = e.target.name;
    const value = e.target.value.trim();
    this.setState({ [name]: value });
    this.checkParam(name, value);
  };

  /**
   * 字段校验
   * @param {*} key key
   * @param {*} value 值
   */
  checkParam(key, value) {
    const { password } = this.state;
    if (key === "repassword") {
      if (password !== value) {
        this.setState({ errorText: "两次密码不一致" });
        return false;
      } else {
        this.setState({ errorText: "" });
        return true;
      }
    }
    const rule = pattern[key];
    if (rule.pattern.test(value)) {
      this.setState({ errorText: "" });
      return true;
    } else {
      this.setState({ errorText: rule.text });
      return false;
    }
  }

  /**
   * 提交表单
   */
  submit = () => {
    const {
      current,
      username,
      email,
      password,
      repassword,
      authCode
    } = this.state;
    let form = { username, email, password, authCode };
    if (current === REGISTER_TYPE && !this.checkParam("username", username)) {
      return;
    }
    if (
      current === RESET_PASSWORD_TYPE &&
      !this.checkParam("authCode", authCode)
    ) {
      return;
    }
    const isOk =
      this.checkParam("email", email) &&
      this.checkParam("password", password) &&
      this.checkParam("repassword", repassword);
    if (!isOk) {
      return;
    }
    this.setState({ isLoading: true });
    if (current === REGISTER_TYPE) {
      axios
        .put("/user", form)
        .then(() => {
          message.success("注册成功");
          this.setState({ isLoading: false });
          setTimeout(() => this.props.history.replace("/public/login"), 500);
        })
        .catch(() => this.setState({ isLoading: false }));
    } else {
      delete form.username;
      axios
        .post("/user/resetPassword", form)
        .then(() => {
          message.success("操作成功");
          this.setState({ isLoading: false });
          setTimeout(() => this.props.history.replace("/public/login"), 500);
        })
        .catch(() => this.setState({ isLoading: false }));
    }
  };

  render() {
    const {
      current,
      username,
      email,
      password,
      repassword,
      authCode,
      authCodeText,
      isCountDown,
      errorText,
      isLoading
    } = this.state;
    return (
      <LoginLayout type={current}>
        <div className={styles.main}>
          <div className={styles.errorText}>{errorText}</div>
          {current === REGISTER_TYPE ? (
            <Input
              type="text"
              size="large"
              name="username"
              value={username}
              onChange={this.changeData}
              addonBefore={
                <IconFont type="icon-person" style={{ fontSize: "0.3rem" }} />
              }
              placeholder="用户名"
            />
          ) : null}
          <Input
            type="email"
            size="large"
            name="email"
            value={email}
            onChange={this.changeData}
            addonBefore={
              <IconFont type="icon-mail" style={{ fontSize: "0.3rem" }} />
            }
            placeholder="邮箱"
          />
          <Input
            type="password"
            size="large"
            name="password"
            value={password}
            onChange={this.changeData}
            addonBefore={
              <IconFont type="icon-password" style={{ fontSize: "0.3rem" }} />
            }
            placeholder="密码"
          />
          <Input
            type="password"
            size="large"
            name="repassword"
            value={repassword}
            onChange={this.changeData}
            addonBefore={
              <IconFont type="icon-password" style={{ fontSize: "0.3rem" }} />
            }
            placeholder="重复密码"
          />
          {current === RESET_PASSWORD_TYPE ? (
            <Input
              type="text"
              size="large"
              name="authCode"
              value={authCode}
              onChange={this.changeData}
              addonBefore={
                <IconFont
                  type="icon-yanzhengma54"
                  style={{ fontSize: "0.3rem" }}
                />
              }
              addonAfter={
                <span
                  style={{ cursor: isCountDown ? "" : "pointer" }}
                  onClick={this.getCode}
                >
                  {authCodeText}
                </span>
              }
              placeholder="验证码"
            />
          ) : null}
          <Button
            type="primary"
            className={styles.submit}
            onClick={this.submit}
            block
            loading={isLoading}
          >
            {current === REGISTER_TYPE ? "注册" : "重置密码"}
          </Button>
        </div>
      </LoginLayout>
    );
  }
}
