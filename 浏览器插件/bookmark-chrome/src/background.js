import store from './store';
global.browser = require('webextension-polyfill');

chrome.extension.onConnect.addListener(port => {
  console.log(port);
  port.onMessage.addListener(msg => {
    switch (msg.type) {
      case 'sendToken':
        localStorage.setItem('token', msg.data);
        break;
      default:
        console.error('未知的数据', msg);
    }
  });
});
