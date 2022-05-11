console.debug('注入了页面');

var bookmarkInfo = null;
var addBlockDiv = null;
var iframe = null;

/**
 * 接收当前注入页面传来的消息
 */
window.addEventListener('message', function (event) {
  console.debug(event);
  if (event.data.code === undefined) {
    return;
  }
  console.debug('接受到网页消息:', event.data);
  if (event.data.code === 'getBookmarkData') {
    iframe.contentWindow.postMessage({ code: "addBookmarkAction", data: bookmarkInfo }, "*");
  } else if (event.data.code === 'setToken') {
    sendToBg(event.data);
  } else if (event.data.code == 'closeIframe') {
    addBlockDiv.remove();
  }
});


/**
 * 接收content/background发送的消息
 */
chrome.runtime.onMessage.addListener(async (data, sender, sendResponse) => {
  if (!data || !data.code || data.receiver != "content") {
    return;
  }
  sendResponse("ok");
  console.debug('收到消息：', data);
  if (data.code == 'setTokenOk') {
    sendToPage(data);
  } else if (data.code == 'addBookmark') {
    await addBookmark(data);
  }
});

async function addBookmark (data) {
  if (!checkTokenValid(data.token)) {
    alert("登陆失效，请登陆后，重试");
    window.open(bookmarkHost + "/manage/sso/auth");
    return;
  }
  //新增书签
  try {
    if (data.data.iconUrl) {
      let icon = await axios.get(data.data.iconUrl, { responseType: 'arraybuffer' });
      console.debug(JSON.stringify(new Uint8Array(icon.data)));
      data.data.icon = `data:` + icon.headers['content-type'] + ';base64,' + window.btoa(String.fromCharCode(...new Uint8Array(icon.data)));
    }
  } catch (error) {
    console.error(error);
  }
  console.debug("新增书签", data.data);
  bookmarkInfo = data.data;
  addBlockDiv = document.createElement("div");
  addBlockDiv.setAttribute("style", "position:fixed;width:100%;height:100vh;z-index:100000;left:0;top:0;background:rgba(211, 211, 205, 0.8)");
  document.getElementsByTagName("body")[0].appendChild(addBlockDiv);
  iframe = document.createElement("iframe");
  iframe.src = bookmarkHost + "/noHead/addBookmark?token=" + data.token;
  iframe.setAttribute("style", "width:640px;display:block;height:80vh;margin:0 auto;margin-top:10vh;padding:0;border:0;border-radius:10px");
  addBlockDiv.appendChild(iframe);
}

/**
 * 发送消息给bg
 * @param {*} data 
 */
function sendToBg (data) {
  data.receiver = "background";
  chrome.runtime.sendMessage(data, response => {
    console.debug(response);
  });
}

/**
 * 发消息到页面
 * @param {*} data 
 */
function sendToPage (data) {
  data.receiver = "page";
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