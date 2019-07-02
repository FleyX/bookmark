import React, { Component } from "react";
import queryString from "query-string";
import { Button, Input, message } from "antd";
import IconFont from "../../../components/IconFont";
import styles from "./index.module.less";
import { connect } from "react-redux";
import { changeLoginInfo, DATA_NAME } from "../../../redux/action/loginInfoAction";
import axios from "../../../util/httpUtil";
import { LoginLayout, LOGIN_TYPE } from "../../../layout/LoginLayout";

function mapStateToProps(state) {
  return state[DATA_NAME];
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
    this.query = queryString.parse(window.location.search);
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
      if (this.query.redirect) {
        this.props.history.replace(decodeURIComponent(this.query.redirect));
      } else {
        this.props.history.replace("/");
      }
    });
  };

  render() {
    return (
      <LoginLayout type={LOGIN_TYPE}>
        <div className={styles.main}>
          <Input type="text" placeholder="输入邮箱" />
          <Input type="text" placeholder="验证码" addonAfter={<span className={styles.getCode}>获取验证码</span>} />
        </div>
      </LoginLayout>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Login);
