package com.yunpan.mappers;

import com.yunpan.entity.query.DownloadFileQuery;
import com.yunpan.entity.vo.DownloadFileVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文件下载记录Mapper
 * @auther: lnorly
 * @Date: 2024/09/23
 */
public interface DownloadFileMapper<T, P> extends BaseMapper {
    List<DownloadFileVO> selectDownloadFileByUserInfo(@Param("query") DownloadFileQuery query);
}
