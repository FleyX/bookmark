let port = parseInt(process.argv[2]);
let token = process.argv[3];

module.exports = {
  port: Number.isInteger(port) ? port : 8012,
  token: token || "123456"
};
