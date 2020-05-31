<template>
  <div>
    <div class="head">
      <img width="30%" src="/static/img/bookmarkLogo.png" alt="icon" />
      <span>{{ personInfo.username }}</span>
    </div>
    <!-- 书签检索 -->
    <search />
  </div>
</template>

<script>
import axios from 'axios';
import Search from '../components/SearchBookmark';
export default {
  components: {
    Search,
  },
  data() {
    return {
      personInfo: {},
    };
  },

  created() {
    window.token = localStorage.getItem('token');
    if (!window.token) {
      this.$router.replace('/public/login');
    } else {
      this.init();
    }
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
</style>
