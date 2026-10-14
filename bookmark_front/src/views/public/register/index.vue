<template>
  <div>
    <PublicSwitch current="register" />
    <div class="form">
      <a-form ref="registerForm" :model="form" :rules="rules">
        <a-form-item name="username">
          <a-input v-model:value="form.username" placeholder="用户名">
            <template #prefix>
              <UserOutlined style="color: rgba(0, 0, 0, 0.25)" />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item name="email">
          <a-input v-model:value="form.email" placeholder="邮箱">
            <template #prefix>
              <LockOutlined style="color: rgba(0, 0, 0, 0.25)" />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item name="password">
          <a-input v-model:value="form.password" placeholder="密码" type="password">
            <template #prefix>
              <LockOutlined style="color: rgba(0, 0, 0, 0.25)" />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item name="repeatPass">
          <a-input v-model:value="form.repeatPass" placeholder="重复密码" type="password">
            <template #prefix>
              <LockOutlined style="color: rgba(0, 0, 0, 0.25)" />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item>
          <div class="btns">
            <a-button type="primary" block @click="submit">注册</a-button>
          </div>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script>
import PublicSwitch from "@/components/public/Switch.vue";
import httpUtil from "@/util/HttpUtil.js";
import { LockOutlined, UserOutlined } from "@ant-design/icons-vue";
import { useGlobalConfigStore } from "@/store/globalConfig";

export default {
  name: "Register",
  components: {
    PublicSwitch,
    LockOutlined,
    UserOutlined,
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
            pattern:
              /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
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
  methods: {
    async submit() {
      try {
        await this.$refs.registerForm.validate();
      } catch {
        return;
      }
      let res = await httpUtil.put("/user", null, this.form);
      await useGlobalConfigStore().setToken(res);
      this.$router.replace("/");
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
