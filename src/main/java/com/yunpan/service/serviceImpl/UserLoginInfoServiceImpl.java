package com.yunpan.service.serviceImpl;

import com.yunpan.entity.po.FileInfo;
import com.yunpan.entity.po.UserInfo;
import com.yunpan.entity.po.UserLoginInfo;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.query.UserInfoQuery;
import com.yunpan.entity.query.UserLoginInfoQuery;
import com.yunpan.entity.query.SimplePage;
import com.yunpan.mappers.FileInfoMapper;
import com.yunpan.service.UserLoginInfoService;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.mappers.UserLoginInfoMapper;
import com.yunpan.enums.PageSize;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户登录信息表ServiceImpl
 * @auther: lnorly
 * @Date: 2025/02/19
 */
@Service("userLoginInfoService")
public class UserLoginInfoServiceImpl implements UserLoginInfoService {

	@Resource
	private UserLoginInfoMapper<UserLoginInfo, UserLoginInfoQuery> userLoginInfoMapper;
	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

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

	@Override
	public Map<String, Long> selectMemoryAnalysisByUserId(String userId) {
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setUserId(userId);
		List<FileInfo> list = this.fileInfoMapper.selectList(fileInfoQuery);
		// 1:视频 2:音频 3:图片 4:文档 5:其他
		long[] l = new long[6];
		for (FileInfo fileInfo: list) {
			if (fileInfo.getFileCategory() != null) {
				l[fileInfo.getFileCategory()] = l[fileInfo.getFileCategory()] + fileInfo.getFileSize();
			}
		}
		Map<String, Long> map = new HashMap<>();
		map.put("视频", l[1]);
		map.put("音频", l[2]);
		map.put("图片", l[3]);
		map.put("文档", l[4]);
		map.put("其他", l[5]);
		return map;
	}


}
