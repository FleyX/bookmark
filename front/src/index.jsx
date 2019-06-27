import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import App from "./App";
import { BrowserRouter } from "react-router-dom";

const s = (
  <BrowserRouter>
    <App />
  </BrowserRouter>
);

ReactDOM.render(s, document.getElementById("root"));
