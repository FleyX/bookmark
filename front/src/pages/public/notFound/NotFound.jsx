import React from "react";
import { Link } from "react-router-dom";

const style = {
  "text-align": "center",
  "padding-top": "200px"
};

const render = () => {
  return (
    <div style={style}>
      404 您的页面走丢啦！ <Link to="/">返回首页</Link>
    </div>
  );
};

export default render;
