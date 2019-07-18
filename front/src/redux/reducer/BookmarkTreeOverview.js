import * as info from "../action/BookmarkTreeOverview";

const BookmarkTreeOverviewReducer = (state = info.getInitData(), action) => {
  switch (action.type) {
    case info.CLOSE_MODAL:
    case info.SET_IS_EDIT:
    case info.UPDATE_TREEDATA:
    case info.CHANGE_CHECKED_KEYS:
    case info.CHANGE_EXPANDED_KEYS:
    case info.ADD_NODE:
    case info.EDIT_NODE:
      return { ...state, ...action.data };
    default:
      return state;
  }
};

export default BookmarkTreeOverviewReducer;
