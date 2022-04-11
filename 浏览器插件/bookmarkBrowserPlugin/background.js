chrome.contextMenus.create(
  {
    title: '添加到书签',
    id: "addBookmark",
  },
  () => console.log("创建右键菜单成功")
);


chrome.contextMenus.onClicked.addListener(async function (info, tab) {
  console.log(info, tab);
  let body = {
    path: "",
    name: tab.title,
    url: tab.url,
    type: 0,
    iconUrl: tab.favIconUrl
  };
  sendToContent(tab.id, { code: "addBookmark", data: body, token: await getVal("token") });
})



/**
 * 构建一个标准命令
 * @param {*} code code
 * @param {*} data data
 */
function createMsg (code, data) {
  return JSON.stringify({ code, data });
}

// 接收content发送的消息
chrome.runtime.onMessage.addListener(async (data, sender, sendResponse) => {
  if (!data.code) {
    return;
  }
  console.log("收到content发送消息：", data);
  if (data.code == 'setToken') {
    setVal("token", data.data);
    sendResponse({ code: "setTokenOk" });
  }
})

/**
 * 向content发送消息
 * @param {*} tabId 
 * @param {*} data 
 */
function sendToContent (tabId, data) {
  console.log(tabId, data);
  chrome.tabs.sendMessage(tabId, data, res => {
    console.log("接受响应", res);
  })
}

function setVal (key, val) {
  return new Promise((resolve, reject) => {
    chrome.storage.local.set({ [key]: val }, function () {
      console.log("设置值成功:", key, val)
      resolve();
    })
  })
}

function getVal (key) {
  return new Promise((resolve, reject) => {
    chrome.storage.local.get([key], function (res) {
      console.log("取值成功", res);
      resolve(res[key]);
    })
  })
}