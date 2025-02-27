package com.yunpan.aspect;

import com.yunpan.entity.po.RequestLog;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestLogRepository {
    void save(RequestLog requestLog);
}
