<template>
  <div class="main">
    <div class="main-body">
      <login-register-header type="login"></login-register-header>
    </div>
  </div>
</template>

<script>
import LoginRegisterHeader from "../../components/public/LoginRegisterHeader.vue";

export default {
  name: "Login",
  components: { LoginRegisterHeader }
};
</script>

<style lang="less" scoped>
@import "../../global.less";
.main {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  .main-body {
    width: 5rem;
    height: 3.5rem;
    background-color: @loginBgColor;
    border-radius: 5px;
    padding: 0.1rem;
  }
}
</style>
