const proxy = require("http-proxy-middleware");

module.exports = function(app) {
  app.use(
    proxy("/bookmark/api/**", {
      target: "http://10.82.17.56:8088/",
      changeOrigin: true
    })
  );
};
