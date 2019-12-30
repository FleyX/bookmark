import React from "react";
import { Tree, Empty, Button, Spin } from "antd";
import MainLayout from "../../../layout/MainLayout";
import httpUtil from "../../../util/httpUtil";
import styles from "./index.module.less";
import { batchDelete, renderTreeNodes, onDrop } from "./function.js";
import AddModal from "./AddModal";
import Search from "../../../components/Search";

import * as action from "../../../redux/action/BookmarkTreeOverview";
import { connect } from "react-redux";

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

  /**
   * 初始化第一级书签
   */
  componentDidMount() {
    this.props.refresh();
    httpUtil
      .get("/bookmark/currentUser/path?path=")
      .then(res => {
        this.props.updateTreeData(res);
        this.props.changeIsInit(true);
      })
      .catch(() => this.props.changeIsInit(true));
  }

  /**
   * 异步加载
   */
  loadData = e => {
    const { loadedKeys, treeData } = this.props;
    return new Promise(resolve => {
      const item = e.props.dataRef;
      const newPath = item.path + "." + item.bookmarkId;
      httpUtil.get("/bookmark/currentUser/path?path=" + newPath).then(res => {
        item.children = res;
        this.props.updateTreeData([...treeData]);
        loadedKeys.push(item.bookmarkId.toString());
        this.props.changeLoadedKeys(loadedKeys);
        resolve();
      });
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
