package com.yunpan.entity.query;

import java.util.Date;

public class DownloadFileQuery extends BaseParam {
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
}
