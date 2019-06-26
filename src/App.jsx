import React, { Component } from "react";
import { Route } from "react-router-dom";
import { withRouter } from "react-router-dom";
import { Provider } from "react-redux";
import store from "./redux";
import Login from "./pages/public/Login";
import Main from "./pages/main";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {};
    this.checkRoute(this.props.location.pathname);
  }

  componentWillReceiveProps(nextProps) {
    this.checkRoute(nextProps.location.pathname);
  }

  checkRoute(currentPath) {
    // if (currentPath !== "/login") {
    //   console.log("不在登录页，跳转到登录页去");
    //   this.props.history.replace("/login");
    // }
  }

  render() {
    const mainStyle = {
      fontSize: "0.16rem"
    };
    return (
      <Provider store={store}>
        <div className="fullScreen" style={mainStyle}>
          <Route exact path="/" component={Main} />
          <Route exact path="/login" component={Login} />
        </div>
      </Provider>
    );
  }
}

export default withRouter(App);
