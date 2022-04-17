<template>
  <div class="about">
    <div>一个开源的书签管理系统。</div>
    <div>
      源码地址：
      <a href="https://github.com/FleyX/bookmark" target="_blank">github.com/FleyX/bookmark</a>
    </div>
    <div>
      使用教程：
      <a href="https://blog.fleyx.com/blog/detail/20220329" target="_blank">点击跳转</a>
    </div>
    <div>
      浏览器插件：
      <a href="/static/bookmarkBrowserPlugin.zip" download="浏览器插件.zip" target="_blank">点击下载</a>
      ,使用详情请参考使用教程
    </div>
    <div>交流反馈qq群：150056494,邮箱：fleyx20@outlook.com</div>
    <div>
      统计：
      <a href="https://qiezi.fleyx.com" style="" target="_blank">
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
  </div>
</template>

<script>
export default {
  name: "about",
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

<style lang="less" scoped></style>
