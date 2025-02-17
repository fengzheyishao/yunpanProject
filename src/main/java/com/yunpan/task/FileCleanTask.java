package com.yunpan.task;

import com.yunpan.entity.po.FileInfo;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.enums.FileDelFlagEnums;
import com.yunpan.service.FileInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileCleanTask {
    @Resource
    private FileInfoService fileInfoService;

    @Scheduled(fixedDelay = 1000 * 60 * 3)
    public void execute() {
        FileInfoQuery query = new FileInfoQuery();
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        query.setQueryExpire(true);
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(query);
        Map<String, List<FileInfo>> fileInfoMap = fileInfoList.stream().collect(Collectors.groupingBy(FileInfo::getUserId));
        for (Map.Entry<String, List<FileInfo>> entry : fileInfoMap.entrySet()) {
            List<String> fileIds = entry.getValue().stream().map(p->p.getFileId()).collect(Collectors.toList());
            fileInfoService.deleteFileBatch(entry.getKey(), String.join(",", fileIds), false);
        }
    }
}
