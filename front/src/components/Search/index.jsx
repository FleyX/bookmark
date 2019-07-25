import React from "react";
import { Input } from "antd";
import styles from "./index.module.less";
import httpUtil from "../../util/httpUtil";

class Search extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      content: "",
      currentIndex: 0,
      isFocus: false,
      resultList: []
    };
  }

  contentChange(e) {
    const content = e.target.value.trim();
    this.setState({ content });
    this.search(content);
  }

  search(content) {
    if (content.length === 0) {
      this.setState({ resultList: [] });
      return;
    }
    httpUtil.get("/bookmark/searchUserBookmark?content=" + encodeURIComponent(content)).then(res => this.setState({ resultList: res }));
  }

  goTo(url) {
    window.open(url);
  }

  keyUp(e) {
    const { isFocus, resultList } = this.state;
    let currentIndex = this.state.currentIndex;
    if (!isFocus || resultList.length === 0) {
      return;
    }
    switch (e.keyCode) {
      //上
      case 38:
        currentIndex--;
        break;
      //下
      case 40:
        currentIndex++;
        break;
      // enter
      case 13:
        this.goTo(resultList[currentIndex].url);
        return;
      default:
        return;
    }
    if (currentIndex < 0) {
      currentIndex = resultList.length - 1;
    } else if (currentIndex > resultList.length - 1) {
      currentIndex = 0;
    }
    this.setState({ currentIndex });
    e.preventDefault();
  }

  render() {
    const { content, resultList, currentIndex } = this.state;
    return (
      <div className={styles.main}>
        <Input.Search
          ref="searchInput"
          value={content}
          placeholder="搜索书签"
          enterButton
          allowClear
          onChange={this.contentChange.bind(this)}
          onKeyDown={this.keyUp.bind(this)}
          onFocus={() => this.setState({ isFocus: true })}
          onBlur={() => this.setState({ isFocus: false })}
        />
        {resultList.length > 0 ? (
          <div className={styles.resultList}>
            {resultList.map((item, index) => (
              <div
                className={`${styles.item} ${index === currentIndex ? styles.checked : ""}`}
                key={item.bookmarkId}
                onClick={this.goTo.bind(this, item.url)}
              >
                <span style={{ fontWeight: 600 }}>{item.name}&emsp;</span>
                <span style={{ fontSize: "0.8em", fontWeight: 400 }}>{item.url}</span>
              </div>
            ))}
          </div>
        ) : null}
      </div>
    );
  }
}

export default Search;
