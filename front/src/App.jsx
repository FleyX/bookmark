import React, { Component } from "react";
import { Route, Switch, Redirect } from "react-router-dom";
import { withRouter } from "react-router-dom";
import { Provider } from "react-redux";
import store from "./redux";
import Login from "./pages/public/Login";
import Main from "./pages/index";
import NotFound from "./pages/public/notFound/NotFound";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const mainStyle = {
      fontSize: "0.14rem"
    };
    return (
      <Provider store={store}>
        <div className="fullScreen" style={mainStyle}>
          <Switch>
            <Route exact path="/" component={Main} />
            <Route exact path="/public/login" component={Login} />
            <Route exact path="/404" component={NotFound} />
            {/* 当前面的路由都匹配不到时就会重定向到/404 */}
            <Redirect path="/" to="/404" />
          </Switch>
        </div>
      </Provider>
    );
  }
}

export default withRouter(App);
