<template>
  <div class="main">
    <div>
      开源地址:
      <a href="https://github.com/FleyX/bookmark" target="_blank">github.com/FleyX/bookmark</a>
      &emsp;
      <a href="https://github.com/FleyX/bookmark/issues" target="_blank">反馈/建议</a>
    </div>
    <!-- 统计 -->
    <a href="https://qiezi.fleyx.com" style="display: block; text-align: center; color: black" target="_blank">
      <div id="qieziStatisticHtmlHostPv" style="display: none">
        总访问次数:
        <span id="qieziStatisticHtmlHostPvValue"></span>
        次&nbsp;
      </div>
      <div id="qieziStatisticHtmlHostUv" style="display: none">
        &nbsp;总访客数:
        <span id="qieziStatisticHtmlHostUvValue"></span>
        人
      </div>
    </a>
  </div>
</template>

<script>
export default {
  name: "Bottom",
  mounted() {
    window.qieziStatisticKey = "b74c4b571b644782a837433209827874";
    let script = document.createElement("script");
    script.type = "text/javascript";
    script.defer = true;
    script.src = "https://qiezi.fleyx.com/qiezijs/1.0/qiezi_statistic.min.js";
    document.getElementsByTagName("head")[0].appendChild(script);
  },
};
</script>

<style lang="less" scoped>
@import "../../global";
.main {
  background-color: rgb(207, 198, 198);
  height: @bottomHeight;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 0.14rem;
}
</style>
