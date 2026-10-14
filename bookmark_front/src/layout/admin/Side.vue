<template>
  <div class="side">
    <router-link class="logo" to="/">
      <img class="ico" src="/static/img/bookmarkLogo.png" />
      <span class="title">签签世界</span>
    </router-link>
    <div class="menu">
      <a-menu mode="inline" :selectedKeys="selectedKeys" @click="menuClick">
        <a-menu-item v-for="item in visibleMenus" :key="item.key">{{ item.label }}</a-menu-item>
      </a-menu>
    </div>
    <div class="user" v-if="userInfo">
      <img class="userIcon" :src="userInfo.icon" />
      <span class="name">{{ userInfo.username }}</span>
      <router-link class="back" to="/">返回书签</router-link>
    </div>
  </div>
</template>

<script>
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";

/**
 * 菜单项，resource为展示该项所需的VIEW:资源
 */
const MENUS = [
  { key: "/admin/user", label: "用户管理", resource: "VIEW:/admin/user" },
  { key: "/admin/role", label: "角色管理", resource: "VIEW:/admin/role" },
];

export default {
  name: "AdminSide",
  data() {
    return {
      menus: MENUS,
    };
  },
  computed: {
    ...mapState(useUserStore, ["userInfo", "resources"]),
    selectedKeys() {
      return [this.$route.path];
    },
    visibleMenus() {
      return this.menus.filter((item) => this.resources.includes(item.resource));
    },
  },
  methods: {
    menuClick({ key }) {
      this.$router.push(key);
    },
  },
};
</script>

<style lang="less" scoped>
@import (reference) "../../assets/global.less";

.side {
  width: 2.2rem;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: @bgColor;

  .logo {
    height: @topHeight;
    display: flex;
    align-items: center;
    padding: 0 0.1rem;

    .ico {
      height: 0.24rem;
    }
    .title {
      margin-left: 0.1rem;
      font-size: 0.18rem;
    }
  }

  .menu {
    flex: 1;
    overflow: auto;
  }

  .user {
    height: 0.6rem;
    padding: 0 0.1rem;
    display: flex;
    align-items: center;
    border-top: 1px solid rgba(0, 0, 0, 0.1);

    .userIcon {
      width: 0.3rem;
      height: 0.3rem;
      border-radius: 50%;
    }
    .name {
      flex: 1;
      margin-left: 0.08rem;
      font-size: 0.14rem;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .back {
      font-size: 0.12rem;
    }
  }
}
</style>
