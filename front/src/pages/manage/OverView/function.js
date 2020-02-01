import httpUtil from "../../../util/httpUtil";
import React from "react";
import { Modal, Button, Tree, message, Menu, Dropdown } from "antd";
import styles from "./index.module.less";
import IconFont from "../../../components/IconFont";
import { stopTransfer } from "../../../util/eventUtil";
import {
  deleteNodes,
  moveNode,
  getBookmarkList,
  updateCurrentChangeTime
} from "../../../util/cacheUtil";
const { TreeNode } = Tree;

function menuVisible(item, visible) {
  if (visible) {
    window.copyUrl = item.url;
  }
  this.props.changeCurrentClickItem(item);
}

function menuClick(e) {
  stopTransfer(e);
  const { currentClickItem, addNode, editNode } = this.props;
  switch (e.key) {
    case "add":
      addNode(currentClickItem);
      break;
    case "edit":
      editNode(currentClickItem);
      break;
    case "delete":
      deleteOne.call(this, currentClickItem);
      break;
    default:
      break;
  }
}

/**
 * 渲染树节点中节点内容
 * @param {*} item
 */
export function renderNodeContent(item) {
  const { isEdit } = this.props;
  // 节点内容后面的操作按钮
  const menu = (
    <Menu onClick={menuClick.bind(this)}>
      {item.type === 0 ? (
        <Menu.Item key="copyUrl">
          <span className="copy-to-board">复制URL</span>
        </Menu.Item>
      ) : (
        <Menu.Item key="add">
          <span>新增</span>
        </Menu.Item>
      )}
      <Menu.Item key="edit">编辑</Menu.Item>
      <Menu.Item key="delete">删除</Menu.Item>
    </Menu>
  );
  return (
    <React.Fragment>
      {/* 触发右键菜单 */}
      <Dropdown
        overlay={menu}
        trigger={["contextMenu"]}
        onVisibleChange={menuVisible.bind(this, item)}
      >
        {item.type === 0 ? (
          <a href={item.url} className={styles.nodeContent}>
            {item.name}
          </a>
        ) : (
          <span className={styles.nodeContent}>{item.name}</span>
        )}
      </Dropdown>
      {isEdit ? (
        <Dropdown
          overlay={menu}
          trigger={["click"]}
          onVisibleChange={menuVisible.bind(this, item)}
        >
          <Button
            size="small"
            onClick={stopTransfer.bind(this)}
            type="primary"
            icon="menu"
            shape="circle"
          />
        </Dropdown>
      ) : null}
    </React.Fragment>
  );
}

/**
 * 渲染树节点
 * @param {*} items
 */
export function renderTreeNodes(items) {
  if (!(items && items.length >= 0)) {
    return null;
  }
  return items.map(item => {
    const isLeaf = item.type === 0;
    if (!isLeaf) {
      return (
        <TreeNode
          icon={<IconFont type="icon-folder" />}
          isLeaf={isLeaf}
          title={renderNodeContent.call(this, item)}
          key={item.bookmarkId}
          dataRef={item}
        >
          {renderTreeNodes.call(this, item.children)}
        </TreeNode>
      );
    }
    return (
      <TreeNode
        icon={<IconFont type="icon-bookmark" />}
        isLeaf={isLeaf}
        title={renderNodeContent.call(this, item)}
        key={item.bookmarkId}
        dataRef={item}
      />
    );
  });
}

/**
 * 删除一个
 * @param {*} e
 */
export function deleteOne(item) {
  deleteBookmark.call(this, [item]);
}

/**
 * 批量删除
 */
export function batchDelete() {
  const { checkedNodes } = this.props;

  deleteBookmark.call(this, checkedNodes);
}

/**
 * 删除书签
 * @param {*} folderIdList
 * @param {*} bookmarkIdList
 */
function deleteBookmark(nodeList) {
  const { updateTreeData, treeData, changeCheckedKeys } = this.props;
  const folderIdList = [],
    bookmarkIdList = [],
    dataNodeList = [];
  nodeList.forEach(item => {
    const data = item.props ? item.props.dataRef : item;
    dataNodeList.push(data);
    data.type === 0
      ? bookmarkIdList.push(data.bookmarkId)
      : folderIdList.push(data.bookmarkId);
  });
  Modal.confirm({
    title: "确认删除？",
    content: "删除后，无法找回",
    onOk() {
      return new Promise((resolve, reject) => {
        httpUtil
          .post("/bookmark/batchDelete", { folderIdList, bookmarkIdList })
          .then(() => {
            //遍历节点树数据，并删除
            deleteNodes(dataNodeList);
            //删除根节点下的数据
            dataNodeList
              .filter(item => item.path === "")
              .forEach(item => {
                treeData.splice(treeData.indexOf(item), 1);
              });
            changeCheckedKeys([], null);
            updateTreeData([...getBookmarkList("")]);
            resolve();
          })
          .catch(() => reject());
      });
    }
  });
}

/**
 * 节点拖拽
 * @param {*} info
 */
export async function onDrop(info) {
  const { updateTreeData, loadedKeys, changeLoadedKeys } = this.props;
  const target = info.node.props.dataRef;
  if (!info.dropToGap && target.type === 0) {
    message.error("无法移动到书签内部");
    return;
  }
  this.setState({ isLoading: true });
  let body = await moveNode(info);
  //目标未加载且当前节点为已经展开的目录情况下需要把当前节点从已加载列表中移除，否则在目标节点中展开时会不显示当前节点的子节点
  let index = loadedKeys.indexOf(body.bookmarkId.toString());
  if (index > -1) {
    loadedKeys.splice(index, 1);
    changeLoadedKeys(loadedKeys);
  }
  try {
    await httpUtil.post("/bookmark/moveNode", body);
    message.success("移动完成");
    updateTreeData([...getBookmarkList("")]);
  } catch (error) {
    message.error("后台移动失败，将于2s后刷新页面，以免前后台数据不一致");
    setTimeout(window.location.reload, 2000);
  } finally {
    this.setState({ isLoading: false });
  }
  await updateCurrentChangeTime();
}
