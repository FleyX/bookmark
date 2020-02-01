import React from "react";
import { Tree, Empty, Button, Spin, Modal } from "antd";
import MainLayout from "../../../layout/MainLayout";
import styles from "./index.module.less";
import { batchDelete, renderTreeNodes, onDrop } from "./function.js";
import {
  cacheBookmarkData,
  getBookmarkList,
  checkCacheStatus,
  clearCache
} from "../../../util/cacheUtil";
import httpUtil from "../../../util/httpUtil";
import AddModal from "./AddModal";
import Search from "../../../components/Search";

import * as action from "../../../redux/action/BookmarkTreeOverview";
import { connect } from "react-redux";

const { confirm } = Modal;

function mapStateToProps(state) {
  return state[action.DATA_NAME];
}

function mapDispatchToProps(dispatch) {
  return {
    refresh: () => dispatch(action.refresh()),
    updateTreeData: treeData => dispatch(action.updateTreeData(treeData)),
    setIsEdit: isEdit => dispatch(action.setIsEdit(isEdit)),
    addNode: (item, e) => dispatch(action.addNode(item, e)),
    editNode: (item, e) => dispatch(action.editNode(item, e)),
    changeIsInit: value => dispatch(action.changeIsInit(value)),
    changeCheckedKeys: (keys, nodes) =>
      dispatch(action.changeCheckedKeys(keys, nodes)),
    changeExpandedKeys: keys => dispatch(action.changeExpandedKeys(keys)),
    changeCurrentClickItem: item =>
      dispatch(action.changeCurrentClickItem(item)),
    changeLoadedKeys: keys => dispatch(action.changeLoadedKeys(keys))
  };
}

class OverView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      //书签树节点是否显示loading
      isLoading: false
    };
  }

  async componentWillUnmount() {
    if (this.state.timer != null) {
      clearInterval(this.state.timer);
    }
  }

  async componentWillMount() {
    await httpUtil.get("/user/loginStatus");
    this.state.timer = setInterval(this.checkCache.bind(this), 5 * 60 * 1000);
    setTimeout(this.checkCache.bind(this), 5000);
  }

  async checkCache() {
    //检查缓存情况
    if (this.state.showDialog) {
      return;
    }
    let _this = this;
    if (!(await checkCacheStatus())) {
      this.state.showDialog = true;
      confirm({
        title: "缓存过期",
        content: "书签数据有更新，是否立即刷新？",
        onOk() {
          _this.state.showDialog = false;
          clearCache();
        },
        onCancel() {
          _this.state.showDialog = false;
        }
      });
    }
  }

  /**
   * 初始化第一级书签
   */
  async componentDidMount() {
    this.props.refresh();
    await cacheBookmarkData();
    this.props.updateTreeData(getBookmarkList(""));
    this.props.changeIsInit(true);
  }

  /**
   * 异步加载
   */
  loadData = e => {
    const { loadedKeys } = this.props;
    return new Promise(resolve => {
      const item = e.props.dataRef;
      const newPath = item.path + "." + item.bookmarkId;
      item.children = getBookmarkList(newPath);
      this.props.updateTreeData([...getBookmarkList("")]);
      loadedKeys.push(item.bookmarkId.toString());
      this.props.changeLoadedKeys(loadedKeys);
      resolve();
    });
  };
  /**
   * 节点选择
   * @param {*} key
   * @param {*} e
   */
  treeNodeSelect(key, e) {
    const { expandedKeys, changeExpandedKeys } = this.props;
    const item = e.node.props.dataRef;
    if (item.type === 0) {
      window.open(
        item.url.startsWith("http") ? item.url : "http://" + item.url
      );
    } else {
      const id = item.bookmarkId.toString();
      const index = expandedKeys.indexOf(id);
      index === -1 ? expandedKeys.push(id) : expandedKeys.splice(index, 1);
      changeExpandedKeys([...expandedKeys]);
    }
  }

  /**
   * 同步书签数据
   */
  async refreshTree() {
    const { refresh } = this.state;
    await clearCache();
    refresh();
  }

  render() {
    const {
      isEdit,
      setIsEdit,
      treeData,
      addNode,
      isInit,
      expandedKeys,
      checkedKeys,
      loadedKeys
    } = this.props;
    const { isLoading } = this.state;
    const { changeExpandedKeys } = this.props;
    return (
      <MainLayout>
        <div className={styles.main}>
          <Search />
          <div className={styles.header}>
            <div className={styles.left}>
              <span className={styles.myTree}>我的书签树</span>
              <Button
                size="small"
                type="primary"
                icon="sync"
                shape="circle"
                onClick={this.refreshTree.bind(this, null)}
              />
              <Button
                size="small"
                type="primary"
                icon="plus"
                shape="circle"
                onClick={addNode.bind(this, null)}
              />
              {expandedKeys.length > 0 ? (
                <Button
                  type="primary"
                  size="small"
                  onClick={changeExpandedKeys.bind(this, [])}
                >
                  收起
                </Button>
              ) : null}
              <a
                className={styles.help}
                href="https://github.com/FleyX/bookmark/blob/master/README.md"
              >
                使用帮助
              </a>
            </div>
            <div className={styles.right}>
              {isEdit ? (
                <React.Fragment>
                  <Button
                    size="small"
                    type="danger"
                    onClick={batchDelete.bind(this)}
                  >
                    删除选中
                  </Button>
                </React.Fragment>
              ) : null}
              <Button
                size="small"
                type="primary"
                onClick={setIsEdit.bind(this, !isEdit)}
              >
                {isEdit ? "完成" : "编辑"}
              </Button>
            </div>
          </div>
          <Spin spinning={isLoading} delay={200}>
            <Tree
              showIcon
              loadedKeys={loadedKeys}
              checkedKeys={checkedKeys}
              onCheck={this.props.changeCheckedKeys}
              expandedKeys={expandedKeys}
              loadData={this.loadData}
              onExpand={this.props.changeExpandedKeys}
              checkable={isEdit}
              onSelect={this.treeNodeSelect.bind(this)}
              draggable
              onDrop={onDrop.bind(this)}
              blockNode
            >
              {renderTreeNodes.call(this, treeData)}
            </Tree>
            {isInit && treeData.length === 0 ? (
              <Empty description="还没有数据" />
            ) : null}
          </Spin>
          <AddModal />
        </div>
      </MainLayout>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(OverView);
