package com.yunpan.service;

import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.po.DownloadFile;
import com.yunpan.entity.query.DownloadFileQuery;
import com.yunpan.entity.vo.DownloadFileVO;
import com.yunpan.entity.vo.PaginationResultVO;

import java.util.List;

public interface DownloadFileService {
    Integer add(String shareId, String fileId, SessionWebUserDto userDto);
    List<DownloadFileVO> findListByParam(DownloadFileQuery query);
    Integer findCountByParam(DownloadFileQuery query);
    PaginationResultVO<DownloadFileVO> findListByPage(DownloadFileQuery query);
}
