<template>
  <div class="ssoMain">{{ message }}</div>
</template>

<script>
import { GLOBAL_CONFIG, TOKEN } from "@/store/modules/globalConfig";
export default {
  name: "ssoPage",
  data() {
    return {
      message: "loading"
    };
  },
  mounted() {
    window.addEventListener("message", event => {
      if (!event.data.code) {
        return;
      }
      console.log("收到content消息", event);
      if (event.data.code == "setTokenOk") {
        this.message = "登陆成功，3s后关闭本页面";
        setTimeout(() => window.close(), 3000);
      }
    });

    let token = this.$store.state[GLOBAL_CONFIG][TOKEN];
    window.postMessage({ code: "setToken", data: token }, "*");
  },
  beforeDestroy() {
    window.removeEventListener("message");
  }
};
</script>

<style lang="less" scoped>
.ssoMain {
  text-align: center;
}
</style>
