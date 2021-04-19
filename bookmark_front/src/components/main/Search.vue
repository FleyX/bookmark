<template>
  <div class="search">
    <a-input-search id="searchInput" ref="searchInput" size="large" style="width: 100%" v-model="value" @change="search" @search="searchClick" allowClear @blur.prevent="inputBlur" @focus="inputFocus" @keydown="keyPress">
      <a-tooltip title="全网搜索" slot="enterButton">
        <a-button icon="search" type="primary" />
      </a-tooltip>
    </a-input-search>
    <div v-if="focused && searchBookmark" class="searchContent">
      <a-empty v-if="list.length == 0" />
      <div class="listItem" :class="{ itemActive: index == hoverIndex || index == selectIndex }" v-for="(item, index) in list" :key="item.bookmarkId" @mouseenter="mouseEnterOut(index, 'enter')" @mouseleave="mouseEnterOut(index, 'leave')" @mouseup="onMouse">
        <a class="listItemUrl" style="padding-right: 1em;min-width:3em; max-width: calc(100% - 2em)" :id="'bookmark:' + item.bookmarkId" :href="item.url" @click="itemClick($event,index)" target="_blank">
          {{ item.name }}
        </a>
        <a-tooltip v-if="showActions && hoverIndex === index" title="定位到书签树中">
          <my-icon style="color: #40a9ff; font-size: 1.3em; cursor: pointer" type="icon-et-location" @click="location(item)" />
        </a-tooltip>
      </div>
    </div>
    <a ref="targetA" style="left: 1000000px" target="_blank" />
  </div>
</template>

<script>
import HttpUtil from "@/util/HttpUtil";
import { mapState } from "vuex";
export default {
  name: "Search",
  props: {
    //是否显示定位等按钮
    showActions: Boolean,
  },
  data() {
    return {
      value: "",
      focused: false,
      list: [],
      //计时器结束列表的显示
      timer: null,
      //鼠标悬浮选中
      hoverIndex: null,
      //上下选中
      selectIndex: null,
      //是否搜索书签
      searchBookmark: true,
    };
  },
  computed: {
    ...mapState("treeData", ["totalTreeData"]),
    ...mapState("globalConfig", ["userInfo"]),
  },
  methods: {
    search(content) {
      this.value = content.target.value;
      let val = content.target.value.toLocaleLowerCase().trim();
      if (val === "") {
        this.list = [];
        return;
      }
      let time1 = Date.now();
      this.list = this.dealSearch(val);
      this.selectIndex = 0;
      console.info("搜索耗时：" + (Date.now() - time1));
    },
    /**
     * 初始化数据
     */
    init() {
      this.focused = false;
      this.value = "";
      this.list = [];
      this.selectIndex = null;
    },
    searchClick(value, e) {
      //如果是enter按键触发的不作处理
      if (e && e.key) {
        return;
      }
      if (this.timer != null) {
        clearTimeout(this.timer);
      }
      switch (this.userInfo.defaultSearchEngine) {
        case "bing":
          window.open("https://www.bing.com/search?q=" + encodeURIComponent(this.value));
          break;
        case "google":
          window.open("https://www.google.com/search?q=" + encodeURIComponent(this.value));
          break;
        default:
          window.open("https://www.baidu.com/s?ie=UTF-8&wd=" + encodeURIComponent(this.value));
      }
    },
    itemClick(e, index) {
      if (e) {
        this.stopDefault(e);
      }
      if (index === undefined || index === null) {
        return;
      }
      let bookmark = this.list[index];
      HttpUtil.post("/bookmark/visitNum", { id: bookmark.bookmarkId });
      let a = this.$refs["targetA"];
      a.href = bookmark.url;
      a.click();
      return false;
    },
    inputBlur() {
      console.log("blur");
      this.timer = setTimeout(() => (this.focused = false), 250);
    },
    inputFocus() {
      this.focused = true;
    },
    onMouse(e) {
      if (this.timer != null) {
        clearTimeout(this.timer);
      }
      this.$refs["searchInput"].focus();
    },
    mouseEnterOut(item, type) {
      if (type === "enter") {
        this.hoverIndex = item;
      } else {
        this.hoverIndex = null;
      }
    },
    //定位到书签树中
    location(item) {
      this.$emit("location", item);
    },
    /**
     * 键盘事件处理
     */
    keyPress(e) {
      switch (e.key) {
        case "ArrowUp":
          this.selectIndex = this.selectIndex == null ? this.list.length - 1 : this.selectIndex == 0 ? null : this.selectIndex - 1;
          this.stopDefault();
          break;
        case "ArrowDown":
          this.selectIndex = this.selectIndex == null ? 0 : this.selectIndex == this.list.length - 1 ? null : this.selectIndex + 1;
          this.stopDefault();
          break;
        case "Enter":
          if (this.searchBookmark) {
            this.itemClick(e, this.selectIndex);
          } else {
            this.searchClick();
          }
          break;
        case "Tab":
          this.searchBookmark = !this.searchBookmark;
          if (this.searchBookmark) {
            this.$message.info("书签搜索");
          } else {
            this.$message.info("全网搜索");
          }
          this.stopDefault();
          break;
        case "Escape":
          this.init();
          this.$refs["searchInput"].blur();
      }
    },
    dealSearch(content) {
      let res = [];
      let arrs = Object.values(this.totalTreeData);
      for (let i1 = 0, length1 = arrs.length; i1 < length1; i1++) {
        for (let i2 = 0, length2 = arrs[i1].length; i2 < length2; i2++) {
          let item = arrs[i1][i2];
          if (item.type === 1) {
            continue;
          }
          if (item.searchKey.indexOf(content) > -1) {
            res.push(item);
            if (res.length >= 12) {
              return res;
            }
          }
        }
      }
      return res;
    },
    /**
     * 阻止默认事件
     */
    stopDefault(e) {
      //阻止默认浏览器动作(W3C)
      if (e && e.preventDefault) {
        e.preventDefault();
      } else {
        window.event.returnValue = false;
      }
      return false;
    },
  },
};
</script>

<style lang="less" scoped>
.search {
  position: relative;
  .searchContent {
    position: absolute;
    width: 100%;
    background: white;
    z-index: 1;
    border: 1px solid black;
    border-top: 0;
    padding: 5px;
    border-radius: 5px;
    .listItem {
      font-size: 0.16rem;
      display: flex;
      height: 0.3rem;
      line-height: 0.3rem;
      align-items: center;
      .listItemUrl {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    .itemActive {
      background-color: #aca6a6;
    }
  }
}
</style>