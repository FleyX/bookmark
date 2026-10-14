<template>
  <div class="ssoMain">{{ message }}</div>
</template>

<script>
import { useGlobalConfigStore } from "@/store/globalConfig";
export default {
  name: "SsoAuth",
  data() {
    return {
      message: "loading",
    };
  },
  mounted() {
    window.addEventListener("message", (event) => {
      if (!event.data.code) {
        return;
      }
      console.log("收到content消息", event);
      if (event.data.code == "setTokenOk") {
        this.message = "登陆成功，3s后关闭本页面";
        setTimeout(() => window.close(), 3000);
      }
    });

    let token = useGlobalConfigStore().token;
    window.postMessage({ code: "setToken", data: token }, "*");
  },
  unmounted() {
    window.removeEventListener("message");
  },
};
</script>

<style lang="less" scoped>
.ssoMain {
  text-align: center;
}
</style>
