import React, { Component } from "react";
import { Link } from "react-router-dom";
import queryString from "query-string";
import { Button, Input, Checkbox } from "antd";
import IconFont from "../../../components/IconFont";
import styles from "./index.module.less";
import { connect } from "react-redux";
import { changeToken, DATA_NAME } from "../../../redux/action/LoginInfoAction.js";
import axios from "../../../util/httpUtil";
import { LoginLayout, LOGIN_TYPE } from "../../../layout/LoginLayout";

function mapStateToProps(state) {
  return state[DATA_NAME];
}

function mapDispatchToProps(dispatch) {
  return {
    updateToken: token => dispatch(changeToken(token))
  };
}

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      str: "",
      password: "",
      rememberMe: false,
      errorText: "",
      isLoading: false
    };
    this.query = queryString.parse(window.location.search);
  }

  checkStr(value) {
    if (value.length === 0) {
      this.setState({ errorText: "用户名或邮箱为空" });
      return false;
    } else {
      this.setState({ errorText: "" });
      return true;
    }
  }

  checkPassword(value) {
    if (/^(?=.*?\d+.*?)(?=.*?[a-zA-Z]+.*?)[\da-zA-Z]{6,20}$/.test(value)) {
      this.setState({ errorText: "" });
      return true;
    } else {
      this.setState({ errorText: "密码为6-20位数字密码组合" });
      return false;
    }
  }

  setValue(e) {
    if (e.target.name === "rememberMe") {
      this.setState({ rememberMe: e.target.checked });
    } else {
      const value = e.target.value.trim();
      if (e.target.name === "str") {
        this.checkStr(value);
      } else {
        this.checkPassword(value);
      }
      this.setState({ [e.target.name]: value });
    }
  }

  submit = () => {
    const { str, password } = this.state;
    if (!(this.checkStr(str) && this.checkPassword(password))) {
      return;
    }
    this.setState({ isLoading: true });
    axios
      .post("/user/login", this.state)
      .then(res => {
        this.setState({ isLoading: false });
        const token = res.token;
        delete res.token;
        localStorage.setItem("token", token);
        window.token = token;
        this.props.updateToken(token);
        if (this.query.redirect) {
          this.props.history.replace(decodeURIComponent(this.query.redirect));
        } else {
          this.props.history.replace("/");
        }
      })
      .catch(() => this.setState({ isLoading: false }));
  };

  render() {
    const { str, password, rememberMe, errorText, isLoading } = this.state;
    return (
      <LoginLayout type={LOGIN_TYPE}>
        <div className={styles.main}>
          <div className={styles.errorText}>{errorText}</div>
          <Input
            type="text"
            size="large"
            name="str"
            value={str}
            onChange={this.setValue.bind(this)}
            addonBefore={<IconFont type="icon-mail" style={{ fontSize: "0.3rem" }} />}
            placeholder="邮箱或者用户名"
          />
          <Input.Password
            type="password"
            size="large"
            value={password}
            name="password"
            onChange={this.setValue.bind(this)}
            addonBefore={<IconFont type="icon-password" style={{ fontSize: "0.3rem" }} />}
            placeholder="密码"
          />
          <div className={styles.action}>
            <Checkbox checked={rememberMe} name="rememberMe" onChange={this.setValue.bind(this)}>
              记住我
            </Checkbox>
            <Link to="/public/resetPassword">忘记密码</Link>
          </div>
          <Button type="primary" onClick={this.submit} block loading={isLoading}>
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
