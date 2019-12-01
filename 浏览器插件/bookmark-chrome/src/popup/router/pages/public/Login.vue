<template>
  <div class="main">您还未登陆授权<br />
    <el-button type="primary" @click="go">前往授权页面</el-button>
  </div>
</template>

<script>
import config from '../../../../util/config';
export default {
  name: 'login',
  data() {
    return {
    };
  },
  mounted() {
    this.checkToken();
  },
  methods: {
    go() {
      window.open(config.ssoUrl);
    },
    // 循环检测token是否生效
    checkToken() {
      console.log('检测token是否获取到了');
      let token = localStorage.getItem('token');
      if (token == null || token.length === 0) {
        setTimeout(this.checkToken.bind(this), 1000);
      } else {
        this.$router.replace('/');
      }
    },
  },
};
</script>

<style scoped>
</style>
