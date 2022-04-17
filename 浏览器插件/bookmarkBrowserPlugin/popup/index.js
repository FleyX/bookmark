console.log("asdf");
console.log(bookmarkHost);

var token;
var login = document.getElementById("login");
var action = document.getElementById("action");

(async () => {
	//初始化
	login.href = bookmarkHost + "/manage/sso/auth";
	document.getElementById("version").innerText = version;
	document.getElementById("about").href = bookmarkHost + "/public/about";
	sendToBg("getToken", null);
	let newestBlock = document.getElementById("newestVersion");
	newestBlock.href = bookmarkHost + "/static/bookmarkBrowserPlugin.zip";
	let res = await axios.get("/common/config/global");
	console.log(res);
	newestBlock.innerText = res.data.data.map.pluginVersion;
})();

/**
 * 退出登陆
 */
document.getElementById("logout").addEventListener("click", () => {
	console.log("click");
	sendToBg("clearToken", null);
	action.style.display = "none";
	login.style.display = "block";
});

/**
 * 发送消息到后台
 * @param {*} data 
 */
function sendToBg (code, data) {
	chrome.runtime.sendMessage({ code, data, receiver: "background" }, res => console.log(res));
}


// 接收content/background发送的消息
chrome.runtime.onMessage.addListener(async (data, sender, sendResponse) => {
	if (!data.code || !data.receiver == 'popup') {
		return;
	}
	sendResponse("ok");
	console.log("popup收到消息：", data);
	if (data.code == 'setToken') {
		token = data.data;
		if (token) {
			action.style.display = "block";
			login.style.display = "none";
		} else {
			login.style.display = "block";
		}
	}
})