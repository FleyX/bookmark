import React from "react";
import { Link, withRouter } from "react-router-dom";
import { Menu, Dropdown, Divider} from "antd";
import httpUtil from "../../util/httpUtil";
import { connect } from "react-redux";
import styles from "./index.module.less";
import * as infoAction from "../../redux/action/LoginInfoAction";


function mapStateToProps(state) {
  return state[infoAction.DATA_NAME];
}

function mapDispatchToProps(dispatch) {
  return {
    logout: () => dispatch(infoAction.logout()),
    changeUserInfo: userInfo => dispatch(infoAction.changeUserInfo(userInfo))
  };
}

class MainLayout extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      timer: null,
      //刷新数据弹窗是否展示中
      showDialog: false
    };
  }

  async componentWillMount() {
    let res = await httpUtil.get("/user/currentUserInfo");
    this.props.changeUserInfo(res);
  }

  renderUserArea() {
    const { username, icon } = this.props;
    const menu = (
      <Menu onClick={this.onClick}>
        <Menu.Item key="personSpace">个人资料</Menu.Item>
        <Menu.Item key="logout">退出登陆</Menu.Item>
      </Menu>
    );
    if (username != null) {
      return (
        <Dropdown
          overlay={menu}
          placement="bottomCenter"
          trigger={["hover", "click"]}
        >
          <span style={{ cursor: "pointer" }}>
            <img className={styles.icon} src={icon} alt="icon" />
            {username}
          </span>
        </Dropdown>
      );
    } else {
      return (
        <div>
          <Link to="/public/login">登陆</Link>&emsp;
          <Link to="/public/register">注册</Link>
        </div>
      );
    }
  }

  onClick = e => {
    const { history } = this.props;
    switch (e.key) {
      case "personSpace":
        history.push("/userSpace");
        break;
      case "logout":
        this.props.logout();
        history.replace("/");
        break;
      default:
        break;
    }
  };

  render() {
    return (
      <div className={styles.main}>
        <div className={styles.header}>
          <a href="/">
            <img
              style={{ width: "1.5rem" }}
              src="/img/bookmarkLogo.png"
              alt="logo"
            />
          </a>
          {this.renderUserArea()}
        </div>
        <Divider style={{ margin: 0 }} />
        <div
          style={{
            minHeight: `calc(${document.body.clientHeight}px - 1.45rem)`
          }}
          className={styles.content}
        >
          {this.props.children}
        </div>
        <div className={styles.footer}>
          开源地址：
          <a href="https://github.com/FleyX/bookmark">
            github.com/FleyX/bookmark
          </a>
        </div>
      </div>
    );
  }
}

export default withRouter(
  connect(mapStateToProps, mapDispatchToProps)(MainLayout)
);
