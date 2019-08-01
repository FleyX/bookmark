import httpUtil from "../../../util/httpUtil";
import React from "react";
import { Modal, Button, Tree, message, Menu, Dropdown } from "antd";
import styles from "./index.module.less";
import IconFont from "../../../components/IconFont";
import { stopTransfer } from "../../../util/eventUtil";
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
      <Dropdown overlay={menu} trigger={["contextMenu"]} onVisibleChange={menuVisible.bind(this, item)}>
        {item.type === 0 ? (
          <a href={item.url} className={styles.nodeContent}>
            {item.name}
          </a>
        ) : (
          <span className={styles.nodeContent}>{item.name}</span>
        )}
      </Dropdown>
      {isEdit ? (
        <Dropdown overlay={menu} trigger={["click"]} onVisibleChange={menuVisible.bind(this, item)}>
          <Button size="small" onClick={stopTransfer.bind(this)} type="danger" icon="menu" shape="circle" />
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
  if (item.type === 0) {
    deleteBookmark.call(this, [], [item.bookmarkId]);
  } else {
    deleteBookmark.call(this, [item.bookmarkId], []);
  }
}

/**
 * 批量删除
 */
export function batchDelete() {
  const { checkedNodes } = this.props;
  const folderIdList = [],
    bookmarkIdList = [];
  checkedNodes.forEach(item => {
    const data = item.props.dataRef;
    data.type === 0 ? bookmarkIdList.push(data.bookmarkId) : folderIdList.push(data.bookmarkId);
  });
  deleteBookmark.call(this, folderIdList, bookmarkIdList);
}

/**
 * 删除书签
 * @param {*} folderIdList
 * @param {*} bookmarkIdList
 */
function deleteBookmark(folderIdList, bookmarkIdList) {
  const { updateTreeData, treeData, changeCheckedKeys } = this.props;
  Modal.confirm({
    title: "确认删除？",
    content: "删除后，无法找回",
    onOk() {
      return new Promise((resolve, reject) => {
        httpUtil
          .post("/bookmark/batchDelete", { folderIdList, bookmarkIdList })
          .then(() => {
            //遍历节点树数据，并删除
            const set = new Set();
            folderIdList.forEach(item => set.add(parseInt(item)));
            bookmarkIdList.forEach(item => set.add(parseInt(item)));
            deleteTreeData(treeData, set);
            changeCheckedKeys([], null);
            updateTreeData([...treeData]);
            resolve();
          })
          .catch(() => reject());
      });
    }
  });
}

/**
 * 递归删除已经被删除的数据
 * @param {*} treeData
 */
function deleteTreeData(treeData, set) {
  for (let i = 0, length = treeData.length; i < length; i++) {
    const item = treeData[i];
    if (set.has(treeData[i].bookmarkId)) {
      treeData.splice(i, 1);
      length--;
      i--;
    } else if (item.children && item.children.length > 0) {
      deleteTreeData(item.children, set);
    }
  }
}

/**
 * 节点拖拽
 * @param {*} info
 */
export function onDrop(info) {
  const { treeData, updateTreeData, loadedKeys, changeLoadedKeys } = this.props;
  const target = info.node.props.dataRef;
  if (!info.dropToGap && target.type === 0) {
    message.error("无法移动到书签内部");
    return;
  }
  const current = info.dragNode.props.dataRef;
  const body = {
    bookmarkId: current.bookmarkId,
    sourcePath: current.path,
    targetPath: "",
    //-1 表示排在最后
    sort: -1
  };
  const currentBelowList = current.path === "" ? treeData : getBelowList(treeData, current);
  currentBelowList.splice(currentBelowList.indexOf(current), 1);
  if (info.dropToGap) {
    body.targetPath = target.path;
    const targetBelowList = target.path === "" ? treeData : getBelowList(treeData, target);
    const index = targetBelowList.indexOf(target);
    if (info.dropPosition > index) {
      body.sort = target.sort + 1;
      insertToArray(current, index + 1, targetBelowList);
    } else {
      body.sort = target.sort;
      insertToArray(current, index, targetBelowList);
    }
  } else {
    //移动该一个文件夹内，该文件夹可能还未加载
    body.targetPath = target.path + "." + target.bookmarkId;
    //存在children说明已经加载
    if (target.children) {
      const length = target.children.length;
      body.sort = length > 0 ? target.children[length - 1].sort + 1 : 1;
      target.children.push(current);
    } else if (current.type === 1 && current.children) {
      //目标未加载且当前节点为已经展开的目录情况下需要把当前节点从已加载列表中移除，否则在目标节点中展开时会不显示当前节点的子节点
      loadedKeys.splice(loadedKeys.indexOf(current.bookmarkId.toString()), 1);
      changeLoadedKeys(loadedKeys);
    }
  }
  if (body.sort !== -1) {
    //说明目标位置已经加载，需要更新当前节点信息
    current.path = body.targetPath;
    current.sort = body.sort;
  }
  //如果移动的目标和当前位置不在同一个层级需要更新子节点path信息
  if (body.sourcePath !== body.targetPath) {
    updateChildrenPath(current.children, body.sourcePath + "." + body.bookmarkId, body.targetPath + "." + body.bookmarkId);
  }
  httpUtil.post("/bookmark/moveNode", body).then(res => {
    message.success("移动完成");
    updateTreeData([...treeData]);
  });
}

/**
 * 获取node所属list
 * @param {*} tree 树结构
 * @param {*} node node
 */
function getBelowList(treeList, node) {
  for (let i in treeList) {
    let item = treeList[i];
    if (item.type === 1) {
      if (item.path + "." + item.bookmarkId === node.path) {
        return item.children;
      } else if (item.children) {
        return getBelowList(item.children, node);
      }
    }
  }
}

/**
 * 往数组中插入一个节点，并让后面节点的sort+1
 * @param {*} item 节点
 * @param {*} index 插入位置
 * @param {*} arr  目标数组
 */
function insertToArray(item, index, arr) {
  const length = arr.length;
  let i = length;
  for (; i > index; i--) {
    arr[i] = arr[i - 1];
    arr[i].sort++;
  }
  arr[i] = item;
}

/**
 * 更新修改节点的子节点节点path信息
 * @param {*} children 孩子数组
 * @param {*} oldPath  旧path
 * @param {*} newPath 新path
 */
function updateChildrenPath(children, oldPath, newPath) {
  if (children && children.length > 0) {
    children.forEach(item => {
      item.path = item.path.replace(oldPath, newPath);
      updateChildrenPath(item.children, oldPath, newPath);
    });
  }
}
