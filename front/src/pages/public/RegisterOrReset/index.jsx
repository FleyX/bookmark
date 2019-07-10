import React from "react";
import IconFont from "../../../components/IconFont";
import { Button, Input, message } from "antd";
import { LoginLayout, REGISTER_TYPE, RESET_PASSWORD_TYPE } from "../../../layout/LoginLayout";
import styles from "./index.module.less";
import axios from "../../../util/httpUtil";

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
      isCountDown: false
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
    axios.get("/user/authCode?email=" + this.state.email).then(() => {
      message.success("发送成功，请注意查收（检查垃圾箱）");
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
    });
  };

  changeData = e => {
    this.setState({ [e.target.name]: e.target.value });
  };

  /**
   * 提交表单
   */
  submit = () => {
    const { current, username, email, password, repassword, authCode } = this.state;
    if (password !== repassword) {
      message.error("两次密码不一致");
      return;
    }
    let form = { username, email, password, authCode };
    if (current === REGISTER_TYPE) {
      axios
        .put("/user", form)
        .then(() => {
          message.success("注册成功");
          setTimeout(() => this.props.history.replace("/public/login"), 500);
        })
        .catch(error => message.error(error));
    } else {
      delete form.username;
      axios
        .post("/user/resetPassword", form)
        .then(() => {
          message.success("操作成功");
          console.log(this.location);
          setTimeout(() => this.props.history.replace("/public/login"), 500);
        })
        .catch(error => message.error(error));
    }
  };

  render() {
    const { current, username, email, password, repassword, authCode, authCodeText, isCountDown } = this.state;
    return (
      <LoginLayout type={current}>
        <div className={styles.main}>
          {current === REGISTER_TYPE ? (
            <Input
              type="text"
              size="large"
              name="username"
              value={username}
              onChange={this.changeData}
              addonBefore={<IconFont type="icon-person" style={{ fontSize: "0.3rem" }} />}
              placeholder="用户名"
            />
          ) : null}
          <Input
            type="email"
            size="large"
            name="email"
            value={email}
            onChange={this.changeData}
            addonBefore={<IconFont type="icon-mail" style={{ fontSize: "0.3rem" }} />}
            placeholder="邮箱"
          />
          <Input
            type="password"
            size="large"
            name="password"
            value={password}
            onChange={this.changeData}
            addonBefore={<IconFont type="icon-password" style={{ fontSize: "0.3rem" }} />}
            placeholder="密码"
          />
          <Input
            type="password"
            size="large"
            name="repassword"
            value={repassword}
            onChange={this.changeData}
            addonBefore={<IconFont type="icon-password" style={{ fontSize: "0.3rem" }} />}
            placeholder="重复密码"
          />
          <Input
            type="text"
            size="large"
            name="authCode"
            value={authCode}
            onChange={this.changeData}
            addonBefore={<IconFont type="icon-yanzhengma54" style={{ fontSize: "0.3rem" }} />}
            addonAfter={
              <span style={{ cursor: isCountDown ? "" : "pointer" }} onClick={this.getCode}>
                {authCodeText}
              </span>
            }
            placeholder="验证码"
          />
          <Button type="primary" className={styles.submit} onClick={this.submit} block>
            {current === REGISTER_TYPE ? "注册" : "重置密码"}
          </Button>
        </div>
      </LoginLayout>
    );
  }
}
