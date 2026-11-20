<template>
  <div>
    <a :href="pinObj.url" v-if="pinObj" class="pinBookmarkItem">
      <img :src="pinObj.icon.length > 0 ? pinObj.icon : '/favicon.ico'" class="icon" />
      <span class="text" data-wallpaper-contrast :title="pinObj.name">{{ pinObj.name }}</span>
      <a-dropdown :trigger="['click']">
        <span class="action actionShow" data-wallpaper-contrast title="操作" @click="(e) => e.preventDefault()">...</span>
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
      <span class="addIcon" data-wallpaper-contrast><PlusOutlined style="font-size: 1.5em" /></span>
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
    padding: 0.04em 0.24em 0.08em;
    border-radius: 0.24em;
    color: var(--wallpaper-contrast-color, #fff);
    background-color: var(--wallpaper-contrast-background, rgba(12, 12, 12, 0.82));
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .addIcon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 0.35em;
    border-radius: 0.24em;
    color: var(--wallpaper-contrast-color, #fff);
    background-color: var(--wallpaper-contrast-background, rgba(12, 12, 12, 0.82));
  }

  .action {
    position: absolute;
    top: 0.05em;
    right: 0.2em;
    visibility: hidden;
    padding: 0.06em 0.34em;
    border-radius: 0.24em;
    line-height: 1;
    color: var(--wallpaper-contrast-color, #fff);
    background-color: var(--wallpaper-contrast-background, rgba(12, 12, 12, 0.82));
  }
}

.pinBookmarkItem:hover {
  background-color: rgba(66, 66, 66, 0.4);

  .text,
  .addIcon,
  .action {
    color: var(--wallpaper-contrast-hover-color, #fff);
    background-color: var(--wallpaper-contrast-hover-background, rgba(12, 12, 12, 0.82));
  }
}

.pinBookmarkItem:hover .action {
  visibility: visible;
}
</style>
