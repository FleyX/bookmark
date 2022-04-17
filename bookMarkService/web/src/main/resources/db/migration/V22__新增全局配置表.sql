CREATE TABLE bookmark.global_config
(
    code  varchar(20)  NOT NULL,
    value varchar(100) NOT NULL COMMENT '值',
    description  varchar(100) NOT NULL COMMENT '描述',
    CONSTRAINT global_config_pk PRIMARY KEY (code)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci
COMMENT='全局配置表';

insert into global_config
values ("pluginVersion", "0.1.1", "浏览器插件版本");
