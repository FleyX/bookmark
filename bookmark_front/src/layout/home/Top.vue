<template>
  <div class="top">
    <div>应用</div>
    <div>头像</div>
  </div>
</template>

<script>
import { mapState } from "vuex";
export default {
  name: "homeTop",
  data() {
    return {};
  },
  computed: {
    ...mapState("globalConfig", ["userInfo"]),
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
}
</style>
