import React from "react";
import { Modal, Form, Upload, Button, Radio, Input, Icon, message } from "antd";
import httpUtil from "../../../util/httpUtil";

import * as action from "../../../redux/action/BookmarkTreeOverview";
import { connect } from "react-redux";

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

class AddModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      addType: 0,
      addName: "",
      addValue: "",
      file: null
    };
  }

  componentWillReceiveProps(nextProps) {
    const { currentEditNode } = nextProps;
    if (currentEditNode != null) {
      this.type = "edit";
      this.setState({ addType: currentEditNode.type, addName: currentEditNode.name, addValue: currentEditNode.url });
    } else {
      this.type = "add";
      this.setState({ addType: 0, addName: "", addValue: "", file: null });
    }
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
    const { addName, addValue } = this.state;
    const { bookmarkId, type } = this.props.currentEditNode;
    const body = {
      bookmarkId,
      name: addName,
      url: addValue,
      type
    };
    httpUtil.post("/bookmark/updateOne", body).then(() => {
      message.success("编辑成功");
      node.name = addName;
      node.url = addValue;
      this.props.closeModal();
    });
  }

  /**
   * 新增一个节点
   */
  addOne() {
    const { currentAddFolder, updateTreeData, closeModal, treeData } = this.props;
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
        // addToTree(res);
        message.success("加入成功");
        if (currentAddFolder === null) {
          treeData.push(res);
        } else {
          //存在children说明该子节点的孩子数据已经加载，需要重新将新的子书签加入进去
          if (currentAddFolder.children) {
            currentAddFolder.children.push(res);
          }
        }
        updateTreeData(treeData);
        closeModal();
      });
    }
  }

  render() {
    const { isShowModal, currentEditNode, closeModal } = this.props;
    const { addType, addName, addValue } = this.state;
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
      <Modal destroyOnClose title={type === "add" ? "新增" : "编辑"} visible={isShowModal} onCancel={closeModal} footer={false}>
        <Form {...formItemLayout}>
          {type === "add" ? (
            <Form.Item label="类别">
              <Radio.Group defaultValue={0} onChange={e => this.setState({ addType: e.target.value })}>
                <Radio value={0}>书签</Radio>
                <Radio value={1}>文件夹</Radio>
                <Radio value={2}>上传书签html</Radio>
              </Radio.Group>
            </Form.Item>
          ) : null}
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
            <Button type="primary" onClick={this.submit}>
              提交
            </Button>
          </div>
        </Form>
      </Modal>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddModal);
