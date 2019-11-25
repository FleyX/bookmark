import React from "react";
import MainLayout from "../../layout/MainLayout";
import { connect } from "react-redux";
import * as action from "../../redux/action/LoginInfoAction";
import UserInfo from "./components/UserInfo";

function mapStateToProps(state) {
  return state[action.DATA_NAME];
}

function mapDispatchToProps(dispatch) {
  return {
    updateOne: (key, value) => dispatch(action.updateOne(key, value))
  };
}

class UserSpace extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  render() {
    return (
      <MainLayout>
        <UserInfo />
      </MainLayout>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(UserSpace);
