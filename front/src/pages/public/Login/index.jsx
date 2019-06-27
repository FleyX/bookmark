import React, { Component } from "react";
import { Button, Input, message } from "antd";
import IconFont from "../../../components/IconFont";
import styles from "./index.module.less";
import { connect } from "react-redux";
import { changeLoginInfo } from "../../../redux/action/LoginInfoAction";
import axios from "../../../util/httpUtil";

function mapStateToProps(state) {
  return {};
}

function mapDispatchToProps(dispatch) {
  return {
    updateLoginInfo: (token, userInfo) => dispatch(changeLoginInfo(token, userInfo))
  };
}

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password: ""
    };
  }

  usernameInput = e => {
    this.setState({ username: e.target.value });
  };
  passwordInput = e => {
    this.setState({ password: e.target.value });
  };

  submit = () => {
    axios.post("/public/login", this.state).then(res => {
      localStorage.setItem("token", res.token);
      localStorage.setItem("userInfo", JSON.stringify(res.userInfo));
      window.token = res.token;
      window.userInfo = res.userInfo;
      message.success("登录成功");
      this.props.updateLoginInfo(res.token, res.userInfo);
      this.props.history.replace("/");
    });
  };

  render() {
    return (
      <div className="fullScreen flex main-center across-center">
        <div className={styles.main}>
          <div className={styles.top}>
            <IconFont type="icon-LC_icon_chat_fill" />
            <span>FIM</span>
          </div>
          <div className={styles.bottom}>
            <div className={styles.inputArea}>
              <IconFont type="icon-head" style={{ fontSize: "3em" }} />
              {/* 用户名密码输入框 */}
              <div>
                <div>
                  <Input placeholder="用户名" onChange={this.usernameInput} />
                  注册账户
                </div>
                <div>
                  <Input type="password" placeholder="密码" onChange={this.passwordInput} />
                  找回密码
                </div>
              </div>
            </div>
            <Button type="primary" onClick={this.submit}>
              登录
            </Button>
          </div>
        </div>
      </div>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Login);
