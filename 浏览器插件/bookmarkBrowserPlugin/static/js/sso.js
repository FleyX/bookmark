console.log('注入了页面');
var port = chrome.extension.connect({ name: 'data' });
/**
 * 接受background传来的消息
 */
port.onMessage.addListener(msg => {
  console.log('收到消息:' + msg);
  let obj = JSON.parse(msg);
  switch (obj.code) {
    case 'addBookmark':
      break;
    default:
      console.error('未知的命令:' + obj.code);
  }
});

/**
 * 接收当前注入页面传来的消息
 */
window.addEventListener('message', function(event) {
  if (event.data.type === undefined) {
    return;
  }
  console.log('接受到消息', event.data);
  switch (event.data.type) {
    case 'sendToken':
      port.postMessage(event.data);
      window.token = event.data;
      break;
    default:
      console.error('未知的事件', event);
  }
});
