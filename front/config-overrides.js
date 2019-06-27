const { override, fixBabelImports, addLessLoader } = require("customize-cra");

module.exports = override(
  //antd按需引入
  fixBabelImports("import", {
    libraryName: "antd",
    libraryDirectory: "es",
    style: true
  }),
  //配置less，同时通过less的变量覆盖功能配置antd主题
  addLessLoader({
    //设置css类型生成规则
    localIdentName: "[local]--[hash:base64:5]",
    javascriptEnabled: true,
    // modifyVars: { "@primary-color": "#1DA57A" }
  })
);
