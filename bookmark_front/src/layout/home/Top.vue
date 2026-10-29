<template>
  <div class="top">
    <div></div>
    <div v-if="userInfo == null">
      <router-link to="/public/login">登陆</router-link>
      /
      <router-link to="/public/register">注册</router-link>
    </div>
    <div v-else class="topAction">
      <a-dropdown>
        <div class="user">
          <img :src="userInfo.icon" class="userIcon" />
        </div>
        <template #overlay>
          <a-menu @click="menuClick">
            <a-menu-item key="manage">
              <router-link to="/manage">书签管理</router-link>
            </a-menu-item>
            <a-menu-item v-if="hasAnyAdminResource" key="admin">
              <router-link to="/admin">管理后台</router-link>
            </a-menu-item>
            <a-menu-item key="personSpace">
              <router-link to="/manage/personSpace/userInfo">个人中心</router-link>
            </a-menu-item>
            <a-menu-item key="logout">
              <a href="javascript:;">退出</a>
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>
  </div>
</template>

<script>
import { mapState } from "pinia";
import { useGlobalConfigStore } from "@/store/globalConfig";
import { useUserStore } from "@/store/user";
import { logoutClear } from "@/store/index";
export default {
  name: "homeTop",
  computed: {
    ...mapState(useGlobalConfigStore, ["userInfo"]),
    ...mapState(useUserStore, ["hasAnyAdminResource"]),
  },
  methods: {
    async menuClick(item) {
      if (item.key == "logout") {
        await logoutClear();
      }
    },
  },
};
</script>

<style lang="less" scoped>
.top {
  height: 0.8rem;
  overflow: hidden;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-left: 0.4rem;
  padding-right: 0.4rem;

  .userIcon {
    border-radius: 50%;
    width: 2.5em;
    height: 2.5em;
  }

  .topAction {
    display: flex;
    align-items: center;
  }
}
</style>
