<template>
  <div class="main">
    <div class="title">角色管理</div>
    <div class="toolbar">
      <a-input-search v-model:value="keyword" class="searchInput" placeholder="搜索角色编码/名称" allow-clear @search="search" />
      <a-button type="primary" @click="openAdd">新建角色</a-button>
    </div>
    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="pagination" row-key="roleId" @change="tableChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'name'">
          {{ record.name }}
          <a-tag v-if="record.buildIn === 1" class="buildInTag" color="blue">内置</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'status'">
          <a-tag :color="record.status === 1 ? 'green' : 'red'">{{ record.status === 1 ? "启用" : "禁用" }}</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <span v-if="record.buildIn === 1" class="buildInTip">内置角色不可操作</span>
          <template v-else>
            <a @click="openEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a @click="openPermission(record)">权限</a>
            <a-divider type="vertical" />
            <a @click="toggleStatus(record)">{{ record.status === 1 ? "禁用" : "启用" }}</a>
            <a-divider type="vertical" />
            <a class="dangerText" @click="deleteRole(record)">删除</a>
          </template>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="editVisible" :title="editForm.roleId ? '编辑角色' : '新建角色'" :confirm-loading="editSaving" @ok="saveRole">
      <a-form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="角色编码" required>
          <a-input v-model:value="editForm.code" :disabled="!!editForm.roleId" placeholder="小写字母、数字、下划线、冒号" />
        </a-form-item>
        <a-form-item label="角色名称" required>
          <a-input v-model:value="editForm.name" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="editForm.description" :rows="3" placeholder="请输入角色描述" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="permissionVisible"
      :title="`权限配置 - ${currentRole.name || ''}`"
      width="40%"
      :confirm-loading="permissionSaving"
      @ok="savePermission">
      <a-spin :spinning="permissionLoading">
        <a-tree v-model:checkedKeys="checkedKeys" :expanded-keys="expandedKeys" checkable :tree-data="permissionTreeData" @expand="onExpand" />
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
import HttpUtil from "@/util/HttpUtil";

export default {
  name: "AdminRole",
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
        { title: "角色编码", dataIndex: "code", width: "20%" },
        { title: "角色名称", dataIndex: "name", width: "20%" },
        { title: "描述", dataIndex: "description" },
        { title: "状态", dataIndex: "status", width: "10%" },
        { title: "操作", dataIndex: "operation", width: "26%" },
      ],
      editVisible: false,
      editSaving: false,
      editForm: { roleId: null, code: "", name: "", description: "" },
      permissionVisible: false,
      permissionLoading: false,
      permissionSaving: false,
      currentRole: {},
      //权限树数据，叶子层为权限包含的资源
      permissionTreeData: [],
      //全部权限code，用于提交前过滤掉资源叶子
      permissionCodes: [],
      checkedKeys: [],
      //展开的树节点，权限树数据异步到达，用受控展开保证叶子层资源可见
      expandedKeys: [],
    };
  },
  created() {
    this.loadList();
  },
  methods: {
    async loadList() {
      this.loading = true;
      try {
        const data = await HttpUtil.get("/role/manage/page", {
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
    openAdd() {
      this.editForm = { roleId: null, code: "", name: "", description: "" };
      this.editVisible = true;
    },
    openEdit(record) {
      this.editForm = { roleId: record.roleId, code: record.code, name: record.name, description: record.description || "" };
      this.editVisible = true;
    },
    async saveRole() {
      const code = this.editForm.code.trim();
      const name = this.editForm.name.trim();
      if (!this.editForm.roleId && !code) {
        this.$message.error("角色编码不能为空");
        return;
      }
      if (!name) {
        this.$message.error("角色名称不能为空");
        return;
      }
      this.editSaving = true;
      try {
        if (this.editForm.roleId) {
          //编辑角色，code不可修改
          await HttpUtil.post("/role/manage", null, { roleId: this.editForm.roleId, name, description: this.editForm.description });
        } else {
          await HttpUtil.put("/role/manage", null, { code, name, description: this.editForm.description });
        }
        this.$message.success("保存成功");
        this.editVisible = false;
        await this.loadList();
      } catch (err) {
        console.error(err);
      } finally {
        this.editSaving = false;
      }
    },
    async openPermission(record) {
      this.currentRole = record;
      this.permissionVisible = true;
      this.permissionLoading = true;
      this.permissionTreeData = [];
      this.permissionCodes = [];
      this.checkedKeys = [];
      try {
        const [tree, detail] = await Promise.all([
          HttpUtil.get("/role/manage/permissionTree"),
          HttpUtil.get("/role/manage/detail", { roleId: record.roleId }),
        ]);
        this.permissionTreeData = this.buildTree(tree);
        this.permissionCodes = tree.map((item) => item.code);
        this.checkedKeys = detail.permissionCodes || [];
        this.expandedKeys = this.collectKeys(this.permissionTreeData);
      } catch (err) {
        console.error(err);
      } finally {
        this.permissionLoading = false;
      }
    },
    onExpand(keys) {
      this.expandedKeys = keys;
    },
    /**
     * 权限树接口返回扁平列表，按parent组装成树，资源作为权限节点的叶子层展示
     */
    buildTree(list) {
      const nodeMap = new Map();
      list.forEach((item) => {
        nodeMap.set(item.code, {
          key: item.code,
          title: item.name,
          children: (item.resources || []).map((resource) => ({ key: resource.code, title: resource.name, disableCheckbox: true })),
        });
      });
      const roots = [];
      list.forEach((item) => {
        const node = nodeMap.get(item.code);
        if (item.parent && nodeMap.has(item.parent)) {
          nodeMap.get(item.parent).children.unshift(node);
        } else {
          roots.push(node);
        }
      });
      return roots;
    },
    /**
     * 收集树中全部节点key，用于默认展开
     */
    collectKeys(nodes) {
      const keys = [];
      nodes.forEach((node) => {
        keys.push(node.key);
        if (node.children && node.children.length) {
          keys.push(...this.collectKeys(node.children));
        }
      });
      return keys;
    },
    async savePermission() {
      //父子联动勾选会把资源叶子一起带上，后端绑的是权限code，提交前只保留权限层
      const codes = this.checkedKeys.filter((key) => this.permissionCodes.includes(key));
      this.permissionSaving = true;
      try {
        await HttpUtil.post("/role/manage/permissions", null, { roleId: this.currentRole.roleId, permissionCodes: codes });
        this.$message.success("保存成功");
        this.permissionVisible = false;
      } catch (err) {
        console.error(err);
      } finally {
        this.permissionSaving = false;
      }
    },
    toggleStatus(record) {
      const status = record.status === 1 ? 0 : 1;
      this.$confirm({
        title: status === 0 ? "确认禁用该角色？" : "确认启用该角色？",
        content: status === 0 ? "禁用后已绑用户将立即失去该角色权限" : "",
        onOk: async () => {
          await HttpUtil.post("/role/manage/status", null, { roleId: record.roleId, status });
          this.$message.success("操作成功");
          await this.loadList();
        },
      });
    },
    deleteRole(record) {
      this.$confirm({
        title: "确认删除该角色？",
        content: "删除后不可恢复",
        onOk: async () => {
          await HttpUtil.delete(`/role/manage/${record.roleId}`);
          this.$message.success("删除成功");
          await this.loadList();
        },
      });
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
  .buildInTag {
    margin-left: 0.08rem;
  }
  .buildInTip {
    color: rgba(0, 0, 0, 0.35);
  }
  .dangerText {
    color: #ff4d4f;
  }
}
</style>
