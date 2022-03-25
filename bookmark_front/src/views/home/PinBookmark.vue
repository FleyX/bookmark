<template>
  <div class="pinBookmark">
    <div class="oneLine">
      <pin-bookmark-item v-for="(item, index) in lineOne" :key="index" :pinObj="item" />
    </div>
    <div class="oneLine">
      <pin-bookmark-item v-for="(item, index) in lineTwo" :key="index" :pinObj="item" />
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
const LINE_NUM = 10;
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
    lineTwo() {
      if (this.homePinList.length < LINE_NUM) {
        return [];
      }
      if (this.homePinList.length - LINE_NUM < LINE_NUM) {
        return [...this.homePinList.slice(LINE_NUM), null];
      } else {
        return this.homePinList.slice(LINE_NUM, LINE_NUM * 2);
      }
    },
  },
};
</script>

<style lang="less" scoped>
.pinBookmark {
  .oneLine {
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
</style>
