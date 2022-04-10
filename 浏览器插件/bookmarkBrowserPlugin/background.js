window.envType = 'background';
window.token = localStorage.getItem('token');

axios.defaults.baseURL = 'https://fleyx.com/bookmark/api';
axios.defaults.headers.common['jwt-token'] = window.token;
axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';


let token = null;
let globalPort = null;

chrome.extension.onConnect.addListener(port => {
  console.log(port);
  globalPort = port;
  port.onMessage.addListener(msg => {
    switch (msg.type) {
      case 'sendToken':
        console.log(msg);
        localStorage.setItem('token', msg.data);
        window.token = msg.data;
        axios.defaults.headers.common['jwt-token'] = window.token;
        token = msg.data;
        break;
      default:
        console.error('未知的数据', msg);
    }
  });
});

chrome.contextMenus.create(
  {
    title: '添加到书签',
    onclick: async function (info, tab) {
      console.log(info, tab);
      let { favIconUrl, title, url } = tab;
      let icon = await axios.get(favIconUrl, { responseType: 'arraybuffer' });
      console.log(icon);
      icon = `data:` + icon.headers['content-type'] + ';base64,' + window.btoa(String.fromCharCode(...new Uint8Array(icon.data)));
      let body = {
        path: "",
        name: title,
        url,
        type: 0,
        icon
      }
      chrome.tabs.sendMessage(tab.id, { code: "addBookmark", body }, res => {
        log.info("send to content");
        console.log(res);
      })
      let res = await axios.put("/bookmark", body);
      if (res.data.code == -1) {
        alert("还未登录，点击拓展按钮进行登录");
      } else if (res.data.code == 0) {
        alert("系统错误");
      }
    },
  },
  () => {
    console.log("创建右键菜单采购");
  }
);

/**
 * 构建一个标准命令
 * @param {*} code code
 * @param {*} data data
 */
function createMsg (code, data) {
  return JSON.stringify({ code, data });
}

// 接收background发送的消息
chrome.runtime.onMessage.addListener((req, sender, sendResponse) => {
  console.log(req);
  sendResponse("收到");
})