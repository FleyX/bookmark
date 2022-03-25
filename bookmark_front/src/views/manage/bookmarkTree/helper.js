/**
 * 导出文件头部信息
 */
export const exportFileHead = `<!DOCTYPE NETSCAPE-Bookmark-file-1>
<!-- This is an automatically generated file.
     It will be read and overwritten.
     DO NOT EDIT! -->
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<TITLE>Bookmarks</TITLE>
<H1>签签世界导出</H1>`;

/**
 * 递归处理数据到html节点中，用于导出
 * @param {*} root 
 * @param {*} list 
 * @param {*} totalMap 
 * @returns 
 */
export function dealList (root, list, totalMap) {
	if (!list || list.length == undefined) {
		return;
	}
	list.forEach((item) => {
		let node = document.createElement("DT");
		root.appendChild(node);
		if (item.type === 0) {
			//说明为书签
			let url = document.createElement("A");
			url.setAttribute("HREF", item.url);
			url.setAttribute("ADD_DATE", parseInt(Date.now() / 1000));
			url.innerText = item.name;
			if (item.icon.length > 0) {
				url.setAttribute("ICON", item.icon);
			}
			node.appendChild(url);
		} else {
			//说明为文件夹
			let header = document.createElement("H3");
			header.setAttribute("ADD_DATE", parseInt(Date.now() / 1000));
			header.innerText = item.name;
			node.appendChild(header);
			let children = document.createElement("DL");
			node.appendChild(children);
			dealList(children, totalMap[item.path + "." + item.bookmarkId], totalMap);
		}
	});
}