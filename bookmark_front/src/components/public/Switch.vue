<template>
  <div class="main">
    <template v-if="current !== 'reset'">
      <span @click="go('login')" :class="[{ current: current == 'login' }, 'item']">登录</span>
      <span class="pot"></span>
      <span @click="go('register')" :class="[{ current: current == 'register' }, 'item']">注册</span>
    </template>
    <template v-else><span class="item">重置密码</span></template>
  </div>
</template>

<script>
export default {
  name: "PublicSwitch",
  props: {
    current: {
      type: String,
      default: "login"
    }
  },
  methods: {
    go(target) {
      this.$router.replace(target);
    }
  }
};
</script>

<style lang="less" scoped>
.main {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.25rem;
  color: #969696;
  .current {
    color: #1890ff !important;
    border-bottom: 2px solid #1890ff;
  }
  .item {
    cursor: pointer;
  }
  .pot {
    background-color: #1890ff;
    width: 0.05rem;
    height: 0.05rem;
    border-radius: 50%;
    margin: 0.15rem;
  }
}
</style>
