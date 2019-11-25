import React from "react";
import { Spin, message } from "antd";
import styles from "./index.module.less";
import MainLayout from "../../../layout/MainLayout";

class SsoAuth extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      message: ""
    };
  }

  componentDidMount() {
    let token = localStorage.getItem("token");
    if (
      token == null ||
      token.length === 0 ||
      JSON.parse(atob(token.split(".")[1])).exp < Date.now() / 1000
    ) {
      return;
    }
    let data = {
      type: "sendToken",
      data: token
    };
    setTimeout(() => {
      window.postMessage(data, "*");
      message.info("授权完成，页面将在3s后自动关闭");
      setTimeout(() => {
        window.opener = null;
        window.open("", "_self");
        window.close();
      }, 3000);
    }, 1000);
  }

  render() {
    const { message } = this.state;
    return (
      <MainLayout>
        <div style={{ textAlign: "center", paddingTop: "30%" }}>
          <Spin tip="正在处理中，请稍候..." />
        </div>
        <div id="myCustomEventDiv" className={styles.message}>
          {JSON.stringify(message)}
        </div>
      </MainLayout>
    );
  }
}

export default SsoAuth;
