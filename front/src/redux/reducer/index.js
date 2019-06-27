import { combineReducers } from "redux";
import { DATA_NAME } from "../action/loginInfoAction";
import loginInfo from "./loginInfo";

const data = {};
data[DATA_NAME] = loginInfo;


const reducer = combineReducers(data);

export default reducer;
