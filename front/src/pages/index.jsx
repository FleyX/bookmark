import React from "react";
import { Button } from "antd";
import httpUtil from "../util/httpUtil.js";

class Main extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  testPriviledge = () => {
    httpUtil.get("/priviledgeTest");
  };

  render() {
    return (
      <div>
        <div>这是主页</div>
        <Button type="primary" onClick={this.testPriviledge}>
          测试未登录跳转
        </Button>
      </div>
    );
  }
}

export default Main;
