<template>
  <div>
    <a-button type="primary" @click="addOne">新增</a-button>
    <a-table :columns="columns" :data-source="list" :pagination="false">
      <template v-for="col in ['name', 'url']" :slot="col" slot-scope="text, record, index">
        <div :key="col">
          <a-input v-if="record.isEdit" style="margin: -5px 0" :value="text"
                   @change="e => handleChange(e.target.value, record.id, col)" />
          <template v-else>{{ text }}</template>
        </div>
      </template>

      <template slot="icon" slot-scope="text, record, index">
        <div key="icon">
          <a-select v-if="record.isEdit" :default-value="text" style="width: 120px"
                    @change="e => handleChange(e, record.id, 'icon')">
            <a-select-option v-for="item in iconList" :key="item.icon" :value="item.icon">
              <my-icon :type="item.icon" />
              {{ item.label }}
            </a-select-option>
          </a-select>
          <template v-else>
            <my-icon style="font-size: 1.2em" :type="text" />
          </template>
        </div>
      </template>

      <template slot="checked" slot-scope="text">
        <span>{{ text === 1 ? "是" : "否" }}</span>
      </template>

      <template slot="operation" slot-scope="text, record, index">
        <div class="editable-row-operations">
        <span v-if="record.isEdit">
          <a @click="() => save(record.id)">保存</a>
            &nbsp;<a @click="() => cancel(record.id)">取消</a>
        </span>
          <div v-else>
            <a :disabled="currentEditCache" @click="() => edit(record.id)">编辑</a>&nbsp;
            <a-popconfirm title="确认删除吗?" ok-text="是" cancel-text="否" @confirm="() => deleteOne(record.id)">
              <a :disabled="currentEditCache">删除</a>
            </a-popconfirm>&nbsp;
            <a :disabled="currentEditCache || record.checked===1" @click="() => setDefault(record.id)">设为默认</a>
          </div>
        </div>
      </template>

    </a-table>
  </div>
</template>
<script>
import HttpUtil from "@/util/HttpUtil";

export default {
  name: "manageSearchEngine",
  data() {
    return {
      list: [],
      currentEditCache: null,
      iconList: [
        { icon: "icon-baidu", label: "百度" },
        { icon: "icon-bing", label: "必应" },
        { icon: "icon-google", label: "谷歌" },
        { icon: "icon-yandex", label: "yandex" },
        { icon: "icon-sogou", label: "搜狗" },
        { icon: "icon-yahoo", label: "雅虎" },
        { icon: "icon-qita", label: "其他" }
      ],
      columns: [
        {
          title: "名称",
          dataIndex: "name",
          width: "10em",
          scopedSlots: { customRender: "name" }
        }, {
          title: "图标",
          dataIndex: "icon",
          width: "8em",
          scopedSlots: { customRender: "icon" }
        }, {
          title: "路径(%s 会被替换为搜索内容)",
          dataIndex: "url",
          scopedSlots: { customRender: "url" }
        }, {
          title: "默认",
          dataIndex: "checked",
          width: "10em",
          scopedSlots: { customRender: "checked" }
        }, {
          title: "操作",
          width: "15em",
          dataIndex: "operation",
          scopedSlots: { customRender: "operation" }
        }]
    };
  },
  async created() {
    await this.getData();
  },
  computed: {
    //是否可点击删除
    deleteOk() {
      return this.list.filter(item => item.isEdit).length === 0;
    }
  },
  methods: {
    addOne() {
      let body = { id: -1, icon: "", name: "", url: "", checked: 0, isEdit: true };
      this.list = [body, ...this.list];
      this.currentEditCache = body;

    },
    handleChange(value, id, column) {
      console.log(value, id, column);
      const target = this.list.find(item => item.id === id);
      target[column] = value;
      this.list = [...this.list];
    },
    async edit(id) {
      let target = this.list.find(item => item.id === id);
      this.currentEditCache = { ...target };
      target.isEdit = true;
      this.list = [...this.list];
    },
    async save(id) {
      let target = this.list.find(item => item.id === id);
      if (target.id > 0) {
        await HttpUtil.post("/searchEngine/edit", null, target);
      } else {
        await HttpUtil.post("/searchEngine/insert", null, target);
      }
      target.isEdit = false;
      this.currentEditCache = null;
      await this.getData();
    },
    cancel(id) {
      let target = this.list.find(item => item.id === id);
      Object.assign(target, this.currentEditCache);
      target.isEdit = false;
      this.list = [...this.list];
      this.currentEditCache = null;
    },
    async deleteOne(id) {
      await HttpUtil.post("/searchEngine/delete", null, { id });
      this.list = await HttpUtil.get("/searchEngine/list");
    },
    async setDefault(id) {
      await HttpUtil.post("/searchEngine/setChecked", null, { id });
      this.list = await HttpUtil.get("/searchEngine/list");
    },
    async getData() {
      this.list = await HttpUtil.get("/searchEngine/list");
    }
  }
};
</script>

<style lang="less" scoped>
.icon {
  color: black;
  cursor: pointer;
  font-size: 1.3em;
}
</style>