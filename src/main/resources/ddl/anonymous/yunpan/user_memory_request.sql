create table user_memory_request
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    user_id          varchar(32)       not null comment '用户ID',
    request_time     datetime          not null comment '申请时间',
    request_size     bigint            not null comment '申请内存大小（字节）',
    status           tinyint default 0 null comment '申请状态：0-待处理，1-已批准，2-已拒绝',
    approval_time    datetime          null comment '批准时间',
    rejection_reason varchar(500)      null comment '拒绝原因',
    notes            varchar(1000)     null comment '备注'
)
    comment '用户内存申请信息表' charset = utf8mb4;

create index idx_request_time
    on user_memory_request (request_time);

create index idx_status
    on user_memory_request (status);

create index idx_user_id
    on user_memory_request (user_id);

