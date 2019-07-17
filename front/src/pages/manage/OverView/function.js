import httpUtil from "../../../util/httpUtil";
import React from "react";
import { Modal, Button, Tooltip, Tree, message } from "antd";
import styles from "./index.module.less";
import IconFont from "../../../components/IconFont";
const { TreeNode } = Tree;

/**
 * 选中的文件夹id列表
 */
let folderIdList = [];
/**
 * 选中的书签id列表
 */
let bookmarkIdList = [];

/**
 * 展开/关闭
 * @param {*} keys
 */
export function onExpand(keys) {
  this.setState({ expandKeys: keys });
}

/**
 * 关闭全部节点
 */
export function closeAll() {
  this.setState({ expandKeys: [] });
}

/**
 * 选中节点
 * @param {*} keys
 * @param {*} data
 */
export function onCheck(keys, data) {
  this.setState({ checkedKeys: keys });
  bookmarkIdList = [];
  folderIdList = [];
  data.checkedNodes.forEach(item => {
    const bookmark = item.props.dataRef;
    bookmark.type === 0 ? bookmarkIdList.push(bookmark.bookmarkId) : folderIdList.push(bookmark.bookmarkId);
  });
}

/**
 * 编辑一个节点
 * @param {*} node 节点对象
 * @param {*} e
 */
function editNode(node, e) {
  e.stopPropagation();
  this.setState({ isShowModal: true, currentEditNode: node });
}

/**
 * 渲染树节点中节点内容
 * @param {*} item
 */
export function renderNodeContent(item) {
  const { isEdit } = this.state;
  // 节点内容后面的操作按钮
  const btns = (
    <div className={styles.btns}>
      {item.type === 0 ? (
        <Button
          size="small"
          className="copy-to-board"
          data-clipboard-text={item.url}
          type="primary"
          name="copy"
          icon="copy"
          shape="circle"
          title="点击复制url"
        />
      ) : null}
      {item.type === 1 ? <Button size="small" type="primary" icon="plus" shape="circle" onClick={this.addOne.bind(this, item)} /> : null}
      <Button size="small" type="primary" icon="edit" shape="circle" onClick={editNode.bind(this, item)} />
      <Button size="small" type="danger" icon="delete" shape="circle" onClick={deleteOne.bind(this, item)} />
    </div>
  );
  return (
    <span style={{ display: "inline-block" }}>
      <Tooltip placement="bottom" title={item.url}>
        <span>{item.name}</span>
      </Tooltip>
      {isEdit ? btns : null}
    </span>
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
export function deleteOne(item, e) {
  e.stopPropagation();
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
  deleteBookmark.call(this, folderIdList, bookmarkIdList);
}

/**
 * 删除书签
 * @param {*} folderIdList
 * @param {*} bookmarkIdList
 */
function deleteBookmark(folderIdList, bookmarkIdList) {
  const _this = this;
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
            deleteTreeData(_this.state.treeData, set);
            _this.setState({ treeData: [..._this.state.treeData], checkedKeys: [] });
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

export function onDragEnter(info) {
  // console.log(info);
}

/**
 * 节点拖拽
 * @param {*} info
 */
export function onDrop(info) {
  const { treeData } = this.state;
  const target = info.node.props.dataRef;
  if (!info.dropToGap && target.type === "0") {
    message.error("只能移动到文件夹中");
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
    current.sort = body.sort;
  } else {
    body.targetPath = target.path + "." + target.bookmarkId;
    if (target.children) {
      target.children.push(current);
    }
  }
  this.setState({ treeData: [...treeData] });
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

function insertToArray(item, index, arr) {
  const length = arr.length;
  let i = length;
  for (; i > index; i--) {
    arr[i] = arr[i - 1];
    arr[i].sort++;
  }
  arr[i] = item;
}
