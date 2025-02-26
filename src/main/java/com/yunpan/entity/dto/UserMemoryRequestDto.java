package com.yunpan.entity.dto;

import com.yunpan.entity.query.UserMemoryRequestQuery;

public class UserMemoryRequestDto {
    private Long[] userIds;
    private Integer status;
    private String rejectionReason;

    private UserMemoryRequestQuery query;

    // Getters and Setters
    public Long[] getUserIds() {
        return userIds;
    }

    public void setUserIds(Long[] userIds) {
        this.userIds = userIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public UserMemoryRequestQuery getQuery() {
        return query;
    }

    public void setQuery(UserMemoryRequestQuery query) {
        this.query = query;
    }
}
