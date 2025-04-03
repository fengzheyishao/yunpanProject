create table email_code
(
    email       varchar(255)                       not null comment '邮箱',
    code        varchar(10)                        not null comment '验证码',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    status      tinyint  default 0                 null comment '0:未使用 1:已使用',
    primary key (email, code)
)
    comment '邮箱验证码表' charset = utf8mb4;

create index idx_create_time
    on email_code (create_time);

