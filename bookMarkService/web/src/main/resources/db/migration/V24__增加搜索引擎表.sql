create table search_engine
(
    id   int auto_increment
        primary key,
    userId int not null,
    checked tinyint not null default 0,
    name varchar(20)  null,
    url  varchar(500) null,
    icon varchar(20)  null
) auto_increment=1001;
create index search_engine_userId_index
    on search_engine (userId);

insert into search_engine(userId, checked, name, url, icon)
select userId, if(defaultSearchEngine = 'baidu', 1, 0), '百度', 'https://www.baidu.com/s?ie=UTF-8&wd=%s', 'icon-baidu'
from user;
insert into search_engine(userId, checked, name, url, icon)
select userId, if(defaultSearchEngine = 'bing', 1, 0), '必应', 'https://www.bing.com/search?q=%s', 'icon-bing'
from user;
insert into search_engine(userId, checked, name, url, icon)
select userId, if(defaultSearchEngine = 'google', 1, 0), '谷歌', 'https://www.google.com/search?q=%s', 'icon-google'
from user;
