import httpUtil from "../../../util/httpUtil";
import { Modal } from "antd";

/**
 * 删除书签
 * @param {*} _this 组件实例
 * @param {*} folderList 删除的文件夹列表
 * @param {*} bookmarkList 删除的具体书签列表
 */
export function deleteBookmark(_this, folderList, bookmarkList) {
  Modal.confirm({
    title: "确认删除？",
    content: "删除后，无法找回",
    onOk() {
      return httpUtil.post("/dele")
    }
  });
}
