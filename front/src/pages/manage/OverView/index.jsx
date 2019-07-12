import React from "react";
import { Tree, Empty, Button } from "antd";
import MainLayout from "../../../layout/MainLayout";
import httpUtil from "../../../util/httpUtil";

const { TreeNode } = Tree;

export default class OverView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      treeData: [],
      isEdit: false,
      expandKeys: ["0"]
    };
  }

  componentDidMount() {
    httpUtil.get("/bookmark/currentUser").then(res => {
      const data = {
        bookmarkId: 0,
        userId: 0,
        name: "我的书签",
        children: res
      };
      this.setState({ treeData: [data] });
    });
  }

  renderTreeNodes(items) {
    return items.map(item => {
      if (item.children && item.children.length > 0) {
        return (
          <TreeNode title={item.name} key={item.bookmarkId} disableCheckbox={item.bookmarkId === 0}>
            {this.renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return <TreeNode title={item.name} key={item.bookmarkId} disableCheckbox={item.bookmarkId === 0} />;
    });
  }

  treeNodeSelect = e => {
    console.log(e);
  };

  render() {
    console.log("页面刷新了");
    const { isEdit, treeData, expandKeys } = this.state;
    const isEmply = treeData.length === 1 && treeData[0].children && treeData[0].children.length === 0;
    return (
      <MainLayout>
        <div>
          <Button type="primary" onClick={() => this.setState({ isEdit: true })}>
            编辑
          </Button>
        </div>
        {treeData.length ? (
          <Tree defaultExpandedKeys={expandKeys} checkable={isEdit} onSelect={this.treeNodeSelect} blockNode>
            {this.renderTreeNodes(treeData)}
          </Tree>
        ) : null}
        {isEmply ? (
          <Empty description="还没有数据">
            <Button type="primary" onClick={() => this.setState({ isEdit: true })}>
              编辑
            </Button>
          </Empty>
        ) : null}
      </MainLayout>
    );
  }
}
