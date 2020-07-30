<template>
  <div>
    <Header current="reset" />
    <div class="form">
      <a-form-model ref="resetPassword" :model="form" :rules="rules">
        <a-form-model-item prop="email">
          <a-input v-model="form.email" placeholder="邮箱">
            <a-icon slot="prefix" type="mail" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item prop="password">
          <a-input v-model="form.password" placeholder="新密码" type="password">
            <a-icon slot="prefix" type="lock" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item prop="repeatPass">
          <a-input v-model="form.repeatPass" placeholder="重复新密码" type="password">
            <a-icon slot="prefix" type="lock" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item prop="authCode" ref="authCode" :autoLink="false">
          <div class="authCodeGroup">
            <a-input v-model="form.authCode" placeholder="验证码" @change="() => $refs.authCode.onFieldChange()" />
            <a-button @click="getAuthCode" :disabled="countDown > 0">{{ countDown == 0 ? "获取验证码" : countDown + "秒后重试" }}</a-button>
          </div>
        </a-form-model-item>

        <a-form-model-item>
          <div class="btns">
            <a-button type="primary" block @click="submit">重置</a-button>
            <router-link to="login" replace>返回登陆</router-link>
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
  name: "ResetPassword",
  components: {
    Header
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
      countDown: 0, //小于等于0说明可点击获取验证码
      timer: null,
      form: {
        email: "",
        password: "",
        repeatPass: "",
        authCode: ""
      },
      rules: {
        authCode: [{ required: true, min: 6, max: 6, message: "请输入6位验证码", trigger: "change" }],
        email: [
          {
            pattern: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
            message: "请输入正确的邮箱",
            trigger: "change"
          }
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { pattern: "^\\w{6,18}$", message: "密码为6-18位数字,字母,下划线组合", trigger: "change" }
        ],
        repeatPass: [{ validator: repeatPass, trigger: "change" }]
      }
    };
  },
  methods: {
    async getAuthCode() {
      this.$refs.resetPassword.validateField("email", async message => {
        if (message === "") {
          try {
            this.countDown = 60;
            if (this.timer != null) {
              clearInterval(this.timer);
            }
            this.timer = setInterval(() => {
              if (this.countDown > 0) {
                this.countDown = this.countDown - 1;
              } else {
                clearInterval(this.timer);
              }
            }, 1000);
            await httpUtil.get("/user/authCode", { email: this.form.email });
            this.$message.success("发送成功，请查收(注意垃圾箱)");
          } catch (error) {
            this.countDown = 0;
          }
        }
      });
    },
    submit() {
      this.$refs.resetPassword.validate(async status => {
        if (status) {
          let res = await httpUtil.post("/user/resetPassword", null, this.form);
          this.$message.success("重置成功");
          this.$router.replace("login");
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
  .authCodeGroup {
    display: flex;
    justify-content: space-between;
  }
  .btns {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
}
</style>
