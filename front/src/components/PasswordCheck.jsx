import React from "react";
import { Modal, Input } from "antd";
import http from "../util/httpUtil";

export default class PasswordCheck extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      password: ""
    };
  }

  /**
   * 提交
   */
  async submit() {
    console.log(this.state);
    let actionId = await http.post("/user/checkPassword", {
      password: this.state.password
    });
    this.props.onChange(actionId);
  }

  render() {
    const { visible, onClose } = this.props;
    const { password } = this.state;
    return (
      <Modal
        title="密码校验"
        visible={visible}
        onOk={this.submit.bind(this)}
        onCancel={onClose}
      >
        <Input.Password
          placeholder="输入旧密码"
          value={password}
          onChange={e => this.setState({ password: e.target.value })}
        />
      </Modal>
    );
  }
}
