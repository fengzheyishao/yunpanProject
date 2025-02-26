package com.yunpan.controller;

import com.yunpan.entity.po.EmailCode;
import com.yunpan.entity.query.EmailCodeQuery;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.service.EmailCodeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 邮箱验证ServiceImpl
 * @auther: lnorly
 * @Date: 2024/09/09
 */
@RestController()
@RequestMapping("/emailCode")
public class EmailCodeController extends ABaseController {

	@Resource
	private EmailCodeService emailCodeService;

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(EmailCodeQuery query) {
		return getSuccessResponseVO(emailCodeService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(EmailCode bean) {
		emailCodeService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<EmailCode> listBean) {
		emailCodeService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<EmailCode> listBean) {
		emailCodeService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}


}
