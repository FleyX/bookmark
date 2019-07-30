import React from "react";
import { Input, Select } from "antd";
import styles from "./index.module.less";
import httpUtil from "../../util/httpUtil";

class Search extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      content: "",
      currentIndex: 0,
      isFocus: false,
      resultList: [],
      options: ["书签", "百度", "谷歌"],
      currentOptionIndex: 0
    };
  }

  componentWillUnmount() {
    this.clearTimer();
  }

  valueIndexChange(newIndex) {
    const { content } = this.state;
    this.setState({ currentOptionIndex: newIndex });
    if (newIndex === 0) {
      this.search(content);
    } else {
      this.setState({ resultList: [] });
    }
  }

  /**
   * 输入内容改变
   */
  contentChange(e) {
    const { currentOptionIndex } = this.state;
    const content = e.target.value;
    this.setState({ content });
    if (currentOptionIndex > 0) {
      this.setState({ resultList: [] });
      this.clearTimer();
      return;
    }
    this.clearTimer();
    this.timer = setTimeout(() => {
      this.search(content);
      this.clearTimer();
    }, 300);
  }

  clearTimer() {
    if (this.timer != null) {
      clearTimeout(this.timer);
    }
  }

  /**
   * 关键词检索
   */
  search(content) {
    if (content.length === 0) {
      this.setState({ resultList: [] });
      return;
    }
    httpUtil.get("/bookmark/searchUserBookmark?content=" + encodeURIComponent(content)).then(res => this.setState({ resultList: res }));
  }

  /**
   * 处理跳转到搜索引擎或者对应的书签
   */
  enter() {
    const { currentIndex, currentOptionIndex, resultList, content } = this.state;
    if (currentOptionIndex === 0 && resultList.length > 0) {
      window.open(resultList[currentIndex].url);
    }
    if (currentOptionIndex === 1) {
      window.open("https://www.baidu.com/s?ie=UTF-8&wd=" + encodeURIComponent(content));
    } else if (currentOptionIndex === 2) {
      window.open("https://www.google.com/search?q=" + encodeURIComponent(content));
    }
  }

  /**
   * 处理键盘按下事件
   * @param {*} e
   */
  keyUp(e) {
    const { isFocus, resultList, currentOptionIndex } = this.state;
    let currentIndex = this.state.currentIndex;
    if (!isFocus) {
      return;
    }
    switch (e.keyCode) {
      // tab
      case 9:
        this.valueIndexChange((currentOptionIndex + 1) % 3);
        e.preventDefault();
        return;
      //上
      case 38:
        currentIndex--;
        break;
      //下
      case 40:
        currentIndex++;
        break;
      default:
        return;
    }
    if (resultList.length > 0) {
      if (currentIndex < 0) {
        currentIndex = resultList.length - 1;
      } else if (currentIndex > resultList.length - 1) {
        currentIndex = 0;
      }
      this.setState({ currentIndex });
      e.preventDefault();
    }
  }

  render() {
    const { content, resultList, currentIndex, options, currentOptionIndex } = this.state;
    const prefix = (
      <Select value={options[currentOptionIndex]} onChange={this.valueIndexChange.bind(this)}>
        {options.map((item, index) => (
          <Select.Option key={index} value={index}>
            {item}
          </Select.Option>
        ))}
      </Select>
    );
    return (
      <div className={styles.main}>
        <Input.Search
          addonBefore={prefix}
          ref="searchInput"
          value={content}
          placeholder={currentOptionIndex === 0 ? "检索我的书签" : "搜索"}
          enterButton
          allowClear
          onSearch={this.enter.bind(this)}
          onChange={this.contentChange.bind(this)}
          onKeyDown={this.keyUp.bind(this)}
          onFocus={() => this.setState({ isFocus: true })}
          onBlur={() => this.setState({ isFocus: false })}
        />
        {resultList.length > 0 ? (
          <div className={styles.resultList}>
            {resultList.map((item, index) => (
              <div className={`${styles.item} ${index === currentIndex ? styles.checked : ""}`} key={item.bookmarkId} onClick={this.enter.bind(this)}>
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
