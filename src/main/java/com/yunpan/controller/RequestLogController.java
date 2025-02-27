package com.yunpan.controller;

import com.yunpan.entity.po.RequestLog;
import com.yunpan.entity.query.RequestLogQuery;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.service.RequestLogService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 请求日志信息表ServiceImpl
 * @auther: lnorly
 * @Date: 2025/02/27
 */
@RestController()
@RequestMapping("/requestLog")
public class RequestLogController extends ABaseController {

	@Resource
	private RequestLogService requestLogService;

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(RequestLogQuery query) {
		return getSuccessResponseVO(requestLogService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(RequestLog bean) {
		requestLogService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<RequestLog> listBean) {
		requestLogService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<RequestLog> listBean) {
		requestLogService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id查询
	 */
	@RequestMapping("/getRequestLogById")
	public ResponseVO getRequestLogById(Long id) {
		return getSuccessResponseVO(this.requestLogService.getRequestLogById(id));
	}

	/**
	 * 根据Id更新
	 */
	@RequestMapping("/updateRequestLogById")
	public ResponseVO updateRequestLogById(RequestLog bean, Long id) {
		this.requestLogService.updateRequestLogById(bean, id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id删除
	 */
	@RequestMapping("/deleteRequestLogById")
	public ResponseVO deleteRequestLogById(String ids) {
		String[] idArray = ids.split(",");
		for (String id: idArray) {
			this.requestLogService.deleteRequestLogById(Long.parseLong(id));
		}
		return getSuccessResponseVO("删除成功");
	}

	@RequestMapping("/deleteRequestLogByQuery")
	public ResponseVO deleteRequestLogByQuery(RequestLogQuery query) {
		this.requestLogService.deleteRequestLogByQuery(query);
		return getSuccessResponseVO("删除成功");
	}
}
