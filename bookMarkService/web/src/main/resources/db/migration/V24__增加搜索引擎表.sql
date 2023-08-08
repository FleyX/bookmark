create table search_engine
(
    id   int auto_increment
        primary key,
    name varchar(20)  null,
    url  varchar(500) null,
    icon varchar(20)  null
);
INSERT INTO bookmark.search_engine (name, url, icon)
VALUES ('谷歌', 'https://www.google.com/search?q=%s', 'icon-google');

INSERT INTO bookmark.search_engine (name, url, icon)
VALUES ('必应', 'https://www.bing.com/search?q=%s', 'icon-bing');

INSERT INTO bookmark.search_engine (name, url, icon)
VALUES ('百度', 'https://www.bing.com/search?q=%s', 'icon-baidu');