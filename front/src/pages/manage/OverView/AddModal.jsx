import React from "react";
import { Modal, Form, Upload, Button, Radio, Input, Icon, message } from "antd";
import httpUtil from "../../../util/httpUtil";

export default class AddModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      addType: 0,
      addName: "",
      addValue: "",
      file: null
    };
  }

  addOne = () => {
    const { currentAddFolder, addToTree, closeModal } = this.props;
    const path = currentAddFolder == null ? "" : currentAddFolder.path + "." + currentAddFolder.bookmarkId;
    if (this.state.addType === 2) {
      const form = new FormData();
      form.append("path", path);
      form.append("file", this.state.file.originFileObj);
      httpUtil
        .put("/bookmark/uploadBookmarkFile", form, {
          headers: { "Content-Type": "multipart/form-data" }
        })
        .then(res => {
          window.location.reload();
        });
    } else {
      let body = {
        type: this.state.addType,
        path,
        name: this.state.addName,
        url: this.state.addValue
      };
      httpUtil.put("/bookmark", body).then(res => {
        addToTree(res);
        message.success("加入成功");
        closeModal();
      });
    }
  };

  close = () => {
    const { closeModal } = this.props;
    this.setState({ file: null, addType: 0, addValue: "", addName: "" });
    closeModal();
  };

  render() {
    const { isShowModal } = this.props;
    const { addType, addName, addValue } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 4 },
        sm: { span: 4 }
      },
      wrapperCol: {
        xs: { span: 20 },
        sm: { span: 20 }
      }
    };
    const uploadProps = {
      accept: ".html,.htm",
      onChange: e => {
        this.setState({ file: e.fileList[0] });
      },
      beforeUpload: file => {
        return false;
      },
      fileList: []
    };
    return (
      <Modal destroyOnClose title="新增" visible={isShowModal} onCancel={this.close} footer={false}>
        <Form {...formItemLayout}>
          <Form.Item label="类别">
            <Radio.Group defaultValue={0} onChange={e => this.setState({ addType: e.target.value })}>
              <Radio value={0}>书签</Radio>
              <Radio value={1}>文件夹</Radio>
              <Radio value={2}>上传书签html</Radio>
            </Radio.Group>
          </Form.Item>
          {addType < 2 ? (
            <Form.Item label="名称">
              <Input type="text" onChange={e => this.setState({ addName: e.target.value })} value={addName} />
            </Form.Item>
          ) : null}
          {addType === 0 ? (
            <Form.Item label="URL">
              <Input type="text" value={addValue} onChange={e => this.setState({ addValue: e.target.value })} />
            </Form.Item>
          ) : null}
          {addType === 2 ? (
            <Upload {...uploadProps}>
              <Button type="primary">
                <Icon type="upload" />
                {this.state.file == null ? "选择文件" : this.state.file.name.substr(0, 20)}
              </Button>
            </Upload>
          ) : null}
          <div style={{ textAlign: "center", paddingTop: "1em" }}>
            <Button type="primary" onClick={this.addOne}>
              提交
            </Button>
          </div>
        </Form>
      </Modal>
    );
  }
}
