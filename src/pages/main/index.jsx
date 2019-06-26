import React from "react";
import { connect } from "react-redux";

function mapStateToProps(state) {
  return {
    loginInfo: state.loginInfo
  };
}

function mapDispatchToProps(dispatch) {
  return {};
}

class Main extends React.Component {
  constructor(props) {
    console.log(props);
    super(props);
    this.state = {};
  }

  render() {
    const { token, userInfo } = this.props.loginInfo;
    if (token == null) {
      return <div>这是首页，你还没登陆</div>;
    }
    return <div>你好：{userInfo.name}</div>;
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Main);
