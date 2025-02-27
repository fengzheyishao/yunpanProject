package com.yunpan.service.serviceImpl;

import com.yunpan.entity.po.RequestLog;
import com.yunpan.entity.query.RequestLogQuery;
import com.yunpan.entity.query.SimplePage;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.service.RequestLogService;
import com.yunpan.mappers.RequestLogMapper;
import com.yunpan.enums.PageSize;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Description: 请求日志信息表ServiceImpl
 * @auther: lnorly
 * @Date: 2025/02/27
 */
@Service("requestLogService")
public class RequestLogServiceImpl implements RequestLogService {

	@Resource
	private RequestLogMapper<RequestLog, RequestLogQuery> requestLogMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<RequestLog> findListByParam(RequestLogQuery query) {
		return this.requestLogMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(RequestLogQuery query) {
		return this.requestLogMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<RequestLog> findListByPage(RequestLogQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();

		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<RequestLog> list = this.findListByParam(query);
		PaginationResultVO<RequestLog> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(RequestLog bean) {
		return this.requestLogMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<RequestLog> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.requestLogMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<RequestLog> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.requestLogMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据Id查询
	 */
	@Override
	public RequestLog getRequestLogById(Long id) {
		return this.requestLogMapper.selectById(id);
	}

	/**
	 * 根据Id更新
	 */
	@Override
	public Integer updateRequestLogById(RequestLog bean, Long id) {
		return this.requestLogMapper.updateById(bean, id);
	}

	/**
	 * 根据Id删除
	 */
	@Override
	public Integer deleteRequestLogById(Long id) {
		return this.requestLogMapper.deleteById(id);
	}

	@Override
	public Integer deleteRequestLogByQuery(RequestLogQuery query) {
		return this.requestLogMapper.deleteByQuery(query);
	}

}
