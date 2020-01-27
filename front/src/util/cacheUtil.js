/* eslint-disable no-undef */
import httpUtil from "./httpUtil";

/**
 * 缓存工具类
 */

/**
 * 全部书签数据key
 */
export const TREE_LIST_KEY = "treeListData";
/**
 * 获取全部书签时间
 */
export const TREE_LIST_TIME_KEY = "treeListDataTime";

/**
 * 缓存书签数据
 */
export async function cacheBookmarkData() {
  let res = await localforage.getItem(TREE_LIST_KEY);
  if (!res) {
    res = await httpUtil.get("/bookmark/currentUser");
    await localforage.setItem(TREE_LIST_KEY, res);
    await localforage.setItem(TREE_LIST_TIME_KEY, Date.now());
  }
  window[TREE_LIST_KEY] = res;
}

/**
 * 获取缓存数据
 * @param {*} path path
 */
export function getBookmarkList(path) {
  return window[TREE_LIST_KEY][path];
}

/**
 * 检查缓存情况
 * @return  返回true说明未过期，否则说明过期了
 */
export async function checkCacheStatus() {
  let date = await localforage.getItem(TREE_LIST_TIME_KEY, Date.now());
  let userInfo = await httpUtil.get("/user/currentUserInfo");
  return !date || date > userInfo.bookmarkChangeTime;
}

/**
 * 清楚缓存数据
 */
export async function clearCache() {
  await localforage.removeItem(TREE_LIST_KEY);
  await localforage.removeItem(TREE_LIST_TIME_KEY);
  window.location.reload();
}
