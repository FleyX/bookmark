import React from "react";
import { Link, withRouter } from "react-router-dom";
import { Menu, Dropdown, Divider, Modal } from "antd";
import httpUtil from "../../util/httpUtil";
import { connect } from "react-redux";
import styles from "./index.module.less";
import * as infoAction from "../../redux/action/LoginInfoAction";
import { checkCacheStatus, clearCache } from "../../util/cacheUtil";

const { confirm } = Modal;

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
    if (!this.props.username) {
      let res = await httpUtil.get("/user/currentUserInfo");
      this.props.changeUserInfo(res);
      if (this.state.timer != null) {
        clearInterval(this.state.timer);
      }
      await this.checkCache();
      this.state.timer = setInterval(this.checkCache.bind(this), 5 * 60 * 1000);
    }
  }

  async checkCache() {
    //检查缓存情况
    if (this.state.showDialog) {
      return;
    }
    let _this = this;
    if (!(await checkCacheStatus())) {
      this.state.showDialog = true;
      confirm({
        title: "缓存过期",
        content: "书签数据有更新，是否立即刷新？",
        onOk() {
          _this.state.showDialog = false;
          clearCache();
        },
        onCancel() {
          _this.state.showDialog = false;
        }
      });
    }
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
