const Koa = require("koa");
const pinyin = require("pinyin");

const config = require("./config.js");

const app = new Koa();

console.log("配置为:" + JSON.stringify(config));

//错误处理,正常情况下200,未登录返回401，其他返回500
app.use(async (ctx, next) => {
  try {
    await next();
    ctx.res.statusCode = 200;
  } catch (error) {
    log.error(error);
    if (error.message.startsWith("token")) {
      ctx.res.statusCode = 401;
    } else {
      ctx.res.statusCode = 500;
    }
    ctx.body(error.message);
  }
});

//检查token
app.use(async (ctx, next) => {
  if (!ctx.req.headers["token"] === config.token) {
    throw new Error("token校验失败");
  }
  await next();
});

//业务处理
app.use(async ctx => {
  if (ctx.req.url !== "/pinyinChange" && ctx.req.method !== "post") {
    throw new Error("路径错误");
  }
  let body = ctx.request.body;
  let style;
  switch (body.style) {
    case 1:
      style = pinyin.STYLE_NORMAL;
      break;
    case 2:
      style = pinyin.STYLE_TONE;
      break;
    case 3:
      style = pinyin.STYLE_TONE2;
      break;
    case 4:
      style = pinyin.STYLE_TO3NE;
      break;
    case 5:
      style = pinyin.STYLE_INITIALS;
      break;
    case 5:
      style = pinyin.STYLE_FIRST_LETTER;
      break;
    default:
      style = pinyin.STYLE_NORMAL;
  }
  body.config.style = style;
  let res = [];
  body.strs.forEach(item => res.push(pinyin(item, body.config.style)));
  ctx.body = res;
});

app.listen(config.port, () => {
  console.log("app start at " + config.port);
});
