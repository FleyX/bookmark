<template>
  <div>
    <Header current="login" />
    <div class="form">
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
        <a-form-model-item prop="password">
          <div class="reset">
            <a-checkbox v-model="form.rememberMe">记住我</a-checkbox>
            <router-link to="resetPassword" replace>重置密码</router-link>
          </div>
        </a-form-model-item>

        <a-form-model-item>
          <div class="btns">
            <a-button type="primary" block @click="submit">登录</a-button>
          </div>
        </a-form-model-item>
      </a-form-model>
    </div>
  </div>
</template>

<script>
import Header from "@/components/public/Switch.vue";
import httpUtil from "../../../util/HttpUtil.js";
import { mapMutations } from "vuex";
export default {
  name: "Login",
  components: {
    Header
  },
  data() {
    return {
      form: {
        str: "",
        password: "",
        rememberMe: false
      },
      rules: {
        str: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { min: 1, max: 50, message: "最短1，最长50", trigger: "change" }
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { pattern: "^\\w{6,18}$", message: "密码为6-18位数字,字母,下划线组合", trigger: "change" }
        ]
      }
    };
  },
  methods: {
    ...mapMutations("globalConfig", ["setUserInfo", "setToken"]),
    submit() {
      this.$refs.loginForm.validate(async status => {
        if (status) {
          let res = await httpUtil.post("/user/login", null, this.form);
          this.setUserInfo(res.user);
          this.$store.commit("globalConfig/setToken", res.token);
          this.$router.replace("/");
        }
      });
    }
  }
};
</script>

<style lang="less" scoped>
.form {
  margin: 0.3rem;
  .reset {
    display: flex;
    justify-content: space-between;
  }
}
</style>
