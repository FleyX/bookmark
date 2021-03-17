<template>
  <div>
    <Header current="register" />
    <div class="form">
      <a-form-model ref="registerForm" :model="form" :rules="rules">
        <a-form-model-item prop="username">
          <a-input v-model="form.username" placeholder="用户名">
            <a-icon slot="prefix" type="user" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item prop="email">
          <a-input v-model="form.email" placeholder="邮箱">
            <a-icon slot="prefix" type="password" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item prop="password">
          <a-input v-model="form.password" placeholder="密码" type="password">
            <a-icon slot="prefix" type="password" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item prop="repeatPass">
          <a-input v-model="form.repeatPass" placeholder="重复密码" type="password">
            <a-icon slot="prefix" type="password" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>

        <a-form-model-item>
          <div class="btns">
            <a-button type="primary" block @click="submit">注册</a-button>
          </div>
        </a-form-model-item>
      </a-form-model>
    </div>
  </div>
</template>

<script>
import Header from "@/components/public/Switch.vue";
import httpUtil from "../../../util/HttpUtil.js";
export default {
  name: "Login",
  components: {
    Header,
  },
  data() {
    let repeatPass = (rule, value, cb) => {
      if (value === "") {
        cb(new Error("请再次输入密码"));
      } else if (value !== this.form.password) {
        cb(new Error("两次密码不一致"));
      } else {
        cb();
      }
    };
    return {
      form: {
        username: "",
        email: "",
        password: "",
        repeatPass: "",
      },
      rules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { min: 1, max: 50, message: "最短1，最长50", trigger: "blur" },
        ],
        email: [
          {
            pattern: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
            message: "请输入正确的邮箱",
            trigger: "change",
          },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { pattern: "^\\w{6,18}$", message: "密码为6-18位数字,字母,下划线组合", trigger: "change" },
        ],
        repeatPass: [{ validator: repeatPass, trigger: "change" }],
      },
    };
  },
  async created() {
    //进入注册、登录页需要清理掉所有的缓存数据
    await this.$store.dispatch("treeData/clear");
    await this.$store.dispatch("globalConfig/clear");
  },
  methods: {
    submit() {
      let _this = this;
      this.$refs.registerForm.validate(async (status) => {
        if (status) {
          let res = await httpUtil.put("/user", null, _this.form);
          this.$store.dispatch("globalConfig/setToken", res);
          this.$router.replace("/");
        }
      });
    },
  },
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
