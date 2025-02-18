package com.yunpan.service.serviceImpl;

import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.po.DownloadFile;
import com.yunpan.entity.query.DownloadFileQuery;
import com.yunpan.entity.query.EmailCodeQuery;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.query.SimplePage;
import com.yunpan.entity.vo.DownloadFileVO;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.enums.PageSize;
import com.yunpan.mappers.DownloadFileMapper;
import com.yunpan.service.DownloadFileService;
import com.yunpan.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service("downloadFileService")
public class DownloadFileServiceImpl implements DownloadFileService {
    private static Logger logger = LoggerFactory.getLogger(DownloadFileServiceImpl.class);

    @Resource
    private DownloadFileMapper<DownloadFile, DownloadFileQuery> downloadFileMapper;


    @Override
    public Integer add(String shareId, String fileId, SessionWebUserDto userDto) {
        if (userDto == null) {
            throw new RuntimeException("请登录");
        }
        Date curDate = new Date();
        String downloadId = StringTools.getRandomNumber(Constants.LEN_10);
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setDownloadId(downloadId);
        downloadFile.setFileId(fileId);
        downloadFile.setDownloadTime(curDate);
        downloadFile.setUserId(userDto.getUserId());
        downloadFile.setShareId(shareId);
        return downloadFileMapper.insert(downloadFile);
    }

    @Override
    public List<DownloadFileVO> findListByParam(DownloadFileQuery query) {
        return this.downloadFileMapper.selectDownloadFileByUserInfo(query);
    }

    @Override
    public Integer findCountByParam(DownloadFileQuery query) {
        return this.downloadFileMapper.selectCount(query);
    }

    @Override
    public PaginationResultVO<DownloadFileVO> findListByPage(DownloadFileQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();

        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<DownloadFileVO> list = this.findListByParam(query);
        PaginationResultVO<DownloadFileVO> resultVO = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return resultVO;
    }


}
