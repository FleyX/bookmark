import { createPinia } from "pinia";
import { useGlobalConfigStore } from "./globalConfig";
import { useTreeDataStore } from "./treeData";
import { checkJwtValid } from "@/util/UserUtil";

export const pinia = createPinia();

let noLoginFinish = false;

//执行各自的非登陆初始化
(async () => {
  const globalConfig = useGlobalConfigStore(pinia);
  const treeData = useTreeDataStore(pinia);
  await globalConfig.noLoginInit();
  //无需等待执行
  treeData.noLoginInit();
  noLoginFinish = true;
})();

/**
 * 执行各模块的登陆后初始化
 */
export async function loginInit() {
  if (!noLoginFinish) {
    await finishNoLogin();
  }
  const globalConfig = useGlobalConfigStore(pinia);
  const treeData = useTreeDataStore(pinia);
  if (checkJwtValid(globalConfig.token)) {
    //无需等待执行完毕
    globalConfig.loginInit();
    treeData.loginInit();
  }
}

/**
 * 推出登陆时需要清理的
 */
export async function logoutClear() {
  await useGlobalConfigStore(pinia).clear();
  await useTreeDataStore(pinia).clear();
}

/**
 * 确保未登录前要初始化的初始化完了
 */
async function finishNoLogin() {
  return new Promise((resolve) => {
    let timer = setInterval(() => {
      if (noLoginFinish) {
        clearInterval(timer);
        resolve();
      }
    }, 100);
  });
}
