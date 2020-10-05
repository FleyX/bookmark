<template>
  <div class="search">
    <a-input-search
      ref="searchInput"
      size="large"
      style="width: 100%"
      v-model="value"
      enter-button
      @change="search"
      @search="searchClick"
      allowClear
      @blur.prevent="inputBlur"
      @focus="inputFocus"
    />
    <div v-if="focused" class="searchContent">
      <a-empty v-if="list.length == 0" />
      <div
        class="listItem"
        v-for="(item, index) in list"
        :key="item.bookmarkId"
        @mouseenter="mouseEnterOut(index, 'enter')"
        @mouseleave="mouseEnterOut(index, 'leave')"
        @mouseup="onMouse"
        @click="itemClick(item)"
      >
        <a style="min-width: 4em" :href="item.url" target="_blank">
          {{ item.name }}
        </a>
        <a-tooltip v-if="showActions && hoverIndex === index" title="定位到书签树中">
          <my-icon style="color: #40a9ff; font-size: 1.3em; cursor: pointer" type="icon-et-location" @click="location(item)" />
        </a-tooltip>
      </div>
    </div>
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
      hoverIndex: null,
    };
  },
  computed: {
    ...mapState("treeData", ["totalTreeData"]),
  },
  methods: {
    search(content) {
      content = content.target.value;
      content = content.toLocaleLowerCase().trim();
      this.value = content;
      if (content === "") {
        this.list = [];
        return;
      }
      let time1 = Date.now();
      this.list = this.dealSearch(content);
      console.info("搜索耗时：" + (Date.now() - time1));
    },
    searchClick() {
      if (this.timer != null) {
        clearTimeout(this.timer);
      }
    },
    itemClick(item) {
      HttpUtil.post("/bookmark/visitNum", { id: item.bookmarkId });
    },
    inputBlur() {
      console.log("blur");
      this.timer = setTimeout(() => (this.focused = false), 300);
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
      console.log(item, type);
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
    .listItem {
      font-size: 0.16rem;
      display: flex;
      height: 0.3rem;
      align-items: center;
    }
  }
}
</style>