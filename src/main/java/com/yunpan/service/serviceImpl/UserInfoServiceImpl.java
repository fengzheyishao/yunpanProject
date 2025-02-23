package com.yunpan.service.serviceImpl;

import com.yunpan.component.RedisComponent;
import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.config.Appconfig;
import com.yunpan.entity.dto.QQInfoDto;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.dto.SysSettingsDto;
import com.yunpan.entity.dto.UserSpaceDto;
import com.yunpan.entity.po.FileInfo;
import com.yunpan.entity.po.UserInfo;
import com.yunpan.entity.po.UserLoginInfo;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.query.SimplePage;
import com.yunpan.entity.query.UserInfoQuery;
import com.yunpan.entity.query.UserLoginInfoQuery;
import com.yunpan.enums.PageSize;
import com.yunpan.enums.UserStatusEnum;
import com.yunpan.exception.BusinessException;
import com.yunpan.mappers.FileInfoMapper;
import com.yunpan.mappers.UserInfoMapper;
import com.yunpan.mappers.UserLoginInfoMapper;
import com.yunpan.service.EmailCodeService;
import com.yunpan.service.UserInfoService;
import com.yunpan.utils.JsonUtils;
import com.yunpan.utils.OKHttpUtils;
import com.yunpan.utils.StringTools;
import com.yunpan.entity.vo.PaginationResultVO;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: ServiceImpl
 * @auther: lnorly
 * @Date: 2024/09/09
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	public static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
	@Resource
	private UserLoginInfoMapper<UserLoginInfo, UserLoginInfoQuery> userLoginInfoMapper;
	@Resource
	private EmailCodeService emailCodeService;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private Appconfig appconfig;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();

		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据UserId查询
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId更新
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email查询
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email更新
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 根据QqOpenId查询
	 */
	@Override
	public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
		return this.userInfoMapper.selectByQqOpenId(qqOpenId);
	}

	/**
	 * 根据QqOpenId更新
	 */
	@Override
	public Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId) {
		return this.userInfoMapper.updateByQqOpenId(bean, qqOpenId);
	}

	/**
	 * 根据QqOpenId删除
	 */
	@Override
	public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
		return this.userInfoMapper.deleteByQqOpenId(qqOpenId);
	}

	/**
	 * 根据NickName查询
	 */
	@Override
	public UserInfo getUserInfoByNickName(String nickName) {
		return this.userInfoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName更新
	 */
	@Override
	public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
		return this.userInfoMapper.updateByNickName(bean, nickName);
	}

	/**
	 * 根据NickName删除
	 */
	@Override
	public Integer deleteUserInfoByNickName(String nickName) {
		return this.userInfoMapper.deleteByNickName(nickName);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(String email, String nickName, String password, String emailCode) {
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo != null) {
			throw new BusinessException("邮箱账号已经存在");
		}
		UserInfo userInfoUser = userInfoMapper.selectByNickName(nickName);
		if (userInfoUser != null) {
			throw new BusinessException("用户名已经存在");
		}

		//校验邮箱验证码
		emailCodeService.checkCode(email, emailCode);

		String userId = StringTools.getRandomNumber(Constants.LEN_10);
		userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setNickName(nickName);
		userInfo.setEmail(email);
		userInfo.setPassword(StringTools.encodeByMD5(password));
		userInfo.setJoinTime(new Date());
		userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
		SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
		userInfo.setTotalSpace(sysSettingsDto.getUserInitUseSpace()*Constants.MB);
		userInfo.setUseSpace(0L);
		this.userInfoMapper.insert(userInfo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public SessionWebUserDto login(String email, String password) {
		Date date = new Date();
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo == null || !userInfo.getPassword().equals(password)) {
			throw new BusinessException("账号或密码错误");
		}
		if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
			throw new BusinessException("账号已被停用");
		}
		UserInfo updateInfo = new UserInfo();
		updateInfo.setLastLoginTime(date);

		this.userInfoMapper.updateByUserId(updateInfo, userInfo.getUserId());


		// 登录信息
		UserLoginInfo userLoginInfo = this.userLoginInfoMapper.selectByUserIdAndLoginDate(userInfo.getUserId(), date);

		if (userLoginInfo == null) {
			userLoginInfo = new UserLoginInfo();
			userLoginInfo.setLoginLastDate(date);
			userLoginInfo.setLoginDate(date);
			userLoginInfo.setUserId(userInfo.getUserId());
			userLoginInfo.setLoginCount(1);
			this.userLoginInfoMapper.insert(userLoginInfo);
		} else {
			userLoginInfo.setLoginLastDate(date);
			userLoginInfo.setLoginCount(userLoginInfo.getLoginCount()+1);
			this.userLoginInfoMapper.updateById(userLoginInfo, userLoginInfo.getId());
		}


		SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
		sessionWebUserDto.setNickName(userInfo.getNickName());
		sessionWebUserDto.setUserId(userInfo.getUserId());
		sessionWebUserDto.setJoinTime(userInfo.getJoinTime());
		sessionWebUserDto.setLastLoginTime(userInfo.getLastLoginTime());

		if (ArrayUtils.contains(appconfig.getAdminEmail().split(","), email)) {
			sessionWebUserDto.setIsAdmin(true);
		} else {
			sessionWebUserDto.setIsAdmin(false);
		}
		UserSpaceDto userSpaceDto = new UserSpaceDto();
//		userSpaceDto.setUseSpace();
		Long useSpace = fileInfoMapper.selectUseSpace(userInfo.getUserId());
		userSpaceDto.setUseSpace(useSpace);
		userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
		redisComponent.saveUserSpace(userInfo.getUserId(), userSpaceDto);
		return sessionWebUserDto;
	}

	@Override
	public void resetPassword(String email, String password, String emailCode) {
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo == null) {
			throw new BusinessException("账号不存在");
		}
		emailCodeService.checkCode(email, emailCode);
		UserInfo updateInfo = new UserInfo();
		updateInfo.setPassword(password);
		this.userInfoMapper.updateByEmail(updateInfo, email);
	}

	@Override
	public SessionWebUserDto qqlogin(String code) {
	    String accessToken = getQQAccessToken(code);
		String qqOpenId = getQQOpenId(accessToken);
		UserInfo userInfo = userInfoMapper.selectByQqOpenId(qqOpenId);
        String avatar = null;
		if(userInfo == null) {
			QQInfoDto qqInfo = getQQUserInfo(accessToken, qqOpenId);
			userInfo = new UserInfo();

			String nickName = qqInfo.getNickname();
			nickName = nickName.length() > Constants.LEN_150 ? nickName.substring(0, 150) : nickName;
			avatar = StringTools.isEmpty(qqInfo.getFigureurl_qq_2()) ? qqInfo.getFigureurl_qq_1() : qqInfo.getFigureurl_qq_2();
			Date curDate = new Date();

			userInfo.setQqOpenId(qqOpenId);
			userInfo.setJoinTime(curDate);
			userInfo.setNickName(nickName);
			userInfo.setQqAvatar(avatar);
			userInfo.setUserId(StringTools.getRandomString(Constants.LEN_10));
			userInfo.setLastLoginTime(curDate);
			userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
			userInfo.setUseSpace(0L);
			userInfo.setTotalSpace(redisComponent.getSysSettingsDto().getUserInitUseSpace() * Constants.MB);
			this.userInfoMapper.insert(userInfo);
			userInfo = userInfoMapper.selectByQqOpenId(qqOpenId);
		} else {
			UserInfo updateInfo = new UserInfo();
			updateInfo.setLastLoginTime(new Date());
			avatar = userInfo.getQqAvatar();
			this.userInfoMapper.updateByQqOpenId(updateInfo, qqOpenId);
		}
		SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
		sessionWebUserDto.setNickName(userInfo.getNickName());
		sessionWebUserDto.setAvatar(avatar);
		sessionWebUserDto.setUserId(userInfo.getUserId());
		if (ArrayUtils.contains(appconfig.getAdminEmail().split(","), userInfo.getEmail())) {
			sessionWebUserDto.setIsAdmin(true);
		} else {
			sessionWebUserDto.setIsAdmin(false);
		}

		UserSpaceDto userSpaceDto = new UserSpaceDto();
		Long useSpace = fileInfoMapper.selectUseSpace(userInfo.getUserId());
		userSpaceDto.setUseSpace(useSpace);
		userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
		redisComponent.saveUserSpace(userInfo.getUserId(), userSpaceDto);

		return sessionWebUserDto;
	}

	private String getQQAccessToken(String code) {
		String accessToken = null;
		String url = null;
		try {
			url = String.format(appconfig.getQqUrlAccessToken(), appconfig.getQqAppId(), appconfig.getQqAppKey(),
					code, URLEncoder.encode(appconfig.getQqUrlRedirect(), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("encode失败", e);
		}
		String tokenResult = OKHttpUtils.getRequest(url);

		if (tokenResult != null && tokenResult.indexOf(Constants.VIEW_OBJ_RESULT_KEY) != -1) {
			logger.error("获取qqToken失败:{}", tokenResult);
			throw new BusinessException("获取qqToken失败");
		}
		String[] params = tokenResult.split("&");
		if (params != null && params.length > 0) {
			for (String str : params) {
				if (str.indexOf("access_token") != -1) {
					accessToken = str.split("=")[1];
					break;
				}
			}
		}

		return accessToken;
	}

	private String getQQOpenId(String accessToken) throws BusinessException {
		String url = String.format(appconfig.getQqUrlOpenId(), accessToken);
		String openIdResult = OKHttpUtils.getRequest(url);
		String tempJson = this.getQQResp(openIdResult);

		if (tempJson == null) {
			logger.error("调qq接口获取openID失败:tmpJson{}", tempJson);
			throw new BusinessException("调qq接口获取openID失败");
		}

		Map jsonData = JsonUtils.convertJson2Obj(tempJson, Map.class);
		if (jsonData == null || jsonData.containsKey(Constants.VIEW_OBJ_RESULT_KEY)) {
			logger.error("调qq接口获取openID失败:{}", jsonData);
			throw new BusinessException("调qq接口获取openID失败");
		}
		return String.valueOf(jsonData.get("openid"));
	}

	private QQInfoDto getQQUserInfo(String accessToken, String qqOpenId) throws BusinessException {
		String url = String.format(appconfig.getQqUrlUserInfo(), accessToken, qqOpenId);
		String response = OKHttpUtils.getRequest(url);
		if (StringUtils.isNotBlank(response)) {
			QQInfoDto qqInfo = JsonUtils.convertJson2Obj(response, QQInfoDto.class);
            if (qqInfo.getRet() != 0) {
				logger.error("qqInfo:{}", response);
				throw new BusinessException("调qq接口获取用户信息异常");
			}
			return qqInfo;
		}
		throw new BusinessException("调qq接口获取用户信息异常");
	}

	private String getQQResp(String result){
		if (StringUtils.isNotBlank(result)) {
			int pos = result.indexOf("callback");
			if (pos != -1) {
				int start =result.indexOf("(");
				int end = result.indexOf(")");
				String jsonStr = result.substring(start + 1, end - 1);
				return jsonStr;
			}
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUserStatus(String userId, Integer status) {
		UserInfo userInfo = new UserInfo();
		userInfo.setStatus(status);
		if (UserStatusEnum.DISABLE.getStatus().equals(status)) {
			userInfo.setUseSpace(0L);
			fileInfoMapper.deleteFileByUserId(userId);
		}
		userInfoMapper.updateByUserId(userInfo, userId);
	}

	@Override
	public void changeUserSpace(String userId, Integer changeSpace) {
		Long space = changeSpace*Constants.MB;
		this.userInfoMapper.updateUserSpace(userId, null, space);
		redisComponent.resetUserSpaceUse(userId);

	}

	@Override
	public void resetNickname(String userId, String nickName) {
		UserInfo userInfo = this.userInfoMapper.selectByNickName(nickName);
		if (userInfo != null) {
			throw new BusinessException("用户名已经存在");
		}
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setNickName(nickName);
		this.userInfoMapper.updateByUserId(userInfo2, userId);
	}
}
