/**
 * 检查jwt是否有效
 */
export function checkJwtValid(token) {
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
