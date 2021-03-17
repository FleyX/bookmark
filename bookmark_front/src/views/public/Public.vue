<template>
  <div class="main">
    <img class="ico" src="/static/img/bookmarkLogo.png" />
    <div class="main-body">
      <router-view />

    </div>
  </div>
</template>

<script>
export default {
  name: "Public",
  async created() {
  },
};
</script>

<style lang="less" scoped>
@import "../../global.less";
.main {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  .ico {
    position: fixed;
    left: 5%;
    top: 5%;
    width: 2rem;
  }
  @media screen and (max-width: 500px) {
    .main-body {
      min-width: 100%;
    }
  }

  @media screen and (min-width: 500px) {
    .main-body {
      min-width: 5rem;
    }
  }

  .main-body {
    // min-height: 3.5rem;
    background-color: @publicBgColor;
    border-radius: 5px;
    padding: 0.1rem;
  }
}
</style>
