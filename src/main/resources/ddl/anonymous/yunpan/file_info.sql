create table file_info
(
    file_id          varchar(32)                        not null comment '文件ID'
        primary key,
    user_id          varchar(32)                        not null comment '用户ID',
    file_md5         varchar(32)                        null comment '文件MD5',
    file_pid         varchar(32)                        null comment '父级ID',
    file_size        bigint                             null comment '文件大小(字节)',
    file_name        varchar(255)                       null comment '文件名',
    file_cover       varchar(255)                       null comment '封面',
    file_path        varchar(500)                       null comment '文件路径',
    nick_name        varchar(100)                       null comment '上传者昵称',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    last_update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '最后修改时间',
    folder_type      tinyint                            null comment '0:文件 1:目录',
    file_category    tinyint                            null comment '1:视频 2:音频 3:图片 4:文档 5:其他',
    file_type        tinyint                            null comment '1:视频 2:音频...',
    status           tinyint  default 0                 null comment '0:转码中 1:成功 2:失败',
    recovery_time    datetime                           null comment '进入回收站时间',
    del_flag         tinyint  default 2                 null comment '0:删除 1:回收站 2:正常'
)
    comment '文件信息表' charset = utf8mb4;

create index idx_create_time
    on file_info (create_time);

create index idx_file_md5
    on file_info (file_md5);

create index idx_file_pid
    on file_info (file_pid);

create index idx_user_id
    on file_info (user_id);

