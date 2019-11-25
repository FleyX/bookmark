/**
 * web页面植入脚本，用于授权等一系列操作。
 */

console.log('注入了页面');
var port = chrome.extension.connect({ name: 'data' });

window.addEventListener('message', event => {
  if (event.data.type === undefined) {
    return;
  }
  console.log('接受到消息', event.data);
  switch (event.data.type) {
    case 'sendToken':
      port.postMessage(event.data);
      break;
    default:
      console.error('未知的事件', event);
  }
});
