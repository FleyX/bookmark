alter table user
    add githubId bigint default -1 not null comment '-1说明未使用github登陆' after userId;

create unique index email_index
    on user (email);

create index githubIdIndex
    on user (githubId);

create index new_email_index
    on user (newEmail);

create unique index username_index
    on user (username);