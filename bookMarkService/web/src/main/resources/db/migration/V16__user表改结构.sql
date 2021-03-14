alter table user
    add githubId bigint default -1 not null comment '-1说明未使用github登陆' after userId;


create index githubIdIndex
    on user (githubId);

create index new_email_index
    on user (newEmail);