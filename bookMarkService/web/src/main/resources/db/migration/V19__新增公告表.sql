-- 创建公告表
create table notify_announce
(
    notifyAnnounceId int auto_increment,
    senderId         int          not null,
    title            varchar(200) not null,
    content          text         null,
    createdDate      bigint(20)   not null,
    startDate        bigint(20)   not null comment '公告开始时间',
    endDate          bigint(20)   not null comment '公告结束时间',
    constraint notify_announce_pk
        primary key (notifyAnnounceId)
)
    comment '公告表';

create index notify_announce_created_date_index
    on notify_announce (createdDate);

-- 创建公告用户表
create table user_notify_announce
(
    userId           int               not null,
    notifyAnnounceId int               not null,
    status           tinyint default 0 null comment '0:未读，1:已读',
    readDate         bigint(20)        null comment '阅读时间',
    constraint user_announce_pk
        primary key (userId, notifyAnnounceId)
)
    comment '用户公告表';
create index user_notify_announce_user_status_index
    on user_notify_announce (userId, status);

-- 用户表新增上次同步时间
alter table user
    add lastSyncAnnounceDate bigint(20) default 0 not null comment '上次同步公告时间';