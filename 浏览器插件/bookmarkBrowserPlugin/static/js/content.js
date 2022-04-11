console.log('注入了页面');

/**
 * 接收当前注入页面传来的消息
 */
window.addEventListener('message', function (event) {
  if (event.data.code === undefined) {
    return;
  }
  console.log('接受到网页消息:', event.data);
  sendToBg(event.data);
});


/**
 * 接收background发送的消息
 */
chrome.runtime.onMessage.addListener((data, sender, sendResponse) => dealBgMessage(data));

/**
 * 发送消息给bg
 * @param {*} data 
 */
function sendToBg (data) {
  chrome.runtime.sendMessage(data, response => dealBgMessage(response));
}

/**
 * 处理后台发送的消息
 */
async function dealBgMessage (data) {
  if (!data || !data.code) {
    return;
  }
  console.log('收到来自bg的回复：', data);
  if (data.code == 'setTokenOk') {
    sendToPage(data);
  } else if (data.code == 'addBookmark') {
    if (!checkTokenValid(data.token)) {
      alert("登陆失效，请登陆后，重试");
      window.open(bookmarkHost + "/manage/sso");
      return;
    }
    //新增书签
    let icon = await axios.get(data.data.iconUrl, { responseType: 'arraybuffer' });
    data.data.icon = `data:` + icon.headers['content-type'] + ';base64,' + window.btoa(String.fromCharCode(...new Uint8Array(icon.data)));
    await axios.put("/bookmark", data.data);
  }
}


/**
 * 发消息到页面
 * @param {*} data 
 */
function sendToPage (data) {
  window.postMessage(data, "*");
}

/**
 * 检查token是否有效
 * @param {*} token 
 * @returns 
 */
function checkTokenValid (token) {
  try {
    if (token && token.trim().length > 0) {
      //检查token是否还有效
      let content = JSON.parse(window.atob(token.split(".")[1]));
      if (content.exp > Date.now() / 1000) {
        return true;
      }
    }
  } catch (err) {
    console.error(err);
  }
  return false;
}