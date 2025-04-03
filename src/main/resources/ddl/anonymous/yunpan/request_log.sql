create table request_log
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    user_id         varchar(32)   null comment '用户id',
    url             varchar(500)  not null comment '请求URL',
    method          varchar(10)   not null comment '请求方法（GET/POST/PUT/DELETE等）',
    request_headers text          null comment '请求头（JSON格式）',
    request_body    text          null comment '请求体（JSON格式）',
    response_body   text          null comment '响应体（JSON格式）',
    response_status int           not null comment '响应状态码',
    timestamp       date          not null comment '请求时间戳',
    log_status      int default 4 not null
)
    comment '请求日志信息表' charset = utf8mb4;

create index idx_method
    on request_log (method);

create index idx_response_status
    on request_log (response_status);

create index idx_timestamp
    on request_log (timestamp);

create index idx_url
    on request_log (url(255));

create index request_log_log_status_index
    on request_log (log_status);

