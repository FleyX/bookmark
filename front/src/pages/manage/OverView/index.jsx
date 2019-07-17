import React from "react";
import { Tree, Empty, Button } from "antd";
import MainLayout from "../../../layout/MainLayout";
import httpUtil from "../../../util/httpUtil";
import styles from "./index.module.less";
import { batchDelete, renderTreeNodes, onExpand, closeAll, onCheck, onDragEnter, onDrop } from "./function.js";
import AddModal from "./AddModal";

export default class OverView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      treeData: [],
      isEdit: false,
      isLoading: true,
      checkedKeys: [],
      expandKeys: [],
      //是否显示新增/编辑书签弹窗
      isShowModal: false,
      currentAddFolder: null,
      currentEditNode: null
    };
  }

  /**
   * 初始化第一级书签
   */
  componentDidMount() {
    httpUtil
      .get("/bookmark/currentUser/path?path=")
      .then(res => {
        this.setState({ treeData: res, isLoading: false });
      })
      .catch(() => this.setState({ isLoading: false }));
  }

  /**
   * 异步加载
   */
  loadData = e =>
    new Promise(resolve => {
      const item = e.props.dataRef;
      const newPath = item.path + "." + item.bookmarkId;
      httpUtil.get("/bookmark/currentUser/path?path=" + newPath).then(res => {
        item.children = res;
        this.setState({ treeData: [...this.state.treeData] });
        resolve();
      });
    });

  /**
   * 节点选择
   * @param {*} key
   * @param {*} e
   */
  treeNodeSelect(key, e) {
    if (e.nativeEvent.delegateTarget && e.nativeEvent.delegateTarget.name === "copy") {
      return;
    }
    const { expandKeys } = this.state;
    const item = e.node.props.dataRef;
    if (item.type === 0) {
      window.open(item.url);
    } else {
      const id = item.bookmarkId.toString();
      const index = expandKeys.indexOf(id);
      index === -1 ? expandKeys.push(id) : expandKeys.splice(index, 1);
      this.setState({ expandKeys: [...expandKeys] });
    }
  }

  /**
   * 将新增的数据加入到state中
   */
  addToTree = node => {
    const { currentAddFolder, treeData } = this.state;
    if (currentAddFolder === null) {
      treeData.push(node);
    } else {
      //存在children说明该子节点的孩子数据已经加载，需要重新将新的子书签加入进去
      if (currentAddFolder.children) {
        currentAddFolder.children.push(node);
        this.setState({ treeData: [...treeData] });
      }
      this.setState({ currentAddFolder: null });
    }
  };

  /**
   * 关闭新增书签弹窗
   */
  closeAddModal = () => {
    this.setState({ isShowModal: false, currentAddFolder: null, currentEditNode: null });
  };

  /**
   * 新增书签
   */
  addOne = (item, e) => {
    e.stopPropagation();
    this.setState({ currentAddFolder: item, isShowModal: true });
  };

  render() {
    const { isLoading, isEdit, treeData, expandKeys, checkedKeys, isShowModal, currentAddFolder, currentEditNode } = this.state;
    return (
      <MainLayout>
        <div className={styles.main}>
          <div className={styles.header}>
            <div className={styles.left}>
              <span className={styles.myTree}>我的书签树</span>
              {isEdit ? <Button size="small" type="primary" icon="plus" shape="circle" onClick={this.addOne.bind(this, null)} /> : null}
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
                </React.Fragment>
              ) : null}
              <Button size="small" type="primary" onClick={() => this.setState({ isEdit: !isEdit })}>
                {isEdit ? "完成" : "编辑"}
              </Button>
            </div>
          </div>
          <Tree
            showIcon
            checkedKeys={checkedKeys}
            onCheck={onCheck.bind(this)}
            expandedKeys={expandKeys}
            loadData={this.loadData}
            onExpand={onExpand.bind(this)}
            checkable={isEdit}
            onSelect={this.treeNodeSelect.bind(this)}
            draggable
            onDragEnter={onDragEnter.bind(this)}
            onDrop={onDrop.bind(this)}
            blockNode
          >
            {renderTreeNodes.call(this, treeData)}
          </Tree>
          {isLoading === false && treeData.length === 0 ? <Empty description="还没有数据" /> : null}
          <AddModal
            isShowModal={isShowModal}
            currentAddFolder={currentAddFolder}
            currentEditNode={currentEditNode}
            closeModal={this.closeAddModal}
            addToTree={this.addToTree}
          />
        </div>
      </MainLayout>
    );
  }
}
