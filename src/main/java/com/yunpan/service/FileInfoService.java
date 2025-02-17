package com.yunpan.service;

import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.dto.UploadResultDto;
import com.yunpan.entity.po.FileInfo;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
/**
 * @Description: Service
 * @auther: lnorly
 * @Date: 2024/09/11
 */
public interface FileInfoService {
	/**
	 * 根据条件查询列表
	 */
	List<FileInfo> findListByParam(FileInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(FileInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<FileInfo> findListByPage(FileInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(FileInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<FileInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<FileInfo> listBean);

	/**
	 * 根据FileIdAndUserId查询
	 */
	FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);

	/**
	 * 根据FileIdAndUserId更新
	 */
	Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId);

	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId);

	UploadResultDto uploadFile(SessionWebUserDto sessionWebUserDto, String fillId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

	FileInfo newFolder(String filePid, String userId, String fileName, Integer folderType);

	FileInfo rename(String fileId, String userId, String fileName);

	void changeFileFolder(String fileIds, String filePid, String userId);

	void removeFile2RecycleBatch(String userId, String fileIds);

	void recoverFile2RecycleBatch(String userId, String fileIds);

	void deleteFileBatch(String userId, String fileIds, Boolean isAdmin);

	void checkFootFilePid(String rootFilePid, String userId, String fileId);

	void saveShare(String shareRootFilePid, String shareFileIds, String myFolderId, String shareUserId,
				   String currentUserId);
}