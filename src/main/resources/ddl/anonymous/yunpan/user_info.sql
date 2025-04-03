create table user_info
(
    user_id         varchar(32)                        not null comment '用户ID'
        primary key,
    nick_name       varchar(100)                       null comment '昵称',
    email           varchar(255)                       null comment '邮箱',
    qq_open_id      varchar(100)                       null comment 'QQ开放ID',
    qq_avatar       varchar(500)                       null comment 'QQ头像',
    password        varchar(100)                       null comment '密码',
    join_time       datetime default CURRENT_TIMESTAMP null comment '注册时间',
    last_login_time datetime                           null comment '最后登录时间',
    status          tinyint  default 0                 null comment '状态（预留字段）',
    use_space       bigint   default 0                 null comment '已使用空间（字节）',
    total_space     bigint   default 10737418240       null comment '总空间（字节，默认10GB）',
    constraint email
        unique (email),
    constraint idx_email
        unique (email),
    constraint idx_qq_openid
        unique (qq_open_id),
    constraint qq_open_id
        unique (qq_open_id)
)
    comment '用户信息表' charset = utf8mb4;

create index idx_join_time
    on user_info (join_time);

create index idx_last_login_time
    on user_info (last_login_time);

