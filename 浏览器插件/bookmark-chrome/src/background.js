import httpUtil from './util/httpUtil.js';
global.browser = require('webextension-polyfill');

window.envType = 'background';
window.token = localStorage.getItem('token');

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
    onclick: (info, tab) => {
      console.log(info, tab);
      httpUtil.put('/bookmark', {
        type: 0,
        path: '',
        name: tab.title,
        url: tab.url,
      });
    },
  },
  err => {
    console.log(err);
  }
);

/**
 * 构建一个标准命令
 * @param {*} code code
 * @param {*} data data
 */
function createMsg(code, data) {
  return JSON.stringify({ code, data });
}
