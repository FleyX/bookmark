import React from "react";
import IconFont from "../../../components/IconFont";
import { Button, Input, Checkbox } from "antd";
import { LoginLayout, REGISTER_TYPE, RESET_PASSWORD_TYPE } from "../../../layout/LoginLayout";
import styles from "./index.module.less";
import { Link } from "react-router-dom";
import axios from "../../../util/httpUtil";

export default class Register extends React.Component {
  constructor(props) {
    super(props);
    let type = props.location.pathname.split("/").reverse()[0];
    this.state = {
      current: type === "register" ? REGISTER_TYPE : RESET_PASSWORD_TYPE,
      email: "",
      password: "",
      repassword: "",
      code: ""
    };
  }

  /**
   * 获取验证码
   */
  getCode = () => {};

  changeData = e => {
    this.setState({ [e.target.name]: e.target.value });
  };

  submit = () => {};

  render() {
    const { current, email, password, repassword, code } = this.state;
    return (
      <LoginLayout type={current}>
        <div className={styles.main}>
          <Input type="text" size="large" name="email" value={email} onChange={this.changeData} addonBefore={<IconFont type="icon-mail" style={{ fontSize: "0.3rem" }} />} placeholder="邮箱" />
          <Input type="password" size="large" name="password" value={password} onChange={this.changeData} addonBefore={<IconFont type="icon-password" style={{ fontSize: "0.3rem" }} />} placeholder="密码" />
          <Input type="password" size="large" name="repassword" value={repassword} onChange={this.changeData} addonBefore={<IconFont type="icon-password" style={{ fontSize: "0.3rem" }} />} placeholder="重复密码" />
          <Input
            type="text"
            size="large"
            name="code"
            value={code}
            onChange={this.changeData}
            addonBefore={<IconFont type="icon-yanzhengma54" style={{ fontSize: "0.3rem" }} />}
            addonAfter={
              <span style={{ cursor: "pointer" }} onClick={this.getCode}>
                获取验证码
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
