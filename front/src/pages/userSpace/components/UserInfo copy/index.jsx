import React from "react";
import { message } from "antd";
import styles from "./index.module.less";
import MainLayout from "../../layout/MainLayout";
import { connect } from "react-redux";
import * as action from "../../redux/action/LoginInfoAction";
import httpUtil from "../../util/httpUtil";

function mapStateToProps(state) {
  return state[action.DATA_NAME];
}

function mapDispatchToProps(dispatch) {
  return {
    updateOne: (key, value) => dispatch(action.updateOne(key, value))
  };
}

class UserSpace extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  async changeIcon(e) {
    let file = e.target.files[0];
    if (!file || file.size > 500 * 1024) {
      message.error("文件大小请勿超过500KB");
      return;
    }
    let formData = new FormData();
    formData.append("file", file);
    let res = await httpUtil.post("/user/icon", formData, {
      headers: { "Content-Type": "multipart/form-data" }
    });
    console.log(res);
    this.props.updateOne("icon", res);
  }

  render() {
    const { icon, username } = this.props;
    return (
      <MainLayout>
        {/* 头像昵称 */}
        <div className={styles.head}>
          <div className={styles.icon}>
            <img src={icon} alt="icon" className={styles.full} />
            <label className={styles.full}>
              <input type="file" style={{ display: "none" }} onChange={this.changeIcon.bind(this)} />
              <div className={styles.full + " " + styles.changeIcon}>
                <span>编辑</span>
              </div>
            </label>
          </div>
          <div>{username}</div>
        </div>
        {/* 个人资料 */}
        <div></div>
      </MainLayout>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(UserSpace);
