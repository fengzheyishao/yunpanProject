package com.yunpan.service.serviceImpl;

import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.dto.SessionShareDto;
import com.yunpan.entity.po.FileShare;
import com.yunpan.entity.query.FileShareQuery;
import com.yunpan.entity.query.SimplePage;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.enums.PageSize;
import com.yunpan.enums.ResponseCodeEnum;
import com.yunpan.enums.ShareValidTypeEnums;
import com.yunpan.exception.BusinessException;
import com.yunpan.mappers.FileShareMapper;
import com.yunpan.service.FileShareService;
import com.yunpan.utils.DateUtils;
import com.yunpan.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: ServiceImpl
 * @auther: lnorly
 * @Date: 2024/09/23
 */
@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService {

	@Resource
	private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<FileShare> findListByParam(FileShareQuery query) {
		return this.fileShareMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(FileShareQuery query) {
		return this.fileShareMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<FileShare> findListByPage(FileShareQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();

		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<FileShare> list = this.findListByParam(query);
		PaginationResultVO<FileShare> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(FileShare bean) {
		return this.fileShareMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<FileShare> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileShareMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<FileShare> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileShareMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据ShareId查询
	 */
	@Override
	public FileShare getFileShareByShareId(String shareId) {
		return this.fileShareMapper.selectByShareId(shareId);
	}

	/**
	 * 根据ShareId更新
	 */
	@Override
	public Integer updateFileShareByShareId(FileShare bean, String shareId) {
		return this.fileShareMapper.updateByShareId(bean, shareId);
	}

	/**
	 * 根据ShareId删除
	 */
	@Override
	public Integer deleteFileShareByShareId(String shareId) {
		return this.fileShareMapper.deleteByShareId(shareId);
	}

	@Override
	public void saveShare(FileShare fileShare) {
		ShareValidTypeEnums typeEnums = ShareValidTypeEnums.getByType(fileShare.getValidType());
		if (typeEnums == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (ShareValidTypeEnums.FOREVER != typeEnums) {
			fileShare.setExpireTime(DateUtils.getAfterDate(typeEnums.getDays()));
		}
		Date date = new Date();
		fileShare.setShareTime(date);
		if (StringTools.isEmpty(fileShare.getCode())) {
			fileShare.setCode(StringTools.getRandomString(Constants.LEN_5));
		}
		fileShare.setShareId(StringTools.getRandomString(Constants.LEN_20));
		fileShare.setShowCount(0);
		this.fileShareMapper.insert(fileShare);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteFileShareBatch(String[] shareIdArrays, String userId) {
		Integer count = this.fileShareMapper.deleteFileShareBatch(shareIdArrays, userId);
		if (count != shareIdArrays.length) {
			throw new BusinessException(ResponseCodeEnum.CODE_601);
		}
	}

	@Override
	public SessionShareDto checkShareCode(String shareId, String code) {
		FileShare fileShare = this.fileShareMapper.selectByShareId(shareId);
		if (fileShare == null || (fileShare.getExpireTime() != null && new Date().after(fileShare.getExpireTime()))) {
			throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
		}
		if (!fileShare.getCode().equals(code)) {
			throw new BusinessException("提取码错误");
		}
		//更新次数
		this.fileShareMapper.updateShareShowCount(shareId);
		SessionShareDto sessionShareDto = new SessionShareDto();
		sessionShareDto.setShareId(shareId);
		sessionShareDto.setShareUserId(fileShare.getUserId());
		sessionShareDto.setFileId(fileShare.getFileId());
		sessionShareDto.setExpireTime(fileShare.getExpireTime());
		return sessionShareDto;
	}


}