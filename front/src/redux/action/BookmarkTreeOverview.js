/**
  /manage/overview action
 */
export const DATA_NAME = "BookmarkTreeOverview";

export function getInitData() {
  return {
    isShowModal: false,
    currentEditNode: null,
    currentAddFolder: null,
    isEdit: false,
    treeData: [],
    checkedKeys: [],
    checkedNodes: [],
    expandedKeys: [],
    isInit: false
  };
}

//定义修改modal是否显示 type
export const CLOSE_MODAL = "closeModal";

export const closeModal = () => {
  return {
    type: CLOSE_MODAL,
    data: {
      isShowModal: false,
      currentEditNode: null,
      currentAddFolder: null
    }
  };
};

//设置是否处于编辑模式
export const SET_IS_EDIT = "isEdit";

export const setIsEdit = isEdit => {
  return {
    type: SET_IS_EDIT,
    data: {
      isEdit
    }
  };
};

//新增节点
export const ADD_NODE = "addNode";

export const addNode = (node, e) => {
  console.log("add node");
  e.stopPropagation();
  return {
    type: ADD_NODE,
    data: {
      isShowModal: true,
      currentAddFolder: node
    }
  };
};

//编辑节点
export const EDIT_NODE = "editNode";

export const editNode = (node, e) => {
  e.stopPropagation();
  return {
    type: EDIT_NODE,
    data: {
      isShowModal: true,
      currentEditNode: node
    }
  };
};

//修改treeData
export const UPDATE_TREEDATA = "updateTreeData";
export const updateTreeData = treeData => {
  return {
    type: UPDATE_TREEDATA,
    data: {
      treeData
    }
  };
};

//修改checkedKeys
export const CHANGE_CHECKED_KEYS = "changeCheckedKeys";
export const changeCheckedKeys = (checkedKeys, e) => {
  return {
    type: CHANGE_CHECKED_KEYS,
    data: { checkedKeys, checkedNodes: e ? e.checkedNodes : [] }
  };
};

//修改expandedKeys
export const CHANGE_EXPANDED_KEYS = "changeExpandedKeys";
export const changeExpandedKeys = expandedKeys => {
  console.log(expandedKeys);
  return {
    type: CHANGE_EXPANDED_KEYS,
    data: { expandedKeys }
  };
};

//修改isInit
export const CHANGE_IS_INIT = "changeIsInit";
export const changeIsInit = isInit => {
  return {
    type: CHANGE_EXPANDED_KEYS,
    data: { isInit }
  };
};
