<template>
  <div>
    <Header current="login" />
    <a-spin class="form" :delay="100" :spinning="loading">
      <a-form-model ref="loginForm" :model="form" :rules="rules">
        <a-form-model-item prop="str" ref="str">
          <a-input v-model="form.str" placeholder="邮箱/用户名">
            <a-icon slot="prefix" type="user" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item prop="password">
          <a-input v-model="form.password" placeholder="密码" type="password">
            <a-icon slot="prefix" type="password" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <div class="reset">
          <a-checkbox v-model="form.rememberMe">记住我</a-checkbox>
          <router-link to="resetPassword" replace>重置密码</router-link>
        </div>

        <a-form-model-item>
          <div class="btns">
            <a-button type="primary" block @click="submit">登录</a-button>
          </div>
          <div class="thirdPart">
            <span>第三方登陆</span>
            <a-tooltip title="github登陆" class="oneIcon" placement="bottom">
              <a-icon type="github" @click="toGithub" style="font-size:1.4em" />
            </a-tooltip>
          </div>
        </a-form-model-item>
      </a-form-model>
    </a-spin>
  </div>
</template>

<script>
import Header from "@/components/public/Switch.vue";
import httpUtil from "../../../util/HttpUtil.js";
import { mapMutations } from "vuex";
import HttpUtil from "../../../util/HttpUtil.js";
export default {
  name: "Login",
  components: {
    Header,
  },
  data() {
    return {
      form: {
        str: "",
        password: "",
        rememberMe: false,
      },
      rules: {
        str: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { min: 1, max: 50, message: "最短1，最长50", trigger: "change" },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { pattern: "^\\w{6,18}$", message: "密码为6-18位数字,字母,下划线组合", trigger: "change" },
        ],
      },
      loading: false, //是否加载中
      oauthLogining: false, //true:正在进行oauth后台操作
      page: null, //oauth打开的页面实例
    };
  },
  async created() {
    let _this = this;
    window.addEventListener("storage", this.storageDeal.bind(this));

    //进入注册、登录页需要清理掉所有的缓存数据
    await this.$store.dispatch("treeData/clear");
    await this.$store.dispatch("globalConfig/clear");
  },
  destroyed() {
    window.removeEventListener("storage", this.storageDeal);
  },
  methods: {
    ...mapMutations("globalConfig", ["setUserInfo", "setToken"]),
    submit() {
      this.$refs.loginForm.validate(async (status) => {
        if (status) {
          try {
            this.loading = true;
            let token = await httpUtil.post("/user/login", null, this.form);
            this.$store.dispatch("globalConfig/setToken", token);
            this.$router.replace("/");
          } finally {
            this.loading = false;
          }
        }
      });
    },
    toGithub() {
      let redirect = location.origin + "/public/oauth/github";
      let clientId = process.env.VUE_APP_GITHUB_CLIENT_ID;
      this.page = window.open(`https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${redirect}`);
    },
    async storageDeal(e) {
      console.log(e);
      if (e.key === "oauthMessage") {
        if (this.page != null) {
          this.page.close();
        }
        localStorage.removeItem("oauthMessage");
        await this.oauthLogin(e.newValue);
      }
    },
    async oauthLogin(form) {
      if (this.loading) {
        console.error("正在请求中", form);
        return;
      }
      form = JSON.parse(form);
      if (!form.code) {
        this.$message.error("您已拒绝，无法继续登陆");
        return;
      }
      try {
        this.loading = true;
        form.rememberMe = this.form.rememberMe;
        let token = await HttpUtil.post("/user/oAuthLogin", null, form);
        this.$store.dispatch("globalConfig/setToken", token);
        this.$router.replace("/");
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style lang="less" scoped>
.form {
  margin: 0.3rem;
  margin-bottom: 0.1rem;
  .reset {
    display: flex;
    justify-content: space-between;
  }
}
.thirdPart {
  display: flex;
  justify-content: space-between;
  font-size: 1.2em;
  align-items: center;
}
</style>
