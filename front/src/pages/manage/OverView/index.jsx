import React from "react";
import { Icon, Tree, Empty, Button, Tooltip, Modal, Form, Input, Radio, Upload } from "antd";
import MainLayout from "../../../layout/MainLayout";
import httpUtil from "../../../util/httpUtil";
import IconFont from "../../../components/IconFont";
import styles from "./index.module.less";
import { batchDelete, onExpand, closeAll, onCheck, addOne, showAddModel } from "./function.js";

const { TreeNode } = Tree;

export default class OverView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      treeData: [],
      isEdit: false,
      isLoading: true,
      checkedKeys: [],
      expandKeys: [],
      //显示新增弹窗
      isShowModal: false,
      //新增类别
      addType: 0,
      addName: "",
      addValue: "",
      file: null
    };
  }

  componentDidMount() {
    httpUtil
      .get("/bookmark/currentUser")
      .then(res => {
        this.data = res;
        if (res[""]) {
          this.setState({ treeData: res[""] });
        }
        this.setState({ isLoading: false });
      })
      .catch(() => this.setState({ isLoading: false }));
  }

  loadData = e =>
    new Promise(resolve => {
      const item = e.props.dataRef;
      const newPath = item.path + "." + item.bookmarkId;
      if (this.data[newPath]) {
        item.children = this.data[newPath];
        this.setState({ treeData: [...this.state.treeData] });
        resolve();
      } else {
        resolve();
      }
    });

  treeNodeSelect(key, e) {
    const { expandKeys } = this.state;
    const item = e.node.props.dataRef;
    if (item.type === 0) {
      window.open(item.url);
    } else {
      expandKeys.push(item.bookmarkId);
    }
  }

  /**
   * 渲染树节点中节点内容
   * @param {*} item
   */
  renderNodeContent(item) {
    // const { isEdit } = this.state;
    // const bts = (
    //   <div className={styles.btns}>
    //     <Button size="small" type="primary" name="copy" icon="copy" shape="circle" />
    //     <Button size="small" type="danger" id={item.bookmarkId} icon="delete" shape="circle" onClick={this.deleteOne} />
    //   </div>
    // );
    return (
      <React.Fragment>
        <Tooltip placement="bottom" title={item.url}>
          <span>{item.name}</span>
        </Tooltip>
        {/* {isEdit ? bts : null} */}
      </React.Fragment>
    );
  }

  /**
   * 渲染树节点
   * @param {*} items
   */
  renderTreeNodes(items) {
    if (!(items && items.length >= 0)) {
      return null;
    }
    return items.map(item => {
      const isLeaf = item.type === 0;
      if (!isLeaf) {
        return (
          <TreeNode icon={<IconFont type="icon-folder" />} isLeaf={isLeaf} title={item.name} key={item.bookmarkId} dataRef={item}>
            {this.renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return (
        <TreeNode
          icon={<IconFont type="icon-bookmark" />}
          isLeaf={isLeaf}
          title={this.renderNodeContent(item)}
          key={item.bookmarkId}
          dataRef={item}
        />
      );
    });
  }

  render() {
    const { isLoading, isEdit, treeData, expandKeys, checkedKeys, isShowModal, addType, addValue, addName } = this.state;
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
      <MainLayout>
        <div className={styles.main}>
          <div className={styles.header}>
            <div className={styles.left}>
              <span className={styles.myTree}>我的书签树</span>
              {expandKeys.length > 0 ? (
                <Button type="primary" size="small" onClick={closeAll.bind(this)}>
                  收起
                </Button>
              ) : null}
            </div>
            <div className={styles.right}>
              {isEdit ? (
                <React.Fragment>
                  <Button size="small" type="danger" onClick={batchDelete.bind(this)}>
                    删除选中
                  </Button>
                  <Button size="small" type="primary" onClick={showAddModel.bind(this)}>
                    新增
                  </Button>
                </React.Fragment>
              ) : null}
              <Button size="small" type="primary" onClick={() => this.setState({ isEdit: !isEdit })}>
                {isEdit ? "完成" : "编辑"}
              </Button>
            </div>
          </div>
          {/* {treeData.length ? ( */}
          <Tree
            showIcon
            checkedKeys={checkedKeys}
            onCheck={onCheck.bind(this)}
            expandedKeys={expandKeys}
            loadData={this.loadData}
            onExpand={onExpand.bind(this)}
            defaultExpandParent
            checkable={isEdit}
            onSelect={this.treeNodeSelect}
            onDoubleClick={this.copyUrl}
            blockNode
          >
            {this.renderTreeNodes(treeData)}
          </Tree>
          {/* ) : null} */}
          {isLoading === false && treeData.length === 0 ? <Empty description="还没有数据" /> : null}

          <Modal destroyOnClose title="新增" visible={isShowModal} onCancel={() => this.setState({ isShowModal: false })} footer={false}>
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
                <Button type="primary" onClick={addOne.bind(this)}>
                  提交
                </Button>
              </div>
            </Form>
          </Modal>
        </div>
      </MainLayout>
    );
  }
}
