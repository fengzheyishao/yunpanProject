package com.yunpan.service.serviceImpl;

import com.yunpan.entity.po.UserLoginInfo;
import com.yunpan.entity.query.UserLoginInfoQuery;
import com.yunpan.entity.query.SimplePage;
import com.yunpan.service.UserLoginInfoService;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.mappers.UserLoginInfoMapper;
import com.yunpan.enums.PageSize;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description: 用户登录信息表ServiceImpl
 * @auther: lnorly
 * @Date: 2025/02/19
 */
@Service("userLoginInfoService")
public class UserLoginInfoServiceImpl implements UserLoginInfoService {

	@Resource
	private UserLoginInfoMapper<UserLoginInfo, UserLoginInfoQuery> userLoginInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserLoginInfo> findListByParam(UserLoginInfoQuery query) {
		return this.userLoginInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserLoginInfoQuery query) {
		return this.userLoginInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<UserLoginInfo> findListByPage(UserLoginInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();

		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserLoginInfo> list = this.findListByParam(query);
		PaginationResultVO<UserLoginInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserLoginInfo bean) {
		return this.userLoginInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserLoginInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userLoginInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserLoginInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userLoginInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据Id查询
	 */
	@Override
	public UserLoginInfo getUserLoginInfoById(Long id) {
		return this.userLoginInfoMapper.selectById(id);
	}

	/**
	 * 根据Id更新
	 */
	@Override
	public Integer updateUserLoginInfoById(UserLoginInfo bean, Long id) {
		return this.userLoginInfoMapper.updateById(bean, id);
	}

	/**
	 * 根据Id删除
	 */
	@Override
	public Integer deleteUserLoginInfoById(Long id) {
		return this.userLoginInfoMapper.deleteById(id);
	}

	/**
	 * 根据UserIdAndLoginDate查询
	 */
	@Override
	public UserLoginInfo getUserLoginInfoByUserIdAndLoginDate(String userId, Date loginDate) {
		return this.userLoginInfoMapper.selectByUserIdAndLoginDate(userId, loginDate);
	}

	/**
	 * 根据UserIdAndLoginDate更新
	 */
	@Override
	public Integer updateUserLoginInfoByUserIdAndLoginDate(UserLoginInfo bean, String userId, Date loginDate) {
		return this.userLoginInfoMapper.updateByUserIdAndLoginDate(bean, userId, loginDate);
	}

	/**
	 * 根据UserIdAndLoginDate删除
	 */
	@Override
	public Integer deleteUserLoginInfoByUserIdAndLoginDate(String userId, Date loginDate) {
		return this.userLoginInfoMapper.deleteByUserIdAndLoginDate(userId, loginDate);
	}


}
