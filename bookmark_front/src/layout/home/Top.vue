<template>
  <div></div>
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

<style lang="less" scoped></style>
