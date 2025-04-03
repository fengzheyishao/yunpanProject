create table file_download
(
    download_id   varchar(32)                        not null comment '下载记录ID'
        primary key,
    share_id      varchar(32)                        not null comment '分享ID',
    file_id       varchar(32)                        not null comment '文件ID',
    user_id       varchar(32)                        not null comment '下载用户ID',
    download_time datetime default CURRENT_TIMESTAMP null comment '下载时间',
    constraint fk_download_file
        foreign key (file_id) references file_info (file_id)
            on delete cascade,
    constraint fk_download_share
        foreign key (share_id) references file_share (share_id)
            on delete cascade,
    constraint fk_download_user
        foreign key (user_id) references user_info (user_id)
            on delete cascade
)
    comment '文件下载记录表' charset = utf8mb4;

create index idx_file_id
    on file_download (file_id);

create index idx_share_id
    on file_download (share_id);

create index idx_user_id
    on file_download (user_id);

