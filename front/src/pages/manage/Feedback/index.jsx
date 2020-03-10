import React from "react";
import { Input, Select, Button, message } from "antd";
import styles from "./index.module.less";
import MainLayout from "../../../layout/MainLayout";
import httpUtil from "../../../util/httpUtil";

const { TextArea } = Input;
const { Option } = Select;

class Feedback extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      content: "",
      //1:bug反馈，2：功能建议
      type: "bug"
    };
  }

  async submit() {
    await httpUtil.put("/feedback", this.state);
    message.success("创建成功");
    this.props.history.goBack();
  }

  render() {
    const { type } = this.state;
    return (
      <MainLayout>
        <div>反馈页面</div>
        <div className={styles.main}>
          <div>
            <Select
              defaultValue={type}
              style={{ width: 140 }}
              onChange={value => this.setState({ type: value })}
            >
              <Option value="bug">BUG反馈</Option>
              <Option value="advice">功能建议</Option>
            </Select>
            <span className={styles.tips}>
              建议通过
              <a
                href="https://github.com/FleyX/bookmark/issues"
                target="github"
              >
                github
              </a>
              反馈
            </span>
          </div>
          <TextArea
            rows="8"
            onChange={e => this.setState({ content: e.target.value })}
          ></TextArea>
          <div>
            <Button type="primary" onClick={this.submit.bind(this)}>
              确认
            </Button>
            <Button type="normal" onClick={() => this.props.history.goBack()}>
              返回
            </Button>
          </div>
        </div>
      </MainLayout>
    );
  }
}

export default Feedback;
