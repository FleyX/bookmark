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
        <span>昵称</span>
        <span>{{userInfo.name}}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import HttpUtil from "../../../../util/HttpUtil";
export default {
  name: "UserInfo",
  computed: {
    ...mapState("globalConfig", ["userInfo"]),
  },
  methods: {
    async changeIcon(e) {
      let file = e.target.files[0];
      console.log(file);
      if (!file || file.size > 500 * 1024 * 8) {
        message.error("文件大小请勿超过500KB");
        return;
      }
      let formData = new FormData();
      formData.append("file", file);
      let res = await HttpUtil.post("/user/icon", null, formData, true);
      this.$set(this.userInfo, "icon", res);
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

  .baseInfo {
    flex: 1;
  }
}
</style>
