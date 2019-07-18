import { combineReducers } from "redux";
import { DATA_NAME } from "../action/LoginInfoAction.js";
import loginInfo from "./loginInfo";
import * as bookmarkTreeOverview from "../action/BookmarkTreeOverview";
import bookmarkTreeOverviewData from "./BookmarkTreeOverview";

const data = {};
data[DATA_NAME] = loginInfo;
data[bookmarkTreeOverview.DATA_NAME] = bookmarkTreeOverviewData;

const reducer = combineReducers(data);

export default reducer;
