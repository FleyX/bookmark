chrome.runtime.onInstalled.addListener(() => {
  chrome.contextMenus.create(
    {
      title: '添加到书签',
      id: "addBookmark",
    },
    () => console.log("创建右键菜单成功")
  );
});



chrome.contextMenus.onClicked.addListener(async function (info, tab) {
  console.log(info, tab);
  let body = {
    name: tab.title,
    url: tab.url,
    iconUrl: tab.favIconUrl
  };
  sendToContent(tab.id, { code: "addBookmark", data: body, token: await getVal("token") });
});


// 接收content/popup发送的消息
chrome.runtime.onMessage.addListener(async (data, sender, sendResponse) => {
  if (!data.code || !data.receiver == 'background') {
    return;
  }
  sendResponse("ok");
  console.log("收到消息：", data, sender);
  if (data.code == 'setToken') {
    await setVal("token", data.data);
    // sendToContent
    await sendToContent(sender.tab.id, { code: "setTokenOk" });
  } else if (data.code == 'getToken') {
    let token = await getVal("token");
    sendToPopup({ code: "setToken", data: await getVal("token") });
  } else if (data.code == "clearToken") {
    await clearVal("token");
  }
})

/**
 * 向content发送消息
 * @param {*} tabId 
 * @param {*} data 
 */
function sendToContent (tabId, data) {
  console.log(tabId, data);
  data.receiver = "content";
  chrome.tabs.sendMessage(tabId, data, res => {
    console.log("接受响应", res);
  })
}

/**
 * 向popup发送消息
 * @param {*} data 
 */
function sendToPopup (data) {
  data.receiver = "popup";
  chrome.runtime.sendMessage(data, res => console.log(res));
}

/**
 * 设置值
 * @param {*} key 
 * @param {*} val 
 * @returns 
 */
function setVal (key, val) {
  return new Promise((resolve, reject) => {
    chrome.storage.local.set({ [key]: val }, function () {
      console.log("设置值成功:", key, val)
      resolve();
    })
  })
}

/**
 * 获取值
 * @param {*} key 
 * @returns 
 */
function getVal (key) {
  return new Promise((resolve, reject) => {
    chrome.storage.local.get([key], function (res) {
      console.log("取值成功", res);
      resolve(res[key]);
    })
  })
}

function clearVal (key) {
  return new Promise((resolve, reject) => {
    chrome.storage.local.remove(key, function () {
      console.log("remove成功", key);
      resolve();
    })
  })
}