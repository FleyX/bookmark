import React from "react";
import { Modal, Form, Upload, Button, Radio, Input, Icon, message } from "antd";
import httpUtil from "../../../util/httpUtil";

import * as action from "../../../redux/action/BookmarkTreeOverview";
import { connect } from "react-redux";
import { addNode, getBookmarkList } from "../../../util/cacheUtil";

function mapStateToProps(state) {
  return state[action.DATA_NAME];
}

function mapDispatchToProps(dispatch) {
  return {
    updateTreeData: treeData => dispatch(action.updateTreeData(treeData)),
    setIsEdit: isEdit => dispatch(action.setIsEdit(isEdit)),
    addNode: (item, e) => dispatch(action.addNode(item, e)),
    editNode: (item, e) => dispatch(action.editNode(item, e)),
    closeModal: () => dispatch(action.closeModal())
  };
}

const reg = {
  name: {
    reg: /^.{1,200}$/,
    text: "名称字符数为1-200"
  },
  url: {
    reg: /^.{1,5000}$/,
    text: "链接字符数为1-5000"
  }
};

class AddModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      addType: "bookmark",
      name: "",
      url: "",
      nameHelp: "",
      urlHelp: "",
      file: null,
      isLoading: false
    };
  }

  componentWillReceiveProps(nextProps) {
    const { currentEditNode } = nextProps;
    if (currentEditNode != null) {
      this.type = "edit";
      this.setState({
        addType: currentEditNode.type === 0 ? "bookmark" : "folder",
        name: currentEditNode.name,
        url: currentEditNode.url
      });
    } else {
      this.type = "add";
      this.setState({ addType: "bookmark", name: "", url: "", file: null });
    }
    this.setState({ nameHelp: "", urlHelp: "" });
  }

  checkValue(key, value) {
    const rule = reg[key];
    if (rule.reg.test(value)) {
      this.setState({ [key + "Help"]: "" });
      return true;
    } else {
      this.setState({ [key + "Help"]: rule.text });
      return false;
    }
  }

  changeValue(e) {
    const name = e.target.name;
    const value = e.target.value;
    this.checkValue(name, value);
    this.setState({ [name]: value });
  }

  submit = () => {
    const { currentEditNode } = this.props;
    if (currentEditNode == null) {
      this.addOne();
    } else {
      this.editOne(currentEditNode);
    }
  };

  /**
   * 编辑一个节点
   * @param {*} node
   */
  editOne(node) {
    const { name, url } = this.state;
    const { bookmarkId, type } = this.props.currentEditNode;
    const body = {
      bookmarkId,
      name: name,
      url: url,
      type
    };
    let isOk =
      this.checkValue("name", name) &&
      (type === 0 ? this.checkValue("url", url) : true);
    if (!isOk) {
      return;
    }
    this.setState({ isLoading: true });
    httpUtil
      .post("/bookmark/updateOne", body)
      .then(() => {
        message.success("编辑成功");
        node.name = name;
        node.url = url;
        this.setState({ isLoading: false });
        this.props.closeModal();
      })
      .catch(() => this.setState({ isLoading: false }));
  }

  /**
   * 新增一个节点
   */
  async addOne() {
    const {
      currentAddFolder,
      updateTreeData,
      closeModal,
      treeData
    } = this.props;
    const path =
      currentAddFolder == null
        ? ""
        : currentAddFolder.path + "." + currentAddFolder.bookmarkId;
    const { name, url, file, addType } = this.state;
    if (addType === "file") {
      if (file == null) {
        message.error("请先选择文件");
        return;
      }
      this.setState({ isLoading: true });
      const form = new FormData();
      form.append("path", path);
      form.append("file", file.originFileObj);
      httpUtil
        .put("/bookmark/uploadBookmarkFile", form, {
          headers: { "Content-Type": "multipart/form-data" }
        })
        .then(res => {
          this.setState({ isLoading: false });
          window.location.reload();
        })
        .catch(res => this.setState({ isLoading: false }));
    } else {
      let body = {
        type: this.state.addType === "bookmark" ? 0 : 1,
        path,
        name,
        url
      };
      let isOk =
        this.checkValue("name", name) &&
        (body.type === 0 ? this.checkValue("url", url) : true);
      if (!isOk) {
        return;
      }
      this.setState({ isLoading: true });
      let res = await httpUtil.put("/bookmark", body);
      message.success("加入成功");
      await addNode(currentAddFolder, res);
      if (!currentAddFolder) {
        treeData.push(res);
      }
      updateTreeData([...getBookmarkList("")]);
      closeModal();
      this.setState({ isLoading: false });
    }
  }

  renderFileUpload() {
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
    const { file } = this.state;
    return (
      <div style={{ textAlign: "center" }}>
        <Upload {...uploadProps} style={{ width: "100%" }}>
          <Button type="primary">
            <Icon type="upload" />
            选择书签文件(支持chrome,firefox)
          </Button>
          <div>{file ? file.name : ""}</div>
        </Upload>
      </div>
    );
  }

  render() {
    const { isShowModal, currentEditNode, closeModal } = this.props;
    const { addType, name, url, nameHelp, urlHelp, isLoading } = this.state;
    const type = currentEditNode == null ? "add" : "edit";
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
    return (
      <Modal
        destroyOnClose
        title={type === "add" ? "新增" : "编辑"}
        visible={isShowModal}
        onCancel={closeModal}
        footer={false}
      >
        <Form {...formItemLayout}>
          {type === "add" ? (
            <Form.Item label="类别">
              <Radio.Group
                defaultValue="bookmark"
                onChange={e => this.setState({ addType: e.target.value })}
              >
                <Radio value="bookmark">书签</Radio>
                <Radio value="folder">文件夹</Radio>
                <Radio value="file">上传书签html</Radio>
              </Radio.Group>
            </Form.Item>
          ) : null}
          {addType === "bookmark" || addType === "folder" ? (
            <Form.Item
              label="名称"
              validateStatus={nameHelp === "" ? "success" : "error"}
              help={nameHelp}
            >
              <Input
                type="text"
                name="name"
                onChange={this.changeValue.bind(this)}
                value={name}
              />
            </Form.Item>
          ) : null}
          {addType === "bookmark" ? (
            <Form.Item
              label="URL"
              validateStatus={urlHelp === "" ? "success" : "error"}
              help={urlHelp}
            >
              <Input
                type="text"
                name="url"
                value={url}
                onChange={this.changeValue.bind(this)}
              />
            </Form.Item>
          ) : null}
          {addType === "file" ? this.renderFileUpload() : null}
          <div style={{ textAlign: "center", paddingTop: "1em" }}>
            <Button type="primary" onClick={this.submit} loading={isLoading}>
              提交
            </Button>
          </div>
        </Form>
      </Modal>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(AddModal);
