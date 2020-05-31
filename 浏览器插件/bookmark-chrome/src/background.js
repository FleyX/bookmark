global.browser = require('webextension-polyfill');
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
    onclick: () => {
      globalPort.postMessage('点击');
      console.log('我被点击了');
    },
  },
  err => {
    console.error(err);
  }
);
