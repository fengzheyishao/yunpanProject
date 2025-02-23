package com.yunpan.component;

import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.dto.DownloadFileDto;
import com.yunpan.entity.dto.FileTipDto;
import com.yunpan.entity.dto.SysSettingsDto;
import com.yunpan.entity.dto.UserSpaceDto;
import com.yunpan.entity.po.*;
import com.yunpan.entity.query.*;
import com.yunpan.enums.DateTimePatternEnum;
import com.yunpan.mappers.*;
import com.yunpan.utils.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;
    @Resource
    private DownloadFileMapper<DownloadFile, DownloadFileQuery> downloadFileMapper;

    public SysSettingsDto getSysSettingsDto() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    public void saveSysSettingsDto(SysSettingsDto sysSettingsDto) {
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
    }

    public void saveUserSpace(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_EXPIRE_TIME_ONE_DAY);
    }

    public UserSpaceDto resetUserSpaceUse(String userId) {
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
        userSpaceDto.setUseSpace(useSpace);
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId,
                userSpaceDto, Constants.REDIS_EXPIRE_TIME_ONE_DAY);
        return userSpaceDto;
    }

    public UserSpaceDto getUserSpace(String userId) {
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (spaceDto == null) {
            spaceDto = new UserSpaceDto();
            Long useSpace = fileInfoMapper.selectUseSpace(userId);
            spaceDto.setUseSpace(useSpace);
            spaceDto.getTotalSpace();
            saveUserSpace(userId, spaceDto);
        }
        return spaceDto;
    }

    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        Long currentSize = getFileTempSize(userId, fileId);
        redisUtils.setex(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, currentSize + fileSize, Constants.REDIS_KEY_EXPIRES_ONE_HOUR);

    }

    public Long getFileTempSize(String userId, String fileId) {
        Long currentSize = getFileSizeFromRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
        return currentSize;
    }

    private Long getFileSizeFromRedis(String key) {
        Object sizeObj = redisUtils.get(key);
        if (sizeObj == null) {
            return 0L;
        } else if (sizeObj instanceof Integer) {
            return ((Integer) sizeObj).longValue();
        } else if (sizeObj instanceof Long) {
            return (Long) sizeObj;
        }
        return 0L;
    }

    public void saveDownloadCode(String code, DownloadFileDto downloadFileDto) {
        redisUtils.setex(code, downloadFileDto, Constants.REDIS_EXPIRE_TIME_FIVE_MIN);
    }

    public DownloadFileDto getDownloadCode(String code) {
        return (DownloadFileDto) redisUtils.get(code);
    }

    public void saveYesterDayCount(String code, FileTipDto fileTipDto) {
        redisUtils.setex(code, fileTipDto, Constants.REDIS_EXPIRE_TIME_FIVE_MIN);
    }

    public FileTipDto getYesterDayCount(String userId) {
        FileTipDto fileTipDto = (FileTipDto) redisUtils.get(Constants.REDIS_KEY_USER_YESTERDAY_COUNT + userId);

        if (fileTipDto == null) {
            fileTipDto = findFileTipDtoByTime(-1, userId);
            redisUtils.set(Constants.REDIS_KEY_USER_YESTERDAY_COUNT + userId, fileTipDto);
        }
        return fileTipDto;
    }

    public FileTipDto findFileTipDtoByTime(int day, String userId) {
        Date date = DateUtils.getDateZero(day);
        String time = DateUtils.format(date, DateTimePatternEnum.YYYY_MM_DD.getPattern());
        FileTipDto fileTipDto = new FileTipDto();
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setCreateTimeStart(time);
        fileInfoQuery.setCreateTimeEnd(time);
        fileTipDto.setFileYesCount(fileInfoMapper.selectCount(fileInfoQuery));
        FileShareQuery fileShareQuery = new FileShareQuery();
        fileShareQuery.setUserId(userId);
        fileShareQuery.setShareTimeStart(time);
        fileShareQuery.setShareTimeEnd(time);
        List<FileShare> fileShareList = fileShareMapper.selectList(fileShareQuery);
        Integer showCount = 0;
        for (FileShare fileShare: fileShareList) {
            showCount += fileShare.getShowCount();
        }
        fileTipDto.setShowCountYesCount(showCount);
        DownloadFileQuery downloadFileQuery = new DownloadFileQuery();
        downloadFileQuery.setUserId(userId);
        downloadFileQuery.setDownloadTimeStart(time);
        downloadFileQuery.setDownloadTimeEnd(time);
        fileTipDto.setDownLoadYesCount(downloadFileMapper.selectCount(downloadFileQuery));
        return fileTipDto;
    }
}
