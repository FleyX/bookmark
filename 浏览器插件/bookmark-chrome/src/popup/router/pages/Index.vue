<template>
  <div>
    <div class="head">
      <img width="30%" src="/static/img/bookmarkLogo.png" alt="icon" />
      <span>{{ personInfo.username }}</span>
    </div>
    <!-- 书签检索 -->
    <div class="search">
      <el-input placeholder="关键词检索" v-model="searchContent">
        <el-button slot="append" icon="el-icon-search"></el-button>
      </el-input>
      <div class="searchResult">
        <div class="item" v-for="item in searchList" :key="item.bookmarkId">
          <a target="_blank" :href="item.url">{{ item.name }}</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  data() {
    return {
      personInfo: {},
      searchContent: '',
      searchList: [],
      timeOut: null,
    };
  },
  watch: {
    searchContent(newVal, oldVal) {
      if (newVal.trim() !== oldVal) {
        if (this.timeOut != null) {
          clearTimeout(this.timeOut);
        }
        this.searchList = [];
        this.timeOut = setTimeout(async () => {
          this.searchList = await axios.get(`bookmark/searchUserBookmark?content=${newVal}`);
          this.timeOut = null;
        }, 200);
      }
    },
  },
  created() {
    window.token = localStorage.getItem('token');
    this.init();
  },
  methods: {
    async init() {
      let personInfo = await axios.get('/user/currentUserInfo');
      window.personInfo = personInfo;
      this.personInfo = personInfo;
    },
  },
};
</script>

<style lang="scss" scoped>
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 5% 0 5%;
}
.searchResult {
}
.item {
  padding: 5px;
  border-bottom: 1px solid black;
}
</style>
