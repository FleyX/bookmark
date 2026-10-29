<template>
  <div class="side">
    <router-link class="logo" to="/" aria-label="签签世界 管理后台">
      <span class="brandMark" aria-hidden="true"><BookOutlined /></span>
      <span class="brandText">
        <span class="title">签签世界</span>
        <span class="subtitle">管理后台</span>
      </span>
    </router-link>
    <div class="menu">
      <a-menu mode="inline" :selectedKeys="selectedKeys" @click="menuClick">
        <a-menu-item v-for="item in visibleMenus" :key="item.key" :title="item.label">
          <template #icon><component :is="item.icon" aria-hidden="true" /></template>
          {{ item.label }}
        </a-menu-item>
      </a-menu>
    </div>
    <div class="user" v-if="userInfo">
      <img class="userIcon" :src="userInfo.icon" :alt="`${userInfo.username} 的头像`" />
      <span class="name" :title="userInfo.username">{{ userInfo.username }}</span>
      <router-link class="back" to="/" title="返回书签" aria-label="返回书签">
        <HomeOutlined class="backIcon" />
        <span class="backText">返回书签</span>
      </router-link>
    </div>
  </div>
</template>

<script>
import { markRaw } from "vue";
import { mapState } from "pinia";
import { BookOutlined, HomeOutlined, TeamOutlined, UserOutlined } from "@ant-design/icons-vue";
import { useUserStore } from "@/store/user";

/**
 * 菜单项，resource为展示该项所需的VIEW:资源
 */
const MENUS = [
  { key: "/admin/user", label: "用户管理", resource: "VIEW:/admin/user", icon: markRaw(UserOutlined) },
  { key: "/admin/role", label: "角色管理", resource: "VIEW:/admin/role", icon: markRaw(TeamOutlined) },
];

export default {
  name: "AdminSide",
  components: { BookOutlined, HomeOutlined },
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

@sideWidth: 2.2rem;
@sideWidthNarrow: 0.64rem;
@primaryColor: #1677ff;
@borderColor: #e8ecf2;

.side {
  flex: 0 0 @sideWidth;
  width: @sideWidth;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-right: 1px solid @borderColor;

  .logo {
    flex: 0 0 auto;
    height: 0.72rem;
    padding: 0 0.16rem;
    display: flex;
    align-items: center;
    gap: 0.1rem;
    color: inherit;
    border-bottom: 1px solid @borderColor;

    .brandMark {
      flex: 0 0 auto;
      width: 0.32rem;
      height: 0.32rem;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 0.18rem;
      color: #fff;
      background-color: @primaryColor;
      border-radius: 0.08rem;
    }

    .brandText {
      display: flex;
      flex-direction: column;
      min-width: 0;

      .title {
        font-size: 0.15rem;
        font-weight: 600;
        line-height: 1.3;
        white-space: nowrap;
      }

      .subtitle {
        font-size: 0.12rem;
        line-height: 1.3;
        color: rgba(0, 0, 0, 0.45);
        white-space: nowrap;
      }
    }
  }

  .menu {
    flex: 1 1 auto;
    min-height: 0;
    padding: 0.12rem 0.08rem;
    overflow-y: auto;

    :deep(.ant-menu) {
      border-inline-end: none;
    }

    :deep(.ant-menu-item) {
      margin-inline: 0;
      width: 100%;
      border-radius: 0.06rem;
    }
  }

  .user {
    flex: 0 0 auto;
    padding: 0.12rem 0.16rem;
    display: flex;
    align-items: center;
    gap: 0.08rem;
    border-top: 1px solid @borderColor;

    .userIcon {
      flex: 0 0 auto;
      width: 0.3rem;
      height: 0.3rem;
      border-radius: 50%;
    }

    .name {
      flex: 1 1 auto;
      min-width: 0;
      font-size: 0.13rem;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .back {
      flex: 0 0 auto;
      display: inline-flex;
      align-items: center;
      gap: 0.04rem;
      font-size: 0.12rem;
      color: @primaryColor;
      white-space: nowrap;
    }
  }
}

@media screen and (max-width: 768px) {
  .side {
    flex-basis: @sideWidthNarrow;
    width: @sideWidthNarrow;

    .logo {
      height: 0.64rem;
      padding: 0;
      justify-content: center;

      .brandText {
        display: none;
      }
    }

    .menu {
      padding: 0.08rem 0.04rem;

      :deep(.ant-menu-item) {
        padding-inline: 0 !important;
        display: flex;
        justify-content: center;
      }

      :deep(.ant-menu-title-content) {
        display: none;
      }

      :deep(.ant-menu-item-icon) {
        margin-inline-end: 0;
      }
    }

    .user {
      padding: 0.1rem 0;
      flex-direction: column;
      justify-content: center;
      gap: 0.1rem;

      .name {
        display: none;
      }

      .backText {
        display: none;
      }

      .backIcon {
        font-size: 0.16rem;
      }
    }
  }
}
</style>
