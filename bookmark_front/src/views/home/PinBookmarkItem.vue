<template>
  <div>
    <a href="pinObj.url" v-if="pinObj" class="pinBookmarkItem">
      <img :src="pinObj.icon.length > 0 ? pinObj.icon : '/favicon.ico'" class="icon" />
      <span class="text" :title="pinObj.name">{{ pinObj.name }}</span>
      <span class="action actionShow">...</span>
    </a>
    <div v-else class="pinBookmarkItem" @click="showAddBlock = !showAddBlock" title="新增书签并固定到首页">
      <a-icon style="font-size: 1.5em" type="plus" />
    </div>
  </div>
</template>

<script>
export default {
  name: "pinBookmarkItem",
  props: {
    pinObj: Object,
  },
  data() {
    return {
      showAddBlock: false,
    };
  },
  methods: {
    add() {},
  },
};
</script>

<style lang="less" scoped>
.pinBookmarkItem {
  margin-top: 0.1rem;
  border-radius: 5px;
  width: 6em;
  height: 5.5em;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  cursor: pointer;

  .icon {
    width: 2.8em;
    height: 2.8em;
    padding: 0.5em;
    border-radius: 8px;
    background-color: #4a4a4a;
  }

  .text {
    font-size: 0.8em;
    max-width: 5.5em;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .action {
    position: absolute;
    top: 0;
    right: 0.3em;
    display: none;
  }
}

.pinBookmarkItem:hover {
  background-color: rgba(66, 66, 66, 0.4);
}

.pinBookmarkItem:hover .action {
  display: block;
}
</style>
