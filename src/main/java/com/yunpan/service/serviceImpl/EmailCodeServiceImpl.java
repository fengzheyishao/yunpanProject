package com.yunpan.service.serviceImpl;

import com.yunpan.component.RedisComponent;
import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.config.Appconfig;
import com.yunpan.entity.dto.SysSettingsDto;
import com.yunpan.entity.po.EmailCode;
import com.yunpan.entity.po.UserInfo;
import com.yunpan.entity.query.EmailCodeQuery;
import com.yunpan.entity.query.SimplePage;
import com.yunpan.entity.query.UserInfoQuery;
import com.yunpan.enums.PageSize;
import com.yunpan.exception.BusinessException;
import com.yunpan.mappers.EmailCodeMapper;
import com.yunpan.mappers.UserInfoMapper;
import com.yunpan.service.EmailCodeService;
import com.yunpan.utils.StringTools;
import com.yunpan.entity.vo.PaginationResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;

/**
 * @Description: 邮箱验证ServiceImpl
 * @auther: lnorly
 * @Date: 2024/09/09
 */
@Service("emailCodeService")
public class EmailCodeServiceImpl implements EmailCodeService {
	private static Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);

	@Resource
	private EmailCodeMapper<EmailCode, EmailCodeQuery> emailCodeMapper;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private JavaMailSender javamailSender;
	@Resource
	private Appconfig appconfig;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<EmailCode> findListByParam(EmailCodeQuery query) {
		return this.emailCodeMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(EmailCodeQuery query) {
		return this.emailCodeMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();

		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<EmailCode> list = this.findListByParam(query);
		PaginationResultVO<EmailCode> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(EmailCode bean) {
		return this.emailCodeMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<EmailCode> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.emailCodeMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<EmailCode> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.emailCodeMapper.insertOrUpdateBatch(listBean);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sendEmailCode(String email, Integer type) {
		if (type == Constants.ZERO) {
			UserInfo userInfo = userInfoMapper.selectByEmail(email);
			if (userInfo != null) {
				throw new RuntimeException("邮箱已经存在");
			}
		}

		String code = StringTools.getRandomNumber(Constants.LEN_5);

		emailCodeMapper.disableEmailCode(email);

		EmailCode emailCode = new EmailCode();
		emailCode.setCode(code);
		emailCode.setEmail(email);
		emailCode.setStatus(Constants.ZERO);
		emailCode.setCreateTime(new Date());
		emailCodeMapper.insert(emailCode);

		sendEmailCode(email, code);
	}

	public void sendEmailCode(String email, String code) {
		try {
			MimeMessage message = javamailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(appconfig.getSendUserName());
			helper.setTo(email);

			SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();


			helper.setSubject(sysSettingsDto.getRegisterEmailTitle());
			helper.setText(String.format(sysSettingsDto.getRegisterEmailContent(), code));
			helper.setSentDate(new Date());
			javamailSender.send(message);

//			logger.info("发送邮件成功");
		} catch (Exception e) {
			logger.error("发送邮件失败", e);
			throw new BusinessException("发送邮件失败");
		}
	}

	@Override
	public void checkCode(String email, String code) {
		EmailCode emailCode = emailCodeMapper.selectByEmailAndCode(email, code);
		if (emailCode == null) {
			throw new BusinessException("邮箱验证码不正确");
		}
		if (emailCode.getStatus() == Constants.ONE || System.currentTimeMillis() - emailCode.getCreateTime().getTime() > Constants.LEN_15 * 1000 * 60) {
			throw new BusinessException("邮箱验证码已失效");
		}
		emailCodeMapper.disableEmailCode(email);
	}


}