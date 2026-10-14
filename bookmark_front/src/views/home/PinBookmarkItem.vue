<template>
  <div>
    <a :href="pinObj.url" v-if="pinObj" class="pinBookmarkItem">
      <img :src="pinObj.icon.length > 0 ? pinObj.icon : '/favicon.ico'" class="icon" />
      <span class="text" :title="pinObj.name">{{ pinObj.name }}</span>
      <a-dropdown :trigger="['click']">
        <span class="action actionShow" title="操作" @click="(e) => e.preventDefault()">...</span>
        <template #overlay>
          <a-menu @click="objAction">
            <a-menu-item key="pinOrNot">{{ pinObj.id ? "取消固定" : "固定书签" }}</a-menu-item>
            <a-menu-item key="editBookmark">修改书签</a-menu-item>
            <a-menu-item key="delete">删除书签</a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </a>
    <div v-else class="pinBookmarkItem" @click="showAddBlock = !showAddBlock" title="新增书签并固定到首页">
      <PlusOutlined style="font-size: 1.5em" />
    </div>
    <a-modal v-model:open="showAddBlock" title="新增" :footer="null" @afterClose="targetNode = null">
      <add-bookmark v-if="showAddBlock" :isAdd="targetNode == null" :targetNode="targetNode" addType="bookmark" @close="addBlockClose" />
    </a-modal>
  </div>
</template>

<script>
import AddBookmark from "@/components/main/things/AddBookmark.vue";
import { PlusOutlined } from "@ant-design/icons-vue";
import { useTreeDataStore } from "@/store/treeData";
import HttpUtil from "@/util/HttpUtil";
export default {
  name: "pinBookmarkItem",
  components: { AddBookmark, PlusOutlined },
  props: {
    pinObj: Object,
  },
  data() {
    return {
      showAddBlock: false,
      targetNode: null,
    };
  },
  methods: {
    //关闭新增
    async addBlockClose(type, obj) {
      console.log(type, obj);
      if (type == "bookmark" && this.targetNode == null) {
        //说明为新增书签
        await HttpUtil.put("/home/pin", null, { bookmarkId: obj.bookmarkId });
      }
      await useTreeDataStore().refreshHomePinList();
      this.showAddBlock = false;
    },
    //下拉菜单点击处理
    async objAction({ key }) {
      let { id, bookmarkId } = this.pinObj;
      let treeData = useTreeDataStore();
      if (key == "pinOrNot") {
        //已固定的取消固定，否则新增固定
        await (id ? HttpUtil.delete("/home/pin?id=" + id) : HttpUtil.put("/home/pin", null, { bookmarkId }));
        await treeData.refreshHomePinList();
      } else if (key == "editBookmark") {
        this.targetNode = treeData.getById(bookmarkId);
        this.showAddBlock = true;
      } else if (key == "delete") {
        let body = { pathList: [], bookmarkIdList: [bookmarkId] };
        await HttpUtil.post("/bookmark/batchDelete", null, body);
        await treeData.deleteData(body);
        await treeData.refreshHomePinList();
      }
    },
  },
};
</script>

<style lang="less" scoped>
.pinBookmarkItem {
  margin-top: 0.1rem;
  border-radius: 5px;
  width: 6em;
  height: 5.5em;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  cursor: pointer;

  .icon {
    width: 2.8em;
    height: 2.8em;
    padding: 0.5em;
    border-radius: 8px;
    background-color: #4a4a4a;
  }

  .text {
    font-size: 0.8em;
    max-width: 5.5em;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .action {
    position: absolute;
    top: 0;
    right: 0.3em;
    display: none;
  }
}

.pinBookmarkItem:hover {
  background-color: rgba(66, 66, 66, 0.4);
}

.pinBookmarkItem:hover .action {
  display: block;
}
</style>
