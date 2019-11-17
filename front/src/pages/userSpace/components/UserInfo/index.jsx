import React from "react";
import { message, Button, Tooltip, Input, Form } from "antd";
import styles from "./index.module.less";
import { connect } from "react-redux";
import * as action from "../../../../redux/action/LoginInfoAction";
import httpUtil from "../../../../util/httpUtil";
import PasswordCheck from "../../../../components/PasswordCheck";

function mapStateToProps(state) {
  return state[action.DATA_NAME];
}

function mapDispatchToProps(dispatch) {
  return {
    updateOne: (key, value) => dispatch(action.updateOne(key, value))
  };
}

class UserInfo extends React.Component {
  constructor(props) {
    super(props);
    console.log(props);
    this.state = {
      email: null,
      isEmail: false,
      username: null,
      isUsername: false,
      password: "",
      repeatPassword: "",
      isPassword: false,
      actionId: null,
      isModelShow: false,
      //当前正在提交的
      currentAction: null,
      //当前正在提交的
      currentShowKey: null
    };
  }

  /**
   * 修改头像
   * @param {*} e
   */
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

  /**
   * 更新
   * @param {*} itemKey 修改的字段
   * @param {*} isShowKey 是否修改字段
   */
  async submit(itemKey, isShowKey) {
    if (this.state[itemKey] == null || this.state[itemKey] == "") {
      message.error("请修改后重试");
      return;
    }
    const { password, repeatPassword, actionId, email, username } = this.state;
    if (itemKey === "password" && password !== repeatPassword) {
      message.error("两次密码不一致");
      return;
    }
    if ((itemKey === "password" || itemKey === "email") && actionId == null) {
      message.warning("敏感操作，需校验密码");
      this.setState({ isModelShow: true, currentAction: itemKey, currentShowKey: isShowKey });
      return;
    }
    try {
      if (itemKey === "password") {
        await httpUtil.post("/baseInfo/password", { actionId, password });
        message.info("密码更新成功");
      } else if (itemKey === "email") {
        await httpUtil.post("/baseInfo/email", { actionId, email });
        this.props.updateOne("newEmail", email);
        message.info("新邮箱验证邮件已发送，请注意查收");
      } else {
        await httpUtil.post("/baseInfo/username", { username });
        this.props.updateOne("username", username);
      }
      this.setState({ [isShowKey]: false });
    } finally {
      this.setState({ actionId: null });
    }
  }

  async actionIdChange(actionId) {
    const { currentAction, currentShowKey } = this.state;
    this.setState({ actionId, isModelShow: false, currentAction: null, currentShowKey: null });
    this.submit(currentAction, currentShowKey);
  }

  /**
   * 渲染用户名
   * @param {string} itemKey
   * @param {string} isShowKey
   */
  renderItem(label, itemKey, isShowKey) {
    let repeatPassword = this.state.repeatPassword;
    let itemValue = this.state[itemKey] === null ? this.props[itemKey] : this.state[itemKey];
    let isShow = this.state[isShowKey];
    let block;
    if (isShow) {
      block = (
        <div>
          {itemKey == "password" ? (
            <div>
              <Input.Password value={itemValue} placeholder="新密码" onChange={e => this.setState({ [itemKey]: e.target.value })} />
              <Input.Password value={repeatPassword} placeholder="重复密码" onChange={e => this.setState({ repeatPassword: e.target.value })} />
            </div>
          ) : (
            <Input value={itemValue} onChange={e => this.setState({ [itemKey]: e.target.value })} />
          )}
          <div style={{ marginTop: "0.1rem" }}>
            <Button type="primary" onClick={this.submit.bind(this, itemKey, isShowKey)}>
              保存
            </Button>
            &nbsp;&nbsp;
            <Button onClick={() => this.setState({ [isShowKey]: false })}>取消</Button>
          </div>
        </div>
      );
    } else {
      block = (
        <Tooltip title="点击编辑">
          <div
            className={itemKey === "username" ? styles.username : "" + " pointer " + styles.value}
            onClick={() => this.setState({ [isShowKey]: true })}
          >
            {itemKey === "password" ? "********" : itemValue}
          </div>
        </Tooltip>
      );
    }
    return (
      <div className={styles.item + " flex"}>
        {label.length > 0 ? <div className={styles.label}>{label}</div> : null}
        {block}
      </div>
    );
  }

  render() {
    const { isModelShow } = this.state;
    const { icon, username } = this.props;
    return (
      <div className={styles.head}>
        {/* 头像昵称 */}
        <div className={styles.icon}>
          <img src={icon} alt="icon" className={styles.full} />
          <label className={styles.full}>
            <input type="file" style={{ display: "none" }} onChange={this.changeIcon.bind(this)} />
            <div className={styles.full + " " + styles.changeIcon}>
              <span>编辑</span>
            </div>
          </label>
        </div>
        {/* 个人信息 */}
        <div className={styles.userinfo}>
          {this.renderItem("", "username", "isUsername")}
          {this.renderItem("邮箱", "email", "isEmail")}
          {this.renderItem("密码", "password", "isPassword")}
        </div>

        <PasswordCheck visible={isModelShow} onClose={() => this.setState({ isModelShow: false })} onChange={this.actionIdChange.bind(this)} />
      </div>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(UserInfo);
