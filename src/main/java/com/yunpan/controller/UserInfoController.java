package com.yunpan.controller;

import com.yunpan.annotation.GlobalInterceptor;
import com.yunpan.annotation.VerifyParam;
import com.yunpan.component.RedisComponent;
import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.config.Appconfig;
import com.yunpan.entity.dto.CreateImageCode;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.dto.UserSpaceDto;
import com.yunpan.entity.po.UserInfo;
import com.yunpan.enums.ResponseCodeEnum;
import com.yunpan.enums.VerifyRegexEnum;
import com.yunpan.exception.BusinessException;
import com.yunpan.service.EmailCodeService;
import com.yunpan.service.UserInfoService;
import com.yunpan.utils.StringTools;
import com.yunpan.entity.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: ServiceImpl
 * @auther: lnorly
 * @Date: 2024/09/09
 */
@RestController("userInfoController")
public class UserInfoController extends ABaseController {

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

	public static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

	@Resource
	private UserInfoService userInfoService;

	@Resource
	private EmailCodeService emailCodeService;

	@Resource
	private Appconfig appconfig;

	@Resource
	private RedisComponent redisComponent;

	@RequestMapping("/checkCode")
	public void checkEmailCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
		CreateImageCode vCode = new CreateImageCode(130, 38, 28, 5, 10);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		String code = vCode.getCode();
		if (type == null || type == 0) {
			session.setAttribute(Constants.CHECK_CODE_KEY, code);
		} else {
			session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
		}
		vCode.write(response.getOutputStream());
	}


	@RequestMapping("/sendEmailCode")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO sendEmailCode(HttpSession session, @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
									@VerifyParam(required = true) String checkCode,
									@VerifyParam(required = true) Integer type) {
		try {
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
				throw new BusinessException("图片验证码不正确");
			}
			emailCodeService.sendEmailCode(email, type);
			return getSuccessResponseVO(null);
		} finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
		}

	}

	@RequestMapping("/register")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO register(HttpSession session,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
							   @VerifyParam(required = true) String nickName,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
							   @VerifyParam(required = true) String checkCode,
							   @VerifyParam(required = true) String emailCode) {
		try {
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
				throw new BusinessException("图片验证码不正确");
			}
			userInfoService.register(email, nickName, password, emailCode);
			return getSuccessResponseVO(null);
		} finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}

	}

	@RequestMapping("/login")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO login(HttpSession session,
							   @VerifyParam(required = true) String email,
							   @VerifyParam(required = true) String password,
							   @VerifyParam(required = true) String checkCode) {
		try {
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
				throw new BusinessException("图片验证码不正确");
			}
			SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
			session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
			return getSuccessResponseVO(sessionWebUserDto);
		} finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}

	}

	@RequestMapping("/resetPwd")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO resetPwd(HttpSession session,
							@VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
							@VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
							@VerifyParam(required = true) String checkCode,
							@VerifyParam(required = true) String emailCode) {
		try {
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
				throw new BusinessException("图片验证码不正确");
			}
			userInfoService.resetPassword(email, password, emailCode);
			return getSuccessResponseVO(null);
		} finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}

	}

	@RequestMapping("/getAvatar/{userId}")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public void getAvatar(HttpServletResponse response,
							   @VerifyParam(required = true) @PathVariable("userId") String userId) {
			String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
			String avatarPath = appconfig.getProjectFloder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
			File folder = new File(appconfig.getProjectFloder() + avatarFolderName);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File file = new File(avatarPath);
			if (!file.exists()) {
				if (!new File(appconfig.getProjectFloder() + avatarFolderName + Constants.AVATAR_DEFAULT).exists()) {
					printNoDefaultImage(response);
					return;
				}
				avatarPath = appconfig.getProjectFloder() + avatarFolderName + Constants.AVATAR_DEFAULT;
			}
			response.setContentType("image/jpg");
			readFile(response, avatarPath);
	}

	public void printNoDefaultImage(HttpServletResponse response) {
		response.setContentType(CONTENT_TYPE);
		response.setStatus(HttpStatus.OK.value());
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write("没有默认图片");
		} catch (IOException e) {
			logger.error("输出无默认图, 失败", e);
		} finally {
			if (pw!= null) {
				pw.close();
			}
		}
	}

	@RequestMapping("/getUserInfo")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO getUserInfo(HttpSession session) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		return getSuccessResponseVO(sessionWebUserDto);
	}

	@RequestMapping("/getUseSpace")
	@GlobalInterceptor
	public ResponseVO getUseSpace(HttpSession session) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		UserSpaceDto userSpaceDto = redisComponent.getUserSpace(sessionWebUserDto.getUserId());

		return getSuccessResponseVO(userSpaceDto);
	}

	@RequestMapping("/logout")
	public ResponseVO logout(HttpSession session) {
		session.invalidate();
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/updateUserAvatar")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String baseFolder = appconfig.getProjectFloder() + Constants.FILE_FOLDER_FILE;
		File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
		File targetFile = new File(targetFileFolder.getPath() + "/" + sessionWebUserDto.getUserId() + Constants.AVATAR_SUFFIX);
		if (!targetFileFolder.exists()) {
			targetFileFolder.mkdirs();
		}
		try {
			avatar.transferTo(targetFile);
		} catch (IOException e) {
			logger.error("上传头像失败", e);
			throw new BusinessException(ResponseCodeEnum.CODE_500);
		}

		UserInfo userInfo = new UserInfo();
		userInfo.setQqAvatar("");
		userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());
		sessionWebUserDto.setAvatar(null);
		session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/updatePassword")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO updatePassword(HttpSession session,
									 @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		UserInfo userInfo = new UserInfo();
		userInfo.setPassword(StringTools.encodeByMD5(password));
		userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());
		return getSuccessResponseVO(null);
	}

	@RequestMapping("qqlogin")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO qqlogin(HttpSession session, String callbackUrl) {
		String state = StringTools.getRandomNumber(Constants.LEN_30);
		if (!StringTools.isEmpty(state)) {
			session.setAttribute(state, callbackUrl);
		}
		String url = String.format(appconfig.getQqUrlAuthorization(),
				appconfig.getQqAppId(), URLEncoder.encode(appconfig.getQqUrlRedirect()), "utf-8");
		return getSuccessResponseVO(url);
	}

	@RequestMapping("qqlogin/callback")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO qqloginCallback(HttpSession session, @VerifyParam(required = true) String code, String state) {

		SessionWebUserDto sessionWebUserDto = userInfoService.qqlogin(code);
		session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
		Map<String, Object> result = new HashMap<>();
		result.put("callbackUrl", session.getAttribute(state));
		result.put("userInfo", sessionWebUserDto);

		return getSuccessResponseVO(result);
	}
}