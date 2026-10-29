<template>
  <div class="main">
    <header class="pageHeader">
      <h1 class="title">角色管理</h1>
      <p class="desc">维护角色及其权限，内置角色不可修改。</p>
    </header>

    <div class="toolbar">
      <a-input-search v-model:value="keyword" class="searchInput" placeholder="搜索角色编码/名称" allow-clear @search="search" />
      <a-button type="primary" @click="openAdd">新建角色</a-button>
    </div>

    <div class="tableWrap">
      <a-table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ x: 900 }"
        row-key="roleId"
        @change="tableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'name'">
            {{ record.name }}
            <a-tag v-if="record.buildIn === 1" class="statusTag builtIn">内置</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag class="statusTag" :class="record.status === 1 ? 'enabled' : 'disabled'">{{ record.status === 1 ? "启用" : "禁用" }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'operation'">
            <span v-if="record.buildIn === 1" class="buildInTip">
              <LockOutlined class="lockIcon" />
              内置角色不可修改
            </span>
            <template v-else>
              <a class="actionLink" @click="openEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a class="actionLink" @click="openPermission(record)">权限</a>
              <a-divider type="vertical" />
              <a-dropdown :trigger="['click']">
                <a class="actionLink moreLink">
                  更多
                  <DownOutlined class="moreIcon" />
                </a>
                <template #overlay>
                  <a-menu @click="handleMore($event.key, record)">
                    <a-menu-item key="toggle">{{ record.status === 1 ? "禁用" : "启用" }}</a-menu-item>
                    <a-menu-item key="delete" danger>删除</a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </template>
          </template>
        </template>
        <template #emptyText>
          <a-empty class="emptyState" :description="emptyText" />
        </template>
      </a-table>
    </div>

    <a-modal
      v-model:open="editVisible"
      :title="editTitle"
      :width="520"
      :confirm-loading="editSaving"
      ok-text="保存"
      cancel-text="取消"
      destroy-on-close
      @ok="saveRole">
      <a-form ref="editFormRef" class="dialogForm" :model="editForm" :rules="editRules" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="角色编码" name="code" :required="!editForm.roleId">
          <a-input v-model:value="editForm.code" :disabled="!!editForm.roleId" placeholder="小写字母、数字、下划线、冒号" />
        </a-form-item>
        <a-form-item label="角色名称" name="name">
          <a-input v-model:value="editForm.name" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="editForm.description" :rows="3" placeholder="请输入角色描述" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="permissionVisible"
      :title="`权限配置 - ${currentRole.name || ''}`"
      :width="560"
      :confirm-loading="permissionSaving"
      ok-text="保存"
      cancel-text="取消"
      @ok="savePermission">
      <a-spin :spinning="permissionLoading">
        <div class="permissionTree">
          <a-tree v-model:checkedKeys="checkedKeys" :expanded-keys="expandedKeys" checkable :tree-data="permissionTreeData" @expand="onExpand" />
        </div>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
import { DownOutlined, LockOutlined } from "@ant-design/icons-vue";
import HttpUtil from "@/util/HttpUtil";

export default {
  name: "AdminRole",
  components: { DownOutlined, LockOutlined },
  data() {
    const validateCode = (rule, value, callback) => {
      if (!this.editForm.roleId && !(value && value.trim())) {
        callback(new Error("请输入角色编码"));
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
        { title: "角色编码", dataIndex: "code", width: 180 },
        { title: "角色名称", dataIndex: "name", width: 180 },
        { title: "描述", dataIndex: "description" },
        { title: "状态", dataIndex: "status", width: 90 },
        { title: "操作", dataIndex: "operation", width: 180 },
      ],
      editVisible: false,
      editSaving: false,
      editForm: { roleId: null, code: "", name: "", description: "" },
      editRules: {
        code: [{ validator: validateCode, trigger: "blur" }],
        name: [{ required: true, whitespace: true, message: "请输入角色名称", trigger: "blur" }],
      },
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
  computed: {
    emptyText() {
      return this.searchedKeyword ? "未找到匹配结果，请调整关键词" : "暂无角色数据";
    },
    editTitle() {
      return this.editForm.roleId ? `编辑角色 - ${this.editForm.code}` : "新建角色";
    },
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
      this.searchedKeyword = this.keyword.trim();
      this.pagination.current = 1;
      this.loadList();
    },
    tableChange(pager) {
      this.pagination.current = pager.current;
      this.pagination.pageSize = pager.pageSize;
      this.loadList();
    },
    handleMore(key, record) {
      if (key === "toggle") {
        this.toggleStatus(record);
      } else if (key === "delete") {
        this.deleteRole(record);
      }
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
      try {
        await this.$refs.editFormRef.validate();
      } catch {
        return;
      }
      const code = this.editForm.code.trim();
      const name = this.editForm.name.trim();
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

    &.builtIn {
      margin-left: 0.06rem;
      color: @primaryColor;
      background-color: #eff6ff;
      border-color: #d6e6ff;
    }
  }

  .buildInTip {
    display: inline-flex;
    align-items: center;
    gap: 0.04rem;
    font-size: 0.12rem;
    color: @mutedColor;
  }

  .lockIcon {
    font-size: 0.12rem;
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

// 弹窗内容会 teleport 到 body，相关样式不能嵌在 .main 内
.permissionTree {
  max-height: 58vh;
  overflow: auto;
}

.dialogForm {
  :deep(.ant-form-item) {
    margin-bottom: 0.16rem;
  }
}
</style>
