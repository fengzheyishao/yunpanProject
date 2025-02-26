package com.yunpan.service.serviceImpl;

import com.yunpan.component.RedisComponent;
import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.po.UserInfo;
import com.yunpan.entity.po.UserMemoryRequest;
import com.yunpan.entity.query.UserInfoQuery;
import com.yunpan.entity.query.UserMemoryRequestQuery;
import com.yunpan.entity.query.SimplePage;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.enums.MemoryRequestStatusEnum;
import com.yunpan.enums.ResponseCodeEnum;
import com.yunpan.exception.BusinessException;
import com.yunpan.mappers.UserInfoMapper;
import com.yunpan.service.UserMemoryRequestService;
import com.yunpan.mappers.UserMemoryRequestMapper;
import com.yunpan.enums.PageSize;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 用户内存申请信息表ServiceImpl
 * @auther: lnorly
 * @Date: 2025/02/24
 */
@Service("userMemoryRequestService")
public class UserMemoryRequestServiceImpl implements UserMemoryRequestService {

	@Resource
	private UserMemoryRequestMapper<UserMemoryRequest, UserMemoryRequestQuery> userMemoryRequestMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserMemoryRequest> findListByParam(UserMemoryRequestQuery query) {
		return this.userMemoryRequestMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserMemoryRequestQuery query) {
		return this.userMemoryRequestMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<UserMemoryRequest> findListByPage(UserMemoryRequestQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();

		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserMemoryRequest> list = this.findListByParam(query);
		PaginationResultVO<UserMemoryRequest> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserMemoryRequest bean) {
		return this.userMemoryRequestMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserMemoryRequest> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userMemoryRequestMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserMemoryRequest> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userMemoryRequestMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据Id查询
	 */
	@Override
	public UserMemoryRequest getUserMemoryRequestById(Long id) {
		return this.userMemoryRequestMapper.selectById(id);
	}

	/**
	 * 根据Id更新
	 */
	@Override
	public Integer updateUserMemoryRequestById(UserMemoryRequest bean, Long id) {
		return this.userMemoryRequestMapper.updateById(bean, id);
	}

	/**
	 * 根据Id删除
	 */
	@Override
	public Integer deleteUserMemoryRequestById(Long id) {
		return this.userMemoryRequestMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void adminUserMemoryApplyBatch(UserMemoryRequest bean, UserMemoryRequestQuery query) {
		List<UserMemoryRequest> list = this.userMemoryRequestMapper.selectList(query);
		if (bean.getStatus() == MemoryRequestStatusEnum.APPROVED.getCode()) {
			for (UserMemoryRequest item: list) {
				addUserSpace(item.getId());
			}
		}
		this.userMemoryRequestMapper.updateBatchByQuery(bean, query);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void adminUserMemoryApply(UserMemoryRequest bean, Long id) {
		if (bean.getStatus() == MemoryRequestStatusEnum.APPROVED.getCode()) {
			addUserSpace(id);
		}
		this.userMemoryRequestMapper.updateById(bean, id);
	}

	private void addUserSpace(Long id) {
		UserMemoryRequest userMemoryRequest = this.userMemoryRequestMapper.selectById(id);
		if (userMemoryRequest == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_500);
		}
		Long space = userMemoryRequest.getRequestSize() * Constants.MB;
		this.userInfoMapper.updateUserSpace(userMemoryRequest.getUserId(), null, space);
		redisComponent.resetUserSpaceUse(userMemoryRequest.getUserId());
	}
}
