package com.yunpan.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 文件下载记录实体类
 * @auther: lnorly
 * @Date: 2024/09/23
 */
public class DownloadFile implements Serializable {
    /**
     * 下载记录ID
     */
    private String downloadId;

    /**
     * 分享ID
     */
    private String shareId;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 下载用户ID
     */
    private String userId;

    /**
     * 下载时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT-8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date downloadTime;

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(Date downloadTime) {
        this.downloadTime = downloadTime;
    }

    @Override
    public String toString() {
        return "下载记录ID:" + (downloadId == null ? "空" : downloadId) +
                ", 分享ID:" + (shareId == null ? "空" : shareId) +
                ", 文件ID:" + (fileId == null ? "空" : fileId) +
                ", 下载用户ID:" + (userId == null ? "空" : userId) +
                ", 下载时间:" + (downloadTime == null ? "空" : downloadTime);
    }
}
