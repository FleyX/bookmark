<template>
  <div class="ssoAddBookmark">
    <div class="body">
      <div>
        <a-input placeholder="标题" v-model="form.name" @pressEnter="addBookmark" ref="nameInput" />
        <a-input placeholder="网址" v-model="form.url" @pressEnter="addBookmark" />
      </div>
      <div class="list">
        <div class="path">
          <div>保存路径:</div>
          <a-breadcrumb>
            <a-breadcrumb-item class="breadItem"><span @click="breadClick(null)">根</span></a-breadcrumb-item>
            <a-breadcrumb-item class="breadItem" v-for="item in breadList" :key="item.bookmarkId">
              <span @click="breadClick(item)">{{ item.name.length > 4 ? item.name.substr(0, 3) + "..." : item.name }}</span>
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="folderList">
          <div :class="{ item: true, bg: item.type == 1 }" v-for="item in dataList" :key="item.bookmarkId" :title="item.url">
            <span class="text" @click="folderClick(item)">{{ item.name }}</span>
            <a-popconfirm class="actionBar" placement="left" title="确认删除?" ok-text="是" cancel-text="否" @confirm="deleteOne(item, $event)">
              <a-icon type="delete" />
            </a-popconfirm>
          </div>
        </div>
      </div>
    </div>

    <div class="action">
      <div v-if="showAddInput" style="display: flex">
        <a-input v-model="addFolderName" style="width: 8em" @pressEnter="addFolder" ref="folderInput" />
        <a-button shape="circle" icon="close" @click="showAddInput = false" />
        <a-button type="primary" shape="circle" icon="check" @click="addFolder" />
      </div>
      <a-button v-else type="link" @click="showFolderInput">新建文件夹</a-button>
      <div>
        <a-button style="marging-right: 1em" type="" @click="closeIframe">取消</a-button>
        <a-button type="primary" @click="addBookmark">{{ breadList.length === 0 ? "保存到根" : "保存" }}</a-button>
      </div>
    </div>
  </div>
</template>

<script>
import HttpUtil from "@/util/HttpUtil";
import { mapState } from "vuex";
import { TREE_DATA, TOTAL_TREE_DATA, addNode, deleteData } from "@/store/modules/treeData";
export default {
  data() {
    return {
      breadList: [],
      showAddInput: false,
      addFolderName: "",
      form: {
        name: null,
        url: null,
        icon: null,
        iconUrl: null,
        type: 0,
        path: "",
      },
    };
  },
  computed: {
    ...mapState(TREE_DATA, [TOTAL_TREE_DATA]),
    dataList() {
      let path = this.getCurrentPath();
      return this.totalTreeData[path] ? this.totalTreeData[path] : [];
    },
  },
  mounted() {
    //接受父节点传递的书签信息
    window.addEventListener("message", (event) => {
      if (!event.data.code) {
        return;
      }
      console.log("收到content消息", event);
      if (event.data.code == "addBookmarkAction") {
        console.log("新增书签");
        this.form.name = event.data.data.name;
        this.form.url = event.data.data.url;
        this.form.icon = event.data.data.icon;
        this.form.iconUrl = event.data.data.iconUrl;
      }
    });
    console.log("向父节点获取数据");
    window.parent.postMessage({ code: "getBookmarkData", receiver: "content" }, "*");
    this.$refs.nameInput.focus();
  },
  methods: {
    closeIframe() {
      window.parent.postMessage({ code: "closeIframe", receiver: "content" }, "*");
    },
    //新增书签
    async addBookmark() {
      this.form.path = this.getCurrentPath();
      let res = await HttpUtil.put("/bookmark", null, this.form);
      this.$message.success("添加成功");
      await this.$store.dispatch(TREE_DATA + "/" + addNode, {
        sourceNode: this.breadList.length == 0 ? null : this.breadList[this.breadList.length - 1],
        targetNode: res,
      });
      setTimeout(this.closeIframe, 500);
    },
    //面包屑点击
    async breadClick(item) {
      console.log(item);
      //点击最后一个无效
      if (item == null && this.breadList.length == 0) {
        return;
      }
      if (item === this.breadList[this.breadList.length - 1]) {
        return;
      }
      if (item == null) {
        this.breadList = [];
      } else {
        let index = this.breadList.indexOf(item);
        this.breadList = [...this.breadList.slice(0, index + 1)];
      }
    },
    //点击文件夹
    folderClick(item) {
      this.form.path = item.path + "." + item.bookmarkId;
      this.breadList.push(item);
    },
    //新增文件夹
    async addFolder() {
      let length = this.addFolderName.trim().length;
      if (length == 0) {
        this.$message.error("文件夹名称不为空");
        return;
      }
      if (length > 200) {
        this.$message.error("文件夹名称长度不能大于200");
        return;
      }
      let form = {
        name: this.addFolderName,
        path: this.getCurrentPath(),
        type: 1,
        url: "",
      };
      let res = await HttpUtil.put("/bookmark", null, form);
      await this.$store.dispatch(TREE_DATA + "/" + addNode, {
        sourceNode: this.breadList.length == 0 ? null : this.breadList[this.breadList.length - 1],
        targetNode: res,
      });
      this.addFolderName = "";
      this.showAddInput = false;
      this.breadList = [...this.breadList];
      this.$message.success("新增成功");
    },
    //获取当前文件夹路径
    getCurrentPath() {
      if (this.breadList.length == 0) {
        return "";
      } else {
        let lastOne = this.breadList[this.breadList.length - 1];
        return lastOne.path + "." + lastOne.bookmarkId;
      }
    },
    //删除一个
    async deleteOne(item) {
      let body = { pathList: [], bookmarkIdList: [] };
      item.type == 0 ? body.bookmarkIdList.push(item.bookmarkId) : body.pathList.push(item.path + "." + item.bookmarkId);
      await HttpUtil.post("/bookmark/batchDelete", null, body);
      await this.$store.dispatch(TREE_DATA + "/" + deleteData, body);
      this.$message.success("删除成功");
    },
    //显示输入框
    showFolderInput() {
      this.showAddInput = true;
      console.log(this.$refs);
      this.$nextTick(() => this.$refs.folderInput.focus());
    },
  },
};
</script>

<style lang="less" scoped>
.ssoAddBookmark {
  display: flex;
  flex-direction: column;
  padding: 0.5em;
  padding-bottom: 1em;
  background: white;
  width: 100%;
  height: 100vh;

  .body {
    flex: 1;
    height: 0;
    display: flex;
    flex-direction: column;

    .list {
      padding-top: 1em;
      display: flex;
      flex-direction: column;
      flex: 1;
      height: 0;

      .path {
        display: flex;
        overflow: auto;
        font-size: 0.9em;

        .breadItem {
          cursor: pointer;
        }

        .breadItem:last-child {
          cursor: text;
        }
      }

      .folderList {
        flex: 1;
        overflow: auto;
        height: 0;
        margin-left: 0.5em;

        .item {
          display: flex;

          .text {
            flex: 1;
            width: 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .actionBar {
            display: none;
            width: 2em;
            align-items: center;
            color: red;
            cursor: pointer;
          }
        }

        .bg {
          cursor: pointer;
          color: rgb(24, 144, 255);
        }

        .bg:hover {
          background: rgba(74, 74, 74, 0.3);
        }

        .item:hover .actionBar {
          display: flex;
        }
      }
    }
  }

  .action {
    padding-top: 1em;
    display: flex;
    justify-content: space-between;
  }
}
</style>
