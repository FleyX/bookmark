<template>
  <div class="userInfo">
    <div class="icon">
      <img :src="userInfo.icon" class="full" />
      <label class="full">
        <input type="file" style="display: none" @change="changeIcon" />
        <div class="full changeIcon">
          <span>编辑</span>
        </div>
      </label>
    </div>
    <div class="baseInfo">
      <div class="item">

        <a-tooltip title="点击修改" v-if="currentAction!='name'">
          <span style="font-size:2em;cursor: pointer;" @click="()=>this.currentAction='name'">{{userInfo.username}}</span>
        </a-tooltip>
        <div class="inputGroup" v-else-if="currentAction==='name'">
          <a-input type="text" v-model="name" placeholder="修改昵称" />
          <div style="padding-top:0.2em">
            <a-button style="margin-right:2em" type="warn" @click="()=>this.currentAction=null">取消</a-button>
            <a-button type="primary" @click="submit">确定</a-button>
          </div>
        </div>
      </div>

      <div class="item">
        <span style="width:5em">密码</span>
        <a-tooltip title="点击修改" v-if="currentAction!='password'">
          <span style="cursor: pointer;" @click="()=>this.currentAction='password'">{{userInfo.noPassword?'设置密码' : '**********'}}</span>
        </a-tooltip>
        <div class="inputGroup" v-else-if="currentAction==='password'">
          <a-input type="password" v-model="oldPassword" placeholder="旧密码(如无置空)" />
          <a-input type="password" v-model="password" placeholder="新密码" />
          <a-input type="password" v-model="rePassword" placeholder="重复新密码" />
          <div style="padding-top:0.2em">
            <a-button style="margin-right:2em" type="warn" @click="()=>this.currentAction=null">取消</a-button>
            <a-button type="primary" @click="submit">确定</a-button>
          </div>
        </div>
      </div>

      <div class="item">
        <span style="width:5em">邮箱</span>
        <a-tooltip title="点击修改" v-if="currentAction!='email'">
          <span style="cursor: pointer;" @click="()=>this.currentAction='email'">{{userInfo.email}}</span>
        </a-tooltip>
        <div class="inputGroup" v-else-if="currentAction==='email'">
          <a-input type="password" v-model="oldPassword" placeholder="旧密码" />
          <a-input type="email" v-model="email" placeholder="email" />
          <div style="padding-top:0.2em">
            <a-button style="margin-right:2em" type="warn" @click="()=>this.currentAction=null">取消</a-button>
            <a-button type="primary" @click="submit">确定</a-button>
          </div>
        </div>
      </div>

      <div class="item">
        <a-tooltip title="搜索框默认搜索引擎">
          <span style="width:5em">搜索</span>
        </a-tooltip>
        <a-tooltip title="点击修改" v-if="currentAction!='defaultSearchEngine'">
          <span style="cursor: pointer;" @click="()=>this.currentAction='defaultSearchEngine'">{{defaultSearchEngine}}</span>
        </a-tooltip>
        <div class="inputGroup" v-else-if="currentAction==='defaultSearchEngine'">
          <a-select :default-value="userInfo.defaultSearchEngine" style="width:100%" @change="submit">
            <a-select-option value="baidu">
              百度
            </a-select-option>
            <a-select-option value="google">
              谷歌
            </a-select-option>
            <a-select-option value="bing">
              Bing
            </a-select-option>
          </a-select>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import HttpUtil from "../../../../util/HttpUtil";
export default {
  name: "UserInfo",
  data() {
    return {
      currentAction: null, //当前操作,name,password,email
      name: "",
      oldPassword: "",
      password: "",
      rePassword: "",
      email: "",
    };
  },
  computed: {
    ...mapState("globalConfig", ["userInfo"]),
    defaultSearchEngine() {
      switch (this.userInfo.defaultSearchEngine) {
        case "baidu":
          return "百度";
        case "google":
          return "谷歌";
        case "bing":
          return "Bing";
      }
      return "";
    },
  },
  methods: {
    async changeIcon(e) {
      let file = e.target.files[0];
      if (!file || file.size > 200 * 1024) {
        message.error("文件大小请勿超过200KB");
        return;
      }
      let formData = new FormData();
      formData.append("file", file);
      let res = await HttpUtil.post("/user/icon", null, formData, true);
      this.$set(this.userInfo, "icon", res);
    },
    async submit(e) {
      let url, body;
      if (this.currentAction === "name") {
        url = "/baseInfo/username";
        body = { username: this.name };
      } else if (this.currentAction === "password") {
        if (!this.password === this.rePassword) {
          this.$message.error("新密码两次输入不一致");
          return;
        }
        url = "/baseInfo/password";
        body = {
          oldPassword: this.oldPassword,
          password: this.password,
        };
      } else if (this.currentAction === "email") {
        url = "/baseInfo/email";
        body = { oldPassword: this.oldPassword, email: this.email };
      } else if (this.currentAction === "defaultSearchEngine") {
        url = "/baseInfo/updateSearchEngine";
        body = { defaultSearchEngine: e };
      }
      await HttpUtil.post(url, null, body);
      await this.$store.dispatch("globalConfig/refreshUserInfo");
      this.$message.success("操作成功");
      this.currentAction = null;
    },
  },
};
</script>

<style lang="less" scoped>
.userInfo {
  min-width: 50%;
  max-width: 30em;
  margin: 0 auto;
  display: flex;

  .icon {
    position: relative;
    border-radius: 5px;
    margin-right: 1em;
    cursor: pointer;
    @media (min-width: 768px) {
      width: 150px;
      height: 150px;
    }
    @media (max-width: 768px) {
      width: 75px;
      height: 75px;
    }

    .full {
      position: absolute;
      width: 100%;
      height: 100%;
    }

    .changeIcon {
      background-color: transparent;
      color: rgba(173, 166, 166, 0);
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 2em;
    }

    .changeIcon:hover {
      // background-color: rbga(173, 166, 166, 1);
      background-color: rgba(173, 166, 166, 0.8) !important;
      color: white;
    }
  }

  .item {
    font-size: 1.5em;
    padding-top: 1em;
    padding-bottom: 1em;
    border-bottom: 1px solid #ebebeb;

    display: flex;
    .inputGroup {
      flex: 1;
    }
  }

  .baseInfo {
    flex: 1;
  }
}
</style>
