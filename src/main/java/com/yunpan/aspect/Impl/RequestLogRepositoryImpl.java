package com.yunpan.aspect.Impl;

import com.yunpan.aspect.RequestLogRepository;
import com.yunpan.entity.po.RequestLog;
import com.yunpan.entity.query.RequestLogQuery;
import com.yunpan.mappers.RequestLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class RequestLogRepositoryImpl implements RequestLogRepository {
    @Resource
    private RequestLogMapper<RequestLog, RequestLogQuery> requestLogMapper;

    @Override
    public void save(RequestLog requestLog) {
        requestLogMapper.insert(requestLog);
    }
}
