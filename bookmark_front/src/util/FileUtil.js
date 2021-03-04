export function downloadFile(fileName, content) {
  debugger;
  // 定义触发事件的DOM
  var aLink = document.createElement("a");
  // 定义BLOB对象，生成文件内容
  var blob = new Blob([content], { type: "text/html" });
  // 定义事件对象
  var evt = document.createEvent("MouseEvents");
  // 初始化事件
  evt.initMouseEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
  // 定义下载文件名称
  aLink.download = fileName;
  // 根据上面定义的 BLOB 对象创建文件 dataURL
  aLink.href = URL.createObjectURL(blob);
  // 应用事件，触发下载
  aLink.dispatchEvent(evt);
}
