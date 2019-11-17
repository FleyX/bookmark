import React from "react";
import { Link } from "react-router-dom";
import MainLayout from "../../../layout/MainLayout/index";
import http from "../../../util/httpUtil";
import queryString from "query-string";

const style = {
  "text-align": "center",
  "padding-top": "200px"
};

class EmailVerify extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      message: "正在校验中，请稍候！"
    };
  }

  async componentDidMount() {
    let param = queryString.parseUrl(window.location.href);
    try {
      await http.get(`baseInfo/verifyEmail?secret=${param.query.key}`);
      this.setState({ message: "校验成功,3s后跳转到首页" });
      setTimeout(() => {
        window.location.href = "/";
      }, 3000);
    } catch (e) {
      this.setState({ message: "校验失败" });
    }
  }

  render() {
    return (
      <MainLayout>
        <div style={{ textAlign: "center" }}>{this.state.message}</div>
      </MainLayout>
    );
  }
}

export default EmailVerify;
