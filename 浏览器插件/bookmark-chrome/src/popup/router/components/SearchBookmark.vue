<template>
  <div>
    <el-popover placement="bottom-start" width="550" popper-class="popover" trigger="manual" :visible-arrow="false" v-model="isShow">
      <div>
        <div class="item" v-for="item in searchList" :key="item.bookmarkId">
          <a target="_blank" :href="item.url">{{ item.name }}</a>
        </div>
      </div>
      <el-input
        ref="searchInput"
        type="text"
        placeholder="搜索"
        v-model="searchContent"
        clearable
        :autofocus="true"
        slot="reference"
        @blur="clear"
        @keyup.enter.native="searchKey"
        @focus="focus"
      >
        <el-button slot="append" icon="el-icon-search" @click="searchKey"></el-button>
      </el-input>
    </el-popover>
    <div class="searchResult"></div>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'searchBookmark',
  data() {
    return {
      searchContent: '',
      // 是否展示结果列表
      isShow: false,
      // 是否有焦点
      isFocus: false,
      searchList: [],
      timeOut: null,
    };
  },
  watch: {
    searchContent(newVal, oldVal) {
      if (newVal.trim().length === 0) {
        return;
      }
      if (newVal.trim() !== oldVal) {
        if (this.timeOut != null) {
          clearTimeout(this.timeOut);
        }
        this.searchList = [];
        this.timeOut = setTimeout(async () => {
          this.searchList = await axios.get(`bookmark/searchUserBookmark?content=${newVal}`);
          this.isShow = this.searchList.length > 0;
          this.timeOut = null;
        }, 200);
      }
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs['searchInput'].focus();
    });
  },
  methods: {
    searchKey() {
      window.open('https://www.baidu.com/s?ie=UTF-8&wd=' + window.encodeURIComponent(this.searchContent));
    },
    focus() {
      this.isFocus = true;
    },
    clear() {
      this.isFocus = false;
      if (this.timeOut != null) {
        clearTimeout(this.timeOut);
      }
      this.searchContent = '';
      this.searchList = [];
      this.isShow = false;
    },
  },
};
</script>

<style scoped>
.item {
  width: 500px;
  padding: 5px;
  border-bottom: 1px solid black;
}
</style>
