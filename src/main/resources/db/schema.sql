use cloud;

CREATE TABLE if NOT EXISTS user
(
    id             bigint unsigned auto_increment comment 'ID' primary key,
    username       varchar(64)  not null comment '用户名' unique,
    password       char(64)  not null comment '密码',
    email           varchar(64)  not null comment '个人邮箱',
    phone_number   varchar(16)  comment '个人电话',
    allocated       int         default 1024 comment '个人配额',
    create_time    datetime     not null comment '创建时间',
    update_time    datetime     not null comment '修改时间',
    is_deleted      boolean default 0 comment '是否删除',
)

