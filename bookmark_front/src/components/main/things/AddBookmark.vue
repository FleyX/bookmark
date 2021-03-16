<template>
  <div>
    <a-form-model ref="addForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-model-item v-if="isAdd" prop="type" label="类别">
        <a-radio-group v-model="form.type" :options="options" />
      </a-form-model-item>
      <template v-if="form.type !== 'file'">
        <a-form-model-item prop="name" label="名称" :required="false">
          <a-input v-model="form.name" placeholder="名称" />
        </a-form-model-item>
        <a-form-model-item v-if="form.type === '0'" prop="url" label="URL">
          <a-input v-model="form.url" placeholder="url" />
        </a-form-model-item>
        <div class="btns">
          <a-button type="primary" @click="submit" :loading="loading" :disabled="loading">提交</a-button>
        </div>
      </template>
      <div v-else prop="file">
        <a-upload-dragger name="file" :data="{ path: form.path }" :headers="{ 'jwt-token': token }" action="/bookmark/api/bookmark/uploadBookmarkFile" @change="fileChange">
          <p class="ant-upload-drag-icon">
            <a-icon type="inbox" />
          </p>
          <p class="ant-upload-text">单击选中或者拖拽文件到此处</p>
        </a-upload-dragger>
      </div>
    </a-form-model>
  </div>
</template>

<script>
import HttpUtil from "@/util/HttpUtil";
const options = [
  { label: "书签", value: "0" },
  { label: "文件夹", value: "1" },
  { label: "导入", value: "file" },
];
export default {
  name: "addBookmark",
  props: {
    isAdd: Boolean,
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
        type: "0", //0:书签,1:文件夹,file:文件
        name: "",
        url: "",
        path: "",
        file: null,
      },
      rules: {
        name: [{ required: true, min: 1, max: 1000, message: "名称长度为1-1000", trigger: "change" }],
        url: [{ required: true, min: 1, message: "不能为空", trigger: "change" }],
      },
    };
  },
  created() {
    console.log(this.isAdd, this.targetNode);
    if (!this.isAdd) {
      this.form.type = this.targetNode.type.toString();
      this.form.name = this.targetNode.name;
      this.form.url = this.form.type === "0" ? this.targetNode.url : "";
    }
    this.token = this.$store.state.globalConfig.token;
    this.form.path = this.targetNode == null ? "" : this.targetNode.path + (this.isAdd ? "." + this.targetNode.bookmarkId : "");
  },
  methods: {
    /**
     * 文件提交不走这儿
     */
    submit() {
      //名称校验
      this.loading = true;
      this.$refs["addForm"].validateField(this.form.type === 0 ? ["name", "url"] : "name", async (message) => {
        if (message.length > 0) {
          this.loading = false;
          return;
        }
        let res = null;
        if (this.isAdd) {
          res = await HttpUtil.put("/bookmark", null, this.form);
          await this.$store.dispatch("treeData/addNode", { sourceNode: this.targetNode, targetNode: res });
        } else {
          this.form.bookmarkId = this.targetNode.bookmarkId;
          let newIcon = await HttpUtil.post("/bookmark/updateOne", null, this.form);
          await this.$store.dispatch("treeData/editNode", { node: this.targetNode, newName: this.form.name, newUrl: this.form.url, newIcon });
        }
        this.$message.success("操作成功");
        this.$emit("close", false);
        this.loading = false;
      });
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
          this.$emit("close", true);
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
