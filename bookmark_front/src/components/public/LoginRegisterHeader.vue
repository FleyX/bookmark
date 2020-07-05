<template>
  <div class="location">
    <template v-if="type !== 'reset'">
      <span :class="[{ current: type === 'login' }, 'text']">登陆</span>
      <span class="point"></span>
      <span class="text">注册</span>
    </template>
    <template v-else>
      <span class="text">重置密码</span>
    </template>
  </div>
</template>

<script>
export default {
  name: "LoginRegisterHeader",
  props: {
    type: String
  }
};
</script>

<style lang="less" scoped>
@textColor: #969696;
.location {
  display: flex;
  justify-content: center;
  align-items: center;
  color: @textColor;
  .point {
    width: 0.05rem;
    height: 0.05rem;
    border-radius: 50%;
    background-color: @textColor;
    margin-left: 0.1rem;
    margin-right: 0.1rem;
  }
  .text {
    font-size: 0.4rem;
    vertical-align: middle;
    line-height: 0.4rem;
    font-weight: 600;
  }
}
.current {
  color: black;
}
</style>
