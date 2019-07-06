import { combineReducers } from "redux";
import { DATA_NAME } from "../action/LoginInfoAction.js";
import loginInfo from "./loginInfo";

const data = {};
data[DATA_NAME] = loginInfo;

const reducer = combineReducers(data);

export default reducer;
