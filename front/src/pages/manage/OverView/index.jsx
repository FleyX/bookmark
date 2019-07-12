import React from "react";
import { Tree, Empty, Button, Tooltip } from "antd";
import MainLayout from "../../../layout/MainLayout";
import httpUtil from "../../../util/httpUtil";
import IconFont from "../../../components/IconFont";
import styles from "./index.module.less";

const { TreeNode } = Tree;

export default class OverView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      treeData: [],
      isEdit: false,
      isLoading: true,
      expandKeys: ["0"]
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
      let newPath;
      if (item.path === "") {
        newPath = item.bookmarkId;
      } else {
        newPath = item.path + "." + item.bookmarkId;
      }
      if (this.data[newPath]) {
        item.children = this.data[newPath];
        this.setState({ treeData: [...this.state.treeData] });
        resolve();
      } else {
        resolve();
      }
    });
  /**
   * 复制url到剪贴板
   * @param {*} key
   * @param {*} e
   */
  copyUrl(key, e) {}

  deleteOne = e => {
    const id = e.target.id;
    
  };

  treeNodeSelect(key, e) {
    const item = e.node.props.dataRef;
    if (item.type === 0) {
      window.open(item.url);
    } else {
    }
  }

  /**
   * 渲染树节点中节点内容
   * @param {*} item
   */
  renderNodeContent(item) {
    const { isEdit } = this.state;
    const bts = (
      <React.Fragment>
        <Button size="small" type="primary" name="copy" icon="copy" shape="circle" />
        <Button size="small" type="danger" id={item.bookmarkId} icon="delete" shape="circle" onClick={this.deleteOne} />
      </React.Fragment>
    );
    return (
      <React.Fragment>
        <Tooltip placement="bottom" title={item.url}>
          {item.name}
        </Tooltip>
        {isEdit ? bts : null}
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
    const { isLoading, isEdit, treeData, expandKeys } = this.state;
    return (
      <MainLayout>
        <div className={styles.main}>
          <div className={styles.header}>
            <span>我的书签树</span>
            <div>
              <Button size="small" type="primary" onClick={() => this.setState({ isEdit: !isEdit })}>
                {isEdit ? "完成" : "编辑"}
              </Button>
            </div>
          </div>
          {treeData.length ? (
            <Tree
              showIcon
              loadData={this.loadData}
              defaultExpandParent
              checkable={isEdit}
              onSelect={this.treeNodeSelect}
              onDoubleClick={this.copyUrl}
              blockNode
            >
              {this.renderTreeNodes(treeData)}
            </Tree>
          ) : null}
          {isLoading === false && treeData.length === 0 ? (
            <Empty description="还没有数据">
              <Button size="small" type="primary" onClick={() => this.setState({ isEdit: true })}>
                新增
              </Button>
            </Empty>
          ) : null}
        </div>
      </MainLayout>
    );
  }
}
