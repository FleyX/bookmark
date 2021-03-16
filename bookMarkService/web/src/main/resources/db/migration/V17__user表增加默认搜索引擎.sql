alter table user
    add defaultSearchEngine enum('baidu', 'google', 'bing') default 'baidu' not null comment '默认搜索引擎，baidu,google,bing';