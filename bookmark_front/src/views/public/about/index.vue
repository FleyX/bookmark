<template>
  <div class="about">
    <div>一个开源的书签管理系统。</div>
    <div>
      源码地址：
      <a href="https://github.com/FleyX/bookmark" target="_blank">github.com/FleyX/bookmark</a>
    </div>
    <div>
      当前版本：{{ appVersion }}&emsp;&emsp;<a v-if="showNewVersion"
                                              href="https://github.com/FleyX/bookmark/blob/master/DEPLOY.md">最新版本：{{ latestVersion
      }}</a>
    </div>
    <div>
      使用教程：
      <a href="https://blog.fleyx.com/blog/detail/20220329" target="_blank">点击跳转</a>
    </div>
    <div>
      浏览器插件：
      <a href="/static/bookmarkBrowserPlugin.zip" download="浏览器插件.zip"
         target="_blank">最新版本{{ serverConfig.map.pluginVersion }}(注意更新)</a>
      ,使用详情请参考使用教程
    </div>
    <div>交流反馈qq群：150056494,邮箱：fleyx20@outlook.com</div>
    <div>
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
import { mapState } from "vuex";
import { GLOBAL_CONFIG, SERVER_CONFIG } from "@/store/modules/globalConfig";
import httpUtil from "@/util/HttpUtil";

export default {
  name: "about",
  data() {
    return {
      appVersion: "1.4", //应用版本
      latestVersion: null,
      showNewVersion: false
    };
  },
  computed: { ...mapState(GLOBAL_CONFIG, [SERVER_CONFIG]) },
  async mounted() {


    //获取最新版本
    let res = await httpUtil.http.default.get("https://s3.fleyx.com/picbed/bookmark/config.json");
    console.log(res);
    this.latestVersion = res.data.latestAppVersion;
    this.showNewVersion = this.checkVersion(this.appVersion, this.latestVersion);
  },
  methods: {
    checkVersion: function(version, latestVersion) {
      let versions = version.split(".");
      let latestVersions = latestVersion.split(".");
      for (let i = 0; i < versions.length; i++) {
        if (i >= latestVersions.length) {
          return false;
        }
        let versionNum = parseInt(versions[i]);
        let latestVersionNum = parseInt(latestVersions[i]);
        if (versionNum !== latestVersionNum) {
          return versionNum < latestVersionNum;
        }
      }
      return true;
    }
  }
}
;
</script>

<style lang="less" scoped></style>
