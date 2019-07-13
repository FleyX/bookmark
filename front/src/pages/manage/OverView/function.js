import httpUtil from "../../../util/httpUtil";
import { Modal, message } from "antd";

/**
 * 选中的文件夹id列表
 */
let folderIdList = [];
/**
 * 选中的书签id列表
 */
let bookmarkIdList = [];

/**
 * 新增书签的父节点node
 */
let parentNode = null;

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
  parentNode = null;
  data.checkedNodes.forEach(item => {
    const bookmark = item.props.dataRef;
    parentNode = bookmark;
    bookmark.type === 0 ? bookmarkIdList.push(bookmark.bookmarkId) : folderIdList.push(bookmark.bookmarkId);
  });
}

/**
 * 弹出新增modal
 */
export function showAddModel() {
  if (this.state.checkedKeys.length > 1) {
    message.error("选中过多");
    return;
  } else if (this.state.checkedKeys.length === 1) {
    const id = this.state.checkedKeys[0];
    if (bookmarkIdList.indexOf(parseInt(id)) > -1) {
      message.error("只能选择文件夹节点");
      return;
    }
  }
  this.setState({ isShowModal: true });
}

export function addOne() {
  console.log(1);
  let body = {
    type: this.state.addType,
    path: parentNode == null ? "" : parentNode.path + "." + parentNode.bookmarkId,
    name: this.state.addName,
    url: this.state.addValue
  };
  httpUtil.put("/bookmark", body).then(res => {
    let arr;
    if (parentNode == null) {
      arr = this.data[""] ? this.data[""] : [];
    } else {
      arr = this.data[body.path] ? this.data[body.path] : [];
    }
    arr.push(res);
    if (this.state.treeData.length === 0) {
      this.state.treeData.push(arr);
    }
    this.data[body.path] = arr;
    this.setState({ treeData: [...this.state.treeData], addType: 0, addName: "", addValue: "", isShowModal: false });
  });
}

/**
 * 批量删除
 */
export function batchDelete() {
  console.log("1");
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
            folderIdList.forEach(item => set.add(item));
            bookmarkIdList.forEach(item => set.add(item));
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
