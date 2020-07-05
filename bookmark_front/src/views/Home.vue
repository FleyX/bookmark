<template>
  <div class="home">
    <!-- {{ count}}
    <div><button @click="setCount(1)">增加1</button></div>
    <div><button @click="asyncAdd(10)">异步增加</button></div>
    <Button type="primary">button</Button> -->
  </div>
</template>

<script>
// @ is an alias to /src
// import { mapState, mapActions, mapMutations } from 'vuex';

export default {
  name: "Home",
  computed: {
    // ...mapState({
    //   count: (state) => state.count.count,
    // }),
  },
  watch: {},
  methods: {
    // ...mapActions('count', ['asyncAdd']),
    // ...mapMutations('count', ['setCount']),
  }
};
</script>

<style lang="less" scoped></style>
