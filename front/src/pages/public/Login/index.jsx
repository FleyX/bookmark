import React, { Component } from "react";
import { Link } from "react-router-dom";
import queryString from "query-string";
import { Button, Input, message, Checkbox } from "antd";
import IconFont from "../../../components/IconFont";
import styles from "./index.module.less";
import { connect } from "react-redux";
import { changeLoginInfo, DATA_NAME } from "../../../redux/action/LoginInfoAction.js";
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
      str: "",
      password: "",
      rememberMe: false
    };
    this.query = queryString.parse(window.location.search);
  }

  valueChange = e => {
    this.setState({ [e.target.name]: e.target.value });
  };

  submit = () => {
    axios
      .post("/user/login", this.state)
      .then(res => {
        const token = res.token;
        delete res.token;
        localStorage.setItem("token", token);
        localStorage.setItem("userInfo", JSON.stringify(res));
        window.token = token;
        window.userInfo = res;
        message.success("登录成功");
        this.props.updateLoginInfo(token, res);
        if (this.query.redirect) {
          this.props.history.replace(decodeURIComponent(this.query.redirect));
        } else {
          this.props.history.replace("/");
        }
      })
      .catch(error => {
        message.error(error);
      });
  };

  render() {
    const { str, password, rememberMe } = this.state;
    return (
      <LoginLayout type={LOGIN_TYPE}>
        <div className={styles.main}>
          <Input
            type="text"
            size="large"
            name="str"
            value={str}
            onChange={this.valueChange}
            addonBefore={<IconFont type="icon-mail" style={{ fontSize: "0.3rem" }} />}
            placeholder="邮箱或者用户名"
          />
          <Input.Password
            type="password"
            size="large"
            name="password"
            value={password}
            onChange={this.valueChange}
            addonBefore={<IconFont type="icon-password" style={{ fontSize: "0.3rem" }} />}
            placeholder="密码"
          />
          <div className={styles.action}>
            <Checkbox value={rememberMe} name="rememberMe" onChange={this.valueChange}>
              记住我
            </Checkbox>
            <Link to="/public/resetPassword">忘记密码</Link>
          </div>
          <Button type="primary" onClick={this.submit} block>
            登录
          </Button>
        </div>
      </LoginLayout>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Login);
