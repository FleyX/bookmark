<template>
  <div class="main" v-if="userInfo">
    <a class="ico" href="/"><img src="/static/img/bookmarkLogo.png" style="height: 100%" /></a>
    <a-dropdown>
      <div class="user">
        <img :src="userInfo.icon" class="userIcon" />
        <span class="name">{{ userInfo.username }}</span>
      </div>
      <template #overlay>
        <a-menu @click="menuClick">
          <a-menu-item key="manage">
            <a href="javascript:;">书签管理</a>
          </a-menu-item>
          <a-menu-item key="personSpace">
            <a href="javascript:;">个人中心</a>
          </a-menu-item>
          <a-menu-item key="logout">
            <a href="javascript:;">退出</a>
          </a-menu-item>
        </a-menu>
      </template>
    </a-dropdown>
  </div>
</template>

<script>
import { mapState } from "pinia";
import { useGlobalConfigStore } from "@/store/globalConfig";
import { logoutClear } from "@/store/index";
export default {
  name: "manageTop",
  computed: {
    ...mapState(useGlobalConfigStore, ["userInfo"]),
  },
  methods: {
    async menuClick({ key }) {
      if (key === "logout") {
        await logoutClear();
        this.$router.replace("/public/login");
      } else if (key === "personSpace") {
        this.$router.push("/manage/personSpace/userInfo");
      } else if (key === "manage") {
        this.$router.push("/manage");
      }
    },
  },
};
</script>

<style lang="less" scoped>
@import (reference) "../../assets/global.less";
.main {
  position: fixed;
  top: 0;
  width: 100%;
  height: @topHeight;
  padding: @topPaddingTop 3% @topPaddingTop 3%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(197, 190, 198, 0.4);
  z-index: 100;
  .ico {
    display: flex;
    height: 100%;
  }
  .user {
    font-size: 0.3rem;
    display: flex;
    align-items: center;
    cursor: pointer;
    .userIcon {
      width: @topHeight - 0.1rem;
      height: @topHeight - 0.1rem;
      border-radius: 50%;
    }
    .name {
      font-size: 0.2rem;
    }
  }
}
</style>
