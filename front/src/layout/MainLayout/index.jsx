import React from "react";
import { Link, withRouter } from "react-router-dom";
import { Menu, Dropdown, Divider } from "antd";
import { connect } from "react-redux";
import styles from "./index.module.less";
import { changeLoginInfo, DATA_NAME } from "../../redux/action/LoginInfoAction";

function mapStateToProps(state) {
  return state[DATA_NAME];
}

function mapDispatchToProps(dispatch) {
  return {
    updateLoginInfo: (token, userInfo) => dispatch(changeLoginInfo(token, userInfo))
  };
}

class MainLayout extends React.Component {
  constructor(props) {
    super(props);
    console.log(props);
    this.state = {};
  }

  renderUserArea() {
    const { userInfo } = this.props;
    const menu = (
      <Menu onClick={this.onClick}>
        <Menu.Item key="personSpace">个人资料</Menu.Item>
        <Menu.Item key="logout">退出登陆</Menu.Item>
      </Menu>
    );
    if (userInfo !== null) {
      return (
        <Dropdown overlay={menu} placement="bottomCenter" trigger={["hover", "click"]}>
          <span style={{ cursor: "pointer" }}>{userInfo.username}</span>
        </Dropdown>
      );
    } else {
      return (
        <div>
          <Link to="/public/login">登陆</Link>
          <Link to="/public/register">注册</Link>
        </div>
      );
    }
  }

  onClick = e => {
    const { history } = this.props;
    switch (e.key) {
      case "logout":
        this.props.updateLoginInfo(null, null);
        localStorage.removeItem("token");
        localStorage.removeItem("userInfo");
        delete window.token;
        delete window.userInfo;
        history.replace("/");
        break;
      default:
        break;
    }
  };

  render() {
    return (
      <div className={"fullScreen " + styles.main}>
        <div className={styles.header}>
          <img style={{ width: "1.5rem" }} src="/img/bookmarkLogo.png" alt="logo" />
          {this.renderUserArea()}
        </div>
        <Divider style={{ margin: 0 }} />
        <div className={styles.content}>{this.props.children}</div>
      </div>
    );
  }
}

export default withRouter(
  connect(
    mapStateToProps,
    mapDispatchToProps
  )(MainLayout)
);
