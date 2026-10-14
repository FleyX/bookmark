<template>
  <div>
    <a-form ref="addForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item v-if="isAdd && !addType" name="type" label="类别">
        <a-radio-group v-model:value="form.type" :options="options" />
      </a-form-item>
      <template v-if="form.type !== 'file'">
        <a-form-item name="name" label="名称" :required="false">
          <a-input ref="inputName" v-model:value="form.name" placeholder="名称" @pressEnter="submit" />
        </a-form-item>
        <a-form-item v-if="form.type === 'bookmark'" name="url" label="URL">
          <a-input v-model:value="form.url" placeholder="url" @pressEnter="submit" />
        </a-form-item>
        <div class="btns">
          <a-button type="primary" :loading="loading" :disabled="loading" @click="submit">提交</a-button>
        </div>
      </template>
      <div v-else>
        <a-upload-dragger
          name="file"
          :data="{ path: form.path }"
          :headers="{ 'jwt-token': token }"
          action="/bookmark/api/bookmark/uploadBookmarkFile"
          accept=".html,.db3"
          @change="fileChange">
          <p class="ant-upload-drag-icon">
            <InboxOutlined />
          </p>
          <p class="ant-upload-text">单击选中或者拖拽文件到此处</p>
        </a-upload-dragger>
      </div>
    </a-form>
  </div>
</template>

<script>
import { InboxOutlined } from "@ant-design/icons-vue";
import HttpUtil from "@/util/HttpUtil";
import { useGlobalConfigStore } from "@/store/globalConfig";
import { useTreeDataStore } from "@/store/treeData";
const options = [
  { label: "书签", value: "bookmark" },
  { label: "文件夹", value: "folder" },
  { label: "导入", value: "file" },
];
export default {
  name: "addBookmark",
  components: { InboxOutlined },
  props: {
    isAdd: Boolean, //是否新增
    addType: String, //新增的类别
    targetNode: Object,
  },
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 20 },
      options,
      token: "",
      loading: false,
      form: {
        type: "bookmark",
        name: "",
        url: "",
        path: "",
        file: null,
      },
      rules: {
        name: [{ required: true, min: 1, max: 1000, message: "名称长度为1-200", trigger: "change" }],
        url: [{ required: true, min: 1, message: "不能为空", trigger: "change" }],
      },
    };
  },
  created() {
    console.log(this.isAdd, this.targetNode);
    if (this.isAdd) {
      this.form.type = this.addType ? this.addType : "bookmark";
    } else {
      this.form.type = this.targetNode.type == 0 ? "bookmark" : "folder";
      this.form.name = this.targetNode.name;
      this.form.url = this.form.type === "bookmark" ? this.targetNode.url : "";
    }
    this.token = useGlobalConfigStore().token;
    this.form.path = !this.targetNode ? "" : this.targetNode.path + (this.isAdd ? "." + this.targetNode.bookmarkId : "");

    this.$nextTick(() => {
      if (this.$refs.inputName) {
        this.$refs.inputName.focus();
      }
    });
  },
  methods: {
    /**
     * 文件提交不走这儿
     */
    async submit() {
      //名称校验
      this.loading = true;
      try {
        await this.$refs["addForm"].validate();
      } catch {
        this.loading = false;
        return;
      }
      let res = null;
      let body = JSON.parse(JSON.stringify(this.form));
      body.type = body.type === "bookmark" ? 0 : 1;
      if (this.isAdd) {
        res = await HttpUtil.put("/bookmark", null, body);
        await useTreeDataStore().addNode({ sourceNode: this.targetNode, targetNode: res });
      } else {
        body.bookmarkId = this.targetNode.bookmarkId;
        let newIcon = await HttpUtil.post("/bookmark/updateOne", null, body);
        await useTreeDataStore().editNode({ node: this.targetNode, newName: this.form.name, newUrl: this.form.url, newIcon });
      }
      this.$message.success("操作成功");
      this.$emit("close", this.form.type, res);
      this.loading = false;
    },
    fileChange(info) {
      console.log(info);
      if (info.file.status === "done") {
        if (info.file.response.code === 0) {
          this.$notification.error({
            message: "异常",
            description: "文件内容无法解析，确保该文件为书签文件",
          });
        } else {
          this.$message.success("解析成功");
          this.$emit("close", this.form.type);
        }
      }
    },
  },
};
</script>

<style lang="less" scoped>
.btns {
  display: flex;
  justify-content: center;
  margin-top: 0.2rem;
}
</style>
