create table file_share
(
    share_id      varchar(32)                        not null comment '分享ID'
        primary key,
    file_id       varchar(32)                        not null comment '文件ID',
    user_id       varchar(32)                        not null comment '用户ID',
    valid_type    tinyint                            null comment '0:1天 1:7天 2:30天 4:永久',
    expire_time   datetime                           null comment '失效时间',
    share_time    datetime default CURRENT_TIMESTAMP null comment '分享时间',
    code          varchar(10)                        null comment '提取码',
    show_count    int      default 0                 null comment '浏览次数',
    file_name     varchar(255)                       null comment '文件名',
    file_cover    varchar(255)                       null comment '封面',
    file_category tinyint                            null comment '文件分类',
    file_type     tinyint                            null comment '文件类型',
    folder_type   tinyint                            null comment '0:文件 1:目录'
)
    comment '文件分享表' charset = utf8mb4;

create index idx_expire_time
    on file_share (expire_time);

create index idx_file_id
    on file_share (file_id);

create index idx_user_id
    on file_share (user_id);

