<template>
  <div class="main">
    <div class="title">用户管理</div>
    <div class="toolbar">
      <a-input-search v-model:value="keyword" class="searchInput" placeholder="搜索用户名/邮箱" allow-clear @search="search" />
    </div>
    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="pagination" row-key="userId" @change="tableChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'icon'">
          <img class="userIcon" :src="record.icon" />
        </template>
        <template v-else-if="column.dataIndex === 'status'">
          <a-tag :color="record.status === 1 ? 'green' : 'red'">{{ record.status === 1 ? "正常" : "禁用" }}</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">{{ formatTime(record.createTime) }}</template>
        <template v-else-if="column.dataIndex === 'lastLoginTime'">{{ formatTime(record.lastLoginTime) }}</template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a @click="openDetail(record)">详情</a>
          <a-divider type="vertical" />
          <a @click="toggleStatus(record)">{{ record.status === 1 ? "禁用" : "启用" }}</a>
          <a-divider type="vertical" />
          <a @click="openRole(record)">分配角色</a>
          <a-divider type="vertical" />
          <a @click="openResetPassword(record)">重置密码</a>
        </template>
      </template>
    </a-table>

    <a-drawer v-model:open="detailVisible" title="用户详情" width="4.5rem">
      <a-spin :spinning="detailLoading">
        <a-descriptions :column="1" bordered>
          <a-descriptions-item label="头像">
            <img class="userIcon" :src="detail.icon" />
          </a-descriptions-item>
          <a-descriptions-item label="用户名">{{ detail.username }}</a-descriptions-item>
          <a-descriptions-item label="邮箱">{{ detail.email || "-" }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="detail.status === 1 ? 'green' : 'red'">{{ detail.status === 1 ? "正常" : "禁用" }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="书签数">{{ detail.bookmarkCount || 0 }}</a-descriptions-item>
          <a-descriptions-item label="注册时间">{{ formatTime(detail.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="最近登录">{{ formatTime(detail.lastLoginTime) }}</a-descriptions-item>
          <a-descriptions-item label="角色">
            <template v-if="detail.roles && detail.roles.length">
              <a-tag v-for="role in detail.roles" :key="role.roleId" color="blue">{{ role.name }}</a-tag>
            </template>
            <span v-else>-</span>
          </a-descriptions-item>
        </a-descriptions>
      </a-spin>
    </a-drawer>

    <a-modal v-model:open="roleVisible" :title="`分配角色 - ${currentUser.username || ''}`" :confirm-loading="roleSaving" @ok="saveRoles">
      <a-spin :spinning="roleLoading">
        <a-select v-model:value="selectedRoleIds" mode="multiple" class="fullWidth" placeholder="请选择角色，留空表示清空" :options="roleOptions" />
      </a-spin>
    </a-modal>

    <a-modal v-model:open="passwordVisible" :title="`重置密码 - ${currentUser.username || ''}`" :confirm-loading="passwordSaving" @ok="savePassword">
      <a-alert class="passwordTip" type="warning" show-icon message="重置后该用户的已登录状态将失效" />
      <a-form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="新密码" required>
          <a-input-password v-model:value="passwordForm.password" placeholder="请输入新密码" />
        </a-form-item>
        <a-form-item label="确认密码" required>
          <a-input-password v-model:value="passwordForm.confirm" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import dayjs from "dayjs";
import HttpUtil from "@/util/HttpUtil";

export default {
  name: "AdminUser",
  data() {
    return {
      keyword: "",
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
        { title: "头像", dataIndex: "icon", width: "8%" },
        { title: "用户名", dataIndex: "username", width: "16%" },
        { title: "邮箱", dataIndex: "email", width: "20%" },
        { title: "状态", dataIndex: "status", width: "8%" },
        { title: "注册时间", dataIndex: "createTime", width: "16%" },
        { title: "最近登录", dataIndex: "lastLoginTime", width: "16%" },
        { title: "操作", dataIndex: "operation", width: "26%" },
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
    };
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
      if (!this.passwordForm.password) {
        this.$message.error("新密码不能为空");
        return;
      }
      if (this.passwordForm.password !== this.passwordForm.confirm) {
        this.$message.error("两次输入的密码不一致");
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
.main {
  .title {
    font-size: 0.2rem;
    margin-bottom: 0.2rem;
  }
  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 0.16rem;
  }
  .searchInput {
    width: 3rem;
  }
  .userIcon {
    width: 0.3rem;
    height: 0.3rem;
    border-radius: 50%;
  }
  .fullWidth {
    width: 100%;
  }
  .passwordTip {
    margin-bottom: 0.16rem;
  }
}
</style>
