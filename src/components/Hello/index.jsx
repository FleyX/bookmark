import React, { Component } from "react";
import { Button } from "antd";
import styles1 from "./index.module.less";

class Hello extends Component {
  render() {
    return (
      <div className={styles1.main}>
        hello
        <div className={styles1.text}>world</div>
        <Button type="primary">你好</Button>
        <div className="text1">heihei</div>
      </div>
    );
  }
}

export default Hello;
