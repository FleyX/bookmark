const proxy = require("http-proxy-middleware");

module.exports = function(app) {
  app.use(
    proxy("/bookmark/api/**", {
      target: "http://localhost:8088/",
      // target: "http://ali.tapme.top:8083/",
      changeOrigin: true
    })
  );
};
