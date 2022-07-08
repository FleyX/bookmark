<template>
  <div class="top">
    <div></div>
    <div v-if="userInfo == null">
      <router-link to="/public/login">登陆</router-link>
      /
      <router-link to="/public/register">注册</router-link>
    </div>
    <div v-else class="topAction">
      <a-tooltip style="margin-right: 1em">
        <template #title>书签管理</template>
        <router-link to="/manage">
          <a-icon class="bookmarkIcon" type="setting" />
        </router-link>
      </a-tooltip>
      <a-dropdown>
        <div class="user">
          <img :src="userInfo.icon" class="userIcon" />
        </div>
        <a-menu slot="overlay" :trigger="['hover', 'click']" @click="menuClick">
          <a-menu-item key="personSpace">
            <router-link to="/manage/personSpace/userInfo">个人中心</router-link>
          </a-menu-item>
          <a-menu-item key="logout">
            <a href="javascript:;">退出</a>
          </a-menu-item>
        </a-menu>
      </a-dropdown>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { logoutClear } from "@/store/index";
export default {
  name: "homeTop",
  data() {
    return {};
  },
  computed: {
    ...mapState("globalConfig", ["userInfo"]),
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

    .bookmarkIcon {
      font-size: 2em;
      background-color: rgb(74, 74, 74, 0.5);
      color: rgba(255, 255, 255, 0.8);
    }
  }
}
</style>
