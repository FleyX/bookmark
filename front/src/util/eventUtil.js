/**
 * 阻止原生/合成事件冒泡
 * @param {*} e
 */
export function stopTransfer(e) {
  if (e.domEvent) {
    e = e.domEvent;
  }
  e.nativeEvent.stopImmediatePropagation();
  e.stopPropagation();
}
