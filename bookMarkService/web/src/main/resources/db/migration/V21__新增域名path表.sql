CREATE TABLE bookmark.host_icon (
	id INT UNSIGNED auto_increment NOT NULL,
	host varchar(300) NOT NULL COMMENT 'host',
	iconPath varchar(330) NOT NULL,
	CONSTRAINT host_icon_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
CREATE INDEX host_icon_host_IDX USING BTREE ON bookmark.host_icon (host(20));
