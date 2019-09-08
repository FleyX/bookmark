import React from "react";
import styles from "./index.module.less";
import MainLayout from "../../layout/MainLayout";

class UserSpace extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <MainLayout>
        <div className={styles.main}>你好</div>
      </MainLayout>
    );
  }
}

export default UserSpace;
