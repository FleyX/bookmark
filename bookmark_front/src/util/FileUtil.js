export function downloadFile(fileName, content) {
  // 定义触发事件的DOM
  const aLink = document.createElement("a");
  // 定义BLOB对象，生成文件内容
  const blob = new Blob([content], { type: "text/html" });
  // 定义下载文件名称
  aLink.download = fileName;
  // 根据上面定义的 BLOB 对象创建文件 dataURL
  aLink.href = URL.createObjectURL(blob);
  // 触发下载
  aLink.click();
}
