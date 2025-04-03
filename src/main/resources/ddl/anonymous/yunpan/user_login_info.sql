create table user_login_info
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    user_id         varchar(32)   not null comment '用户ID',
    login_date      date          not null comment '登录日期（年月日）',
    login_count     int default 0 null comment '登录次数',
    login_last_date datetime      null,
    constraint idx_user_login_date
        unique (user_id, login_date)
)
    comment '用户登录信息表' charset = utf8mb4;

create index idx_login_date
    on user_login_info (login_date);

