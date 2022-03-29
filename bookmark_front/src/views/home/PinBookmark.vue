<template>
  <div class="pinBookmark">
    <div class="oneLine">
      <pin-bookmark-item v-for="(item, index) in lineOne" :key="index" :pinObj="item" />
    </div>
  </div>
</template>

<script>
import PinBookmarkItem from "./PinBookmarkItem.vue";
import { mapState } from "vuex";
import { TREE_DATA, HOME_PIN_LIST } from "@/store/modules/treeData";
/**
 * 首页网页固定
 */
const LINE_NUM = 20;
export default {
  name: "PinBookmark",
  components: { PinBookmarkItem },
  computed: {
    ...mapState(TREE_DATA, [HOME_PIN_LIST]),
    lineOne() {
      //添加null说明此格展示为新增
      if (this.homePinList.length < LINE_NUM) {
        return [...this.homePinList, null];
      } else {
        return this.homePinList.slice(0, LINE_NUM);
      }
    },
  },
};
</script>

<style lang="less" scoped>
.pinBookmark {
  .oneLine {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;
    margin: 0 auto;
    max-width: 62em;
  }
}
</style>
