<template>
  <div class="main">
    <header class="pageHeader">
      <h1 class="title">用户管理</h1>
      <p class="desc">管理平台用户，查看详情、分配角色或重置密码。</p>
    </header>

    <div class="toolbar">
      <a-input-search v-model:value="keyword" class="searchInput" placeholder="搜索用户名/邮箱" allow-clear @search="search" />
    </div>

    <div class="tableWrap">
      <a-table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ x: 1042 }"
        row-key="userId"
        @change="tableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'icon'">
            <img class="userIcon" :src="record.icon" :alt="`${record.username} 的头像`" />
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag class="statusTag" :class="record.status === 1 ? 'enabled' : 'disabled'">{{ record.status === 1 ? "正常" : "禁用" }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">{{ formatTime(record.createTime) }}</template>
          <template v-else-if="column.dataIndex === 'lastLoginTime'">{{ formatTime(record.lastLoginTime) }}</template>
          <template v-else-if="column.dataIndex === 'operation'">
            <a class="actionLink" @click="openDetail(record)">详情</a>
            <a-divider type="vertical" />
            <a class="actionLink" @click="openRole(record)">分配角色</a>
            <a-divider type="vertical" />
            <a-dropdown :trigger="['click']">
              <a class="actionLink moreLink">
                更多
                <DownOutlined class="moreIcon" />
              </a>
              <template #overlay>
                <a-menu @click="handleMore($event.key, record)">
                  <a-menu-item key="toggle">{{ record.status === 1 ? "禁用" : "启用" }}</a-menu-item>
                  <a-menu-item key="resetPassword" danger>重置密码</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
        </template>
        <template #emptyText>
          <a-empty class="emptyState" :description="emptyText" />
        </template>
      </a-table>
    </div>

    <a-drawer v-model:open="detailVisible" :title="detailTitle" width="min(420px, 92vw)">
      <a-spin :spinning="detailLoading">
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="头像">
            <img class="userIcon" :src="detail.icon" :alt="`${detail.username || ''} 的头像`" />
          </a-descriptions-item>
          <a-descriptions-item label="用户名">{{ detail.username }}</a-descriptions-item>
          <a-descriptions-item label="邮箱">{{ detail.email || "-" }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag class="statusTag" :class="detail.status === 1 ? 'enabled' : 'disabled'">{{ detail.status === 1 ? "正常" : "禁用" }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="书签数">{{ detail.bookmarkCount || 0 }}</a-descriptions-item>
          <a-descriptions-item label="注册时间">{{ formatTime(detail.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="最近登录">{{ formatTime(detail.lastLoginTime) }}</a-descriptions-item>
          <a-descriptions-item label="角色">
            <template v-if="detail.roles && detail.roles.length">
              <a-tag v-for="role in detail.roles" :key="role.roleId" class="roleTag">{{ role.name }}</a-tag>
            </template>
            <span v-else>-</span>
          </a-descriptions-item>
        </a-descriptions>
      </a-spin>
    </a-drawer>

    <a-modal
      v-model:open="roleVisible"
      :title="`分配角色 - ${currentUser.username || ''}`"
      :width="480"
      :confirm-loading="roleSaving"
      ok-text="保存"
      cancel-text="取消"
      @ok="saveRoles">
      <a-spin :spinning="roleLoading">
        <a-select v-model:value="selectedRoleIds" mode="multiple" class="fullWidth" placeholder="请选择角色，留空表示清空" :options="roleOptions" />
      </a-spin>
    </a-modal>

    <a-modal
      v-model:open="passwordVisible"
      :title="`重置密码 - ${currentUser.username || ''}`"
      :width="480"
      :confirm-loading="passwordSaving"
      ok-text="重置密码"
      cancel-text="取消"
      destroy-on-close
      @ok="savePassword">
      <a-alert class="passwordTip" type="warning" show-icon message="重置后该用户的已登录状态将失效" />
      <a-form
        ref="passwordFormRef"
        class="dialogForm"
        :model="passwordForm"
        :rules="passwordRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 18 }">
        <a-form-item label="新密码" name="password">
          <a-input-password v-model:value="passwordForm.password" placeholder="请输入新密码" />
        </a-form-item>
        <a-form-item label="确认密码" name="confirm">
          <a-input-password v-model:value="passwordForm.confirm" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import dayjs from "dayjs";
import { DownOutlined } from "@ant-design/icons-vue";
import HttpUtil from "@/util/HttpUtil";

export default {
  name: "AdminUser",
  components: { DownOutlined },
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请再次输入新密码"));
      } else if (value !== this.passwordForm.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    return {
      keyword: "",
      searchedKeyword: "",
      list: [],
      loading: false,
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showTotal: (total) => `共 ${total} 条`,
      },
      columns: [
        { title: "头像", dataIndex: "icon", width: 72 },
        { title: "用户名", dataIndex: "username", width: 140 },
        { title: "邮箱", dataIndex: "email", width: 200, ellipsis: true },
        { title: "状态", dataIndex: "status", width: 90 },
        { title: "注册时间", dataIndex: "createTime", width: 160 },
        { title: "最近登录", dataIndex: "lastLoginTime", width: 160 },
        { title: "操作", dataIndex: "operation", width: 220 },
      ],
      detailVisible: false,
      detailLoading: false,
      detail: {},
      currentUser: {},
      roleVisible: false,
      roleLoading: false,
      roleSaving: false,
      roleOptions: [],
      selectedRoleIds: [],
      passwordVisible: false,
      passwordSaving: false,
      passwordForm: { password: "", confirm: "" },
      passwordRules: {
        password: [{ required: true, whitespace: true, message: "请输入新密码", trigger: "blur" }],
        confirm: [{ validator: validateConfirm, trigger: "blur" }],
      },
    };
  },
  computed: {
    emptyText() {
      return this.searchedKeyword ? "未找到匹配结果，请调整关键词" : "暂无用户数据";
    },
    detailTitle() {
      return this.currentUser.username ? `用户详情 - ${this.currentUser.username}` : "用户详情";
    },
  },
  created() {
    this.loadList();
  },
  methods: {
    async loadList() {
      this.loading = true;
      try {
        const data = await HttpUtil.get("/user/manage/page", {
          pageNum: this.pagination.current,
          pageSize: this.pagination.pageSize,
          keyword: this.keyword.trim(),
        });
        this.list = data.list || [];
        this.pagination.total = data.total || 0;
      } catch (err) {
        console.error(err);
      } finally {
        this.loading = false;
      }
    },
    search() {
      this.searchedKeyword = this.keyword.trim();
      this.pagination.current = 1;
      this.loadList();
    },
    tableChange(pager) {
      this.pagination.current = pager.current;
      this.pagination.pageSize = pager.pageSize;
      this.loadList();
    },
    formatTime(time) {
      return time ? dayjs(time).format("YYYY-MM-DD HH:mm:ss") : "-";
    },
    handleMore(key, record) {
      if (key === "toggle") {
        this.toggleStatus(record);
      } else if (key === "resetPassword") {
        this.openResetPassword(record);
      }
    },
    async openDetail(record) {
      this.currentUser = record;
      this.detail = {};
      this.detailVisible = true;
      this.detailLoading = true;
      try {
        this.detail = await HttpUtil.get("/user/manage/detail", { userId: record.userId });
      } catch (err) {
        console.error(err);
      } finally {
        this.detailLoading = false;
      }
    },
    toggleStatus(record) {
      const status = record.status === 1 ? 0 : 1;
      this.$confirm({
        title: status === 0 ? "确认禁用该用户？" : "确认启用该用户？",
        content: status === 0 ? "禁用后该用户将立即无法使用，已登录状态最长 60 秒内失效" : "",
        onOk: async () => {
          await HttpUtil.post("/user/manage/status", null, { userId: record.userId, status });
          this.$message.success("操作成功");
          await this.loadList();
        },
      });
    },
    async openRole(record) {
      this.currentUser = record;
      this.roleVisible = true;
      this.roleLoading = true;
      this.roleOptions = [];
      this.selectedRoleIds = [];
      try {
        const [options, detail] = await Promise.all([
          HttpUtil.get("/role/manage/options"),
          HttpUtil.get("/user/manage/detail", { userId: record.userId }),
        ]);
        this.roleOptions = options.map((item) => ({ label: item.name, value: item.roleId }));
        //全量替换语义，回显当前已绑角色
        this.selectedRoleIds = (detail.roles || []).map((role) => role.roleId);
      } catch (err) {
        console.error(err);
      } finally {
        this.roleLoading = false;
      }
    },
    async saveRoles() {
      this.roleSaving = true;
      try {
        await HttpUtil.post("/user/manage/roles", null, { userId: this.currentUser.userId, roleIds: this.selectedRoleIds });
        this.$message.success("保存成功");
        this.roleVisible = false;
        await this.loadList();
      } catch (err) {
        console.error(err);
      } finally {
        this.roleSaving = false;
      }
    },
    openResetPassword(record) {
      this.currentUser = record;
      this.passwordForm = { password: "", confirm: "" };
      this.passwordVisible = true;
    },
    async savePassword() {
      try {
        await this.$refs.passwordFormRef.validate();
      } catch {
        return;
      }
      this.passwordSaving = true;
      try {
        await HttpUtil.post("/user/manage/resetPassword", null, { userId: this.currentUser.userId, password: this.passwordForm.password });
        this.$message.success("重置成功");
        this.passwordVisible = false;
        await this.loadList();
      } catch (err) {
        console.error(err);
      } finally {
        this.passwordSaving = false;
      }
    },
  },
};
</script>

<style lang="less" scoped>
@titleColor: #1f2937;
@mutedColor: rgba(0, 0, 0, 0.45);
@primaryColor: #1677ff;
@borderColor: #eef1f6;
@headerBgColor: #fafbfc;
@hoverBgColor: #f8fafc;

.main {
  width: 100%;

  .pageHeader {
    margin-bottom: 0.16rem;

    .title {
      margin: 0;
      font-size: 0.2rem;
      font-weight: 600;
      line-height: 1.35;
      color: @titleColor;
    }

    .desc {
      margin: 0.04rem 0 0;
      font-size: 0.13rem;
      color: @mutedColor;
    }
  }

  .toolbar {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    gap: 0.08rem;
    margin-bottom: 0.16rem;

    .searchInput {
      width: 3rem;
      max-width: 100%;
    }
  }

  .tableWrap {
    min-height: 4.8rem;
  }

  :deep(.ant-table) {
    font-size: 0.13rem;
  }

  :deep(.ant-table-thead > tr > th) {
    padding: 0.1rem 0.12rem;
    font-weight: 600;
    color: #4b5563;
    background-color: @headerBgColor;
    border-bottom: 1px solid @borderColor;
  }

  :deep(.ant-table-tbody > tr > td) {
    padding: 0.1rem 0.12rem;
    border-bottom: 1px solid @borderColor;
  }

  :deep(.ant-table-tbody > tr:last-child > td) {
    border-bottom: none;
  }

  :deep(.ant-table-tbody > tr.ant-table-row:hover > td) {
    background-color: @hoverBgColor;
  }

  .actionLink {
    color: @primaryColor;
  }

  .moreLink {
    display: inline-flex;
    align-items: center;
    gap: 0.02rem;
    color: @primaryColor;
  }

  .moreIcon {
    font-size: 0.1rem;
  }

  .emptyState {
    margin: 0.32rem 0;
  }
}

// 弹窗与抽屉内容会 teleport 到 body，相关样式不能嵌在 .main 内
.userIcon {
  display: block;
  width: 0.3rem;
  height: 0.3rem;
  border-radius: 50%;
}

.statusTag {
  margin-inline-end: 0;
  padding: 0 0.08rem;
  font-size: 0.12rem;
  line-height: 0.2rem;
  border-radius: 0.04rem;

  &.enabled {
    color: #3f7d3a;
    background-color: #f0f9eb;
    border-color: #d7ecd0;
  }

  &.disabled {
    color: #c0392b;
    background-color: #fdf2f2;
    border-color: #f8d7d5;
  }
}

.roleTag {
  margin-bottom: 0.04rem;
  color: @primaryColor;
  background-color: #eff6ff;
  border-color: #d6e6ff;
}

.fullWidth {
  width: 100%;
}

.passwordTip {
  margin-bottom: 0.16rem;
}

.dialogForm {
  :deep(.ant-form-item) {
    margin-bottom: 0.16rem;
  }
}
</style>
