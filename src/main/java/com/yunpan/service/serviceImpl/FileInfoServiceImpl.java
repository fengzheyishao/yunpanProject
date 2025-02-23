package com.yunpan.service.serviceImpl;

import com.yunpan.component.RedisComponent;
import com.yunpan.entity.config.Appconfig;
import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.dto.FileTipDto;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.dto.UploadResultDto;
import com.yunpan.entity.dto.UserSpaceDto;
import com.yunpan.entity.po.DownloadFile;
import com.yunpan.entity.po.FileInfo;
import com.yunpan.entity.po.FileShare;
import com.yunpan.entity.po.UserInfo;
import com.yunpan.entity.query.*;
import com.yunpan.entity.vo.FileTipVO;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.enums.*;
import com.yunpan.exception.BusinessException;
import com.yunpan.mappers.DownloadFileMapper;
import com.yunpan.mappers.FileInfoMapper;
import com.yunpan.mappers.FileShareMapper;
import com.yunpan.mappers.UserInfoMapper;
import com.yunpan.service.FileInfoService;
import com.yunpan.utils.DateUtils;
import com.yunpan.utils.ProcessUtils;
import com.yunpan.utils.ScaleFilter;
import com.yunpan.utils.StringTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: ServiceImpl
 * @auther: lnorly
 * @Date: 2024/09/11
 */
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {
	private static Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);
	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private DownloadFileMapper<DownloadFile, DownloadFileQuery> downloadFileMapper;
	@Resource
	private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private Appconfig appconfig;
	@Resource
	@Lazy
	private FileInfoServiceImpl fileInfoService;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<FileInfo> findListByParam(FileInfoQuery query) {
		return this.fileInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(FileInfoQuery query) {
		return this.fileInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<FileInfo> findListByPage(FileInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();

		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<FileInfo> list = this.findListByParam(query);
		PaginationResultVO<FileInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(FileInfo bean) {
		return this.fileInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<FileInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<FileInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据FileIdAndUserId查询
	 */
	@Override
	public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
		return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId更新
	 */
	@Override
	public Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId) {
		return this.fileInfoMapper.updateByFileIdAndUserId(bean, fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId删除
	 */
	@Override
	public Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId) {
		return this.fileInfoMapper.deleteByFileIdAndUserId(fileId, userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public UploadResultDto uploadFile(SessionWebUserDto sessionWebUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
		UploadResultDto uploadResultDto = new UploadResultDto();
		File tempFileFolder = null;
		Boolean uploadSucces = true;
		try {
			Date curDate = new Date();
			if (StringUtils.isEmpty(fileId)) {
				fileId = StringTools.getRandomString(Constants.LEN_10);
			}
			uploadResultDto.setFileId(fileId);
			String userId = sessionWebUserDto.getUserId();
			UserSpaceDto userSpaceDto = redisComponent.getUserSpace(userId);
			if (chunkIndex == 0) {
				FileInfoQuery fileInfoQuery = new FileInfoQuery();
				fileInfoQuery.setFileMd5(fileMd5);
				fileInfoQuery.setSimplePage(new SimplePage(0, 1));
				fileInfoQuery.setStatus(FileStatusEnums.USING.getStatus());
				List<FileInfo> fileList = this.fileInfoMapper.selectList(fileInfoQuery);
				if (!fileList.isEmpty()) {
					FileInfo fileInfo = fileList.get(0);
					if (userSpaceDto.getUseSpace() + file.getSize() > userSpaceDto.getTotalSpace()) {
						throw new BusinessException(ResponseCodeEnum.CODE_904);
					}
					uploadResultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
					//TODO 填fileinfo
					fileInfo.setFileId(fileId);
					fileInfo.setFilePid(filePid);
					fileInfo.setUserId(sessionWebUserDto.getUserId());
					fileInfo.setFileMd5(fileMd5);
					fileInfo.setFileName(autoRename(filePid, userId, fileName));
					fileInfo.setCreateTime(curDate);
					fileInfo.setLastUpdateTime(curDate);
					fileInfo.setFileName(fileName);
					fileInfo.setStatus(FileStatusEnums.USING.getStatus());
					fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
					this.fileInfoMapper.insert(fileInfo);
					uploadResultDto.setFileId(fileId);
					uploadResultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
					updateUserSpace(sessionWebUserDto, file.getSize());
					return uploadResultDto;
				}
			}
			String temp = appconfig.getProjectFloder() + Constants.FILE_FOLER_TEMP_NAME;
			String currentUserFolderName = userId + fileId;
			tempFileFolder = new File(temp + currentUserFolderName);

			if (!tempFileFolder.exists()) {
				tempFileFolder.mkdirs();
			}
			Long currentSpaceSize = redisComponent.getFileTempSize(userId, fileId);
			if (currentSpaceSize + file.getSize() + userSpaceDto.getUseSpace() > userSpaceDto.getTotalSpace()) {
				throw new BusinessException(ResponseCodeEnum.CODE_904);
			}

			File tempFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
			//存文件
			file.transferTo(tempFile);

			redisComponent.saveFileTempSize(userId, fileId, file.getSize());
			if (chunkIndex < chunks-1) {
				uploadResultDto.setStatus(UploadStatusEnums.UPLOADING.getCode());
				return uploadResultDto;
			}

			String month = DateUtils.format(curDate, DateTimePatternEnum.YYYYMM.getPattern());
			String fileSuffixName = StringTools.getFileNameSuffix(fileName);

			String realFileName = currentUserFolderName + fileSuffixName;
			FileTypeEnums fileTypeEnum = FileTypeEnums.getFileTypeBySuffix(fileSuffixName);

			fileName = autoRename(filePid, userId, fileName);

			FileInfo fileInfo = new FileInfo();

			fileInfo.setFilePid(filePid);
			fileInfo.setFileId(fileId);
			fileInfo.setFileCategory(fileTypeEnum.getCategory().getCategory());
			fileInfo.setFilePath(month + "/" + realFileName);
			fileInfo.setUserId(userId);
			fileInfo.setFileName(fileName);
			fileInfo.setFileMd5(fileMd5);
			fileInfo.setCreateTime(curDate);
			fileInfo.setLastUpdateTime(curDate);
			fileInfo.setFileType(fileTypeEnum.getType());
			fileInfo.setStatus(FileStatusEnums.TRANSFER.getStatus());
			fileInfo.setFolderType(FileFolderTypeEnums.FILE.getType());
			fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());

			this.fileInfoMapper.insert(fileInfo);

			Long totalSize =redisComponent.getFileTempSize(userId, fileId);
			updateUserSpace(sessionWebUserDto, totalSize);

			uploadResultDto.setStatus(UploadStatusEnums.UPLOAD_FINISH.getCode());
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					fileInfoService.transferFile(fileInfo.getFileId(), sessionWebUserDto);
				}
			});

			return uploadResultDto;
		} catch (BusinessException e) {
			uploadSucces = false;
			logger.error("上传文件失败", e);
			throw e;
		} catch (Exception e) {
			uploadSucces = false;
			logger.error("上传文件失败", e);
			throw new BusinessException("文件上传失败");
		} finally {
			try {
				if (tempFileFolder != null && !uploadSucces) {
					FileUtils.deleteDirectory(tempFileFolder);
				}
			} catch (IOException e) {
				logger.error("删除临时目录失败");
			}
		}
	}

	private void updateUserSpace(SessionWebUserDto sessionWebUserDto, Long fileSize) {
		Integer count = userInfoMapper.updateUserSpace(sessionWebUserDto.getUserId(), fileSize, null);
		if (count == 0) {
			throw new BusinessException(ResponseCodeEnum.CODE_904);
		}
		UserSpaceDto userSpaceDto = redisComponent.getUserSpace(sessionWebUserDto.getUserId());
		userSpaceDto.setUseSpace(userSpaceDto.getUseSpace() + fileSize);
		redisComponent.saveUserSpace(sessionWebUserDto.getUserId(), userSpaceDto);
	}

	private String autoRename(String filePid, String userId, String fileName) {
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setFilePid(filePid);
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setFileName(fileName);
		fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
		Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
		if (count > 0) {
			fileName = StringTools.rename(fileName);
		}
		return fileName;
	}

	@Async
	public void transferFile(String fileId, SessionWebUserDto webUserDto) {
		Boolean transferSuccess = true;
		String targetFilePath = null;
		String cover = null;
		FileTypeEnums fileTypeEnum = null;
		String userId = webUserDto.getUserId();
		FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
		try {
			if (fileInfo == null || !FileStatusEnums.TRANSFER.getStatus().equals(fileInfo.getStatus())) {
				return;
			}
			//临时目录
			String tempFilePath = appconfig.getProjectFloder() + Constants.FILE_FOLER_TEMP_NAME;
			String currentUserFileName = userId + fileId;
			File tempFolder = new File(tempFilePath + currentUserFileName);
			if (!tempFolder.exists()) {
				tempFolder.mkdirs();
			}
			//文件后缀
			String fileSuffix = StringTools.getFileNameSuffix(fileInfo.getFileName());
            String month = DateUtils.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYYMM.getPattern());
			//目标目录
			String targetFolderName = appconfig.getProjectFloder() + Constants.FILE_FOLDER_FILE;
			File targetFolder = new File(targetFolderName + File.separator + month);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}
			String realFileName = currentUserFileName + fileSuffix;
			targetFilePath = targetFolder.getPath() + "/" + realFileName;
            union(tempFolder.getPath(), targetFilePath, fileInfo.getFileName(), true);

			fileTypeEnum = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
			if (FileTypeEnums.VIDEO == fileTypeEnum) {
				curFile4Video(fileId, targetFilePath);
				cover = month + "/" + currentUserFileName + Constants.IMAGE_SUFFIX;
				String coverPath = targetFolderName + "/" + cover;
				ScaleFilter.createCover4Video(new File(targetFilePath), Constants.LEN_150, new File(coverPath));
			} else if (FileTypeEnums.IMAGE == fileTypeEnum) {
				cover = month + "/" + realFileName.replace(".", "_.");
				String coverPath = targetFolderName + "/" + cover;
				Boolean created = ScaleFilter.createThumbnailWidthFFmpeg(new File(targetFilePath), Constants.LEN_150, new File(coverPath), false);
				if (!created) {
					FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
				}
			}

		} catch (Exception e) {
			logger.error("文件转码失败，文件Id:{},userId:{}", fileId, webUserDto.getUserId(), e);
			transferSuccess = false;
		} finally {
			FileInfo updateInfo = new FileInfo();
			updateInfo.setFileSize(new File(targetFilePath).length());
			updateInfo.setFileCover(cover);
			updateInfo.setStatus(transferSuccess ? FileStatusEnums.USING.getStatus() : FileStatusEnums.TRANSFER_FAIL.getStatus());
			fileInfoMapper.updateFileStatusWithOldStatus(fileId, userId, updateInfo, FileStatusEnums.TRANSFER.getStatus());

		}
	}

	private void union(String dirPath, String toFilePath, String fileName, boolean delFile) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			throw new BusinessException("目标文件夹不存在");
		}
		int fileCountLen = dir.listFiles().length;
		File targetFile = new File(toFilePath);
		RandomAccessFile writeFile = null;
		try {
			writeFile = new RandomAccessFile(targetFile, "rw");
			byte[] bytes = new byte[1024*10];
			for (int i = 0; i < fileCountLen; i++) {
				File file = new File(dirPath + File.separator + i);
				RandomAccessFile readFile = null;
		        try {
					readFile = new RandomAccessFile(file, "r");
					int len = -1;
					while ((len = readFile.read(bytes)) != -1) {
						writeFile.write(bytes, 0, len);
					}
				} catch (Exception e) {
					logger.error("合并分片失败", e);
					throw new BusinessException("合并文件失败");
				} finally {
					if (readFile!= null) {
						readFile.close();
					}
				}
			}
		} catch (IOException e) {
			logger.error("合并文件:{}失败", fileName, e);
			throw new BusinessException("合并文件" + fileName + "出错了");
		} finally {
            try {
				if (writeFile != null) {
					writeFile.close();
				}
			} catch (IOException e) {
				logger.error("关闭流失败", e);
			}
			if (!delFile) {
				try {
					FileUtils.deleteDirectory(dir);
				} catch (IOException e) {
					logger.error("删除文件夹失败", e);
					e.printStackTrace();
				}
			}
		}
	}

	private void curFile4Video(String fileId, String videoFilePath) {
		File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
		if (!tsFolder.exists()) {
			tsFolder.mkdirs();
		}
//		final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
		final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -bsf:v h264_mp4toannexb %s";
		final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
		//生成ts
		String tsPath = tsFolder + "/" + Constants.TS_NAME;
		String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
		ProcessUtils.executeCommand(cmd, true);
		//生成索引文件.m3u8和切片.ts
		cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + Constants.M3U8_NAME, tsFolder.getPath(), fileId);
		ProcessUtils.executeCommand(cmd, true);
		new File(tsPath).delete();

	}

	@Override
	public FileInfo newFolder(String filePid, String userId, String fileName, Integer folderType) {
		checkFileName(filePid, userId, fileName, FileFolderTypeEnums.FOLDER.getType());
		FileInfo fileInfo = new FileInfo();
		Date cur = new Date();
		fileInfo.setFileId(StringTools.getRandomString(Constants.LEN_10));
		fileInfo.setUserId(userId);
		fileInfo.setFilePid(filePid);
		fileInfo.setFileName(fileName);
		fileInfo.setFolderType(FileFolderTypeEnums.FOLDER.getType());
		fileInfo.setCreateTime(cur);
		fileInfo.setLastUpdateTime(cur);
		fileInfo.setStatus(FileStatusEnums.USING.getStatus());
		fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
		this.fileInfoMapper.insert(fileInfo);
		return fileInfo;
	}

	private void checkFileName(String filePid, String userId, String fileName, Integer folderType) {
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setFilePid(filePid);
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setFileName(fileName);
		fileInfoQuery.setFolderType(folderType);
		Integer count = this.fileInfoService.findCountByParam(fileInfoQuery);
		if (count > 0) {
			throw new BusinessException("此目录下文件名已存在，请重新命名");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public FileInfo rename(String fileId, String userId, String fileName) {
		FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
		if (fileInfo == null) {
			throw new BusinessException("文件不存在");
		}
		FileInfo updateInfo = new FileInfo();
		checkFileName(fileInfo.getFilePid(), userId, fileName, fileInfo.getFolderType());
		if (!FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
			fileName = fileName + StringTools.getFileNameSuffix(fileInfo.getFileName());
		}

		FileInfo updateFileInfo = new FileInfo();
		updateFileInfo.setFileName(fileName);
		updateFileInfo.setLastUpdateTime(new Date());
		this.fileInfoMapper.updateByFileIdAndUserId(updateFileInfo, fileId, userId);

		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setFileName(fileName);
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setFileId(fileId);
		fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
		Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
		if (count > 1) {
			throw new BusinessException("文件名已存在");
		}
		return fileInfo;
	}

	@Override
	public void changeFileFolder(String fileIds, String filePid, String userId) {
		if (fileIds.equals(filePid)) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (Constants.ZERO_STR.equals(filePid)) {
			FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(filePid, userId);
			if (fileInfo == null) {
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
		}
		String[] fileIdArray = fileIds.split(",");
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setFilePid(filePid);
		fileInfoQuery.setUserId(userId);
		List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);

		Map<String, FileInfo> fileInfoMap = fileInfoList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (a, b)->b));

		fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setFileIdArray(fileIdArray);
		List<FileInfo> selectFileList = this.fileInfoMapper.selectList(fileInfoQuery);

		for (FileInfo item: selectFileList) {
			FileInfo rootFileInfo = fileInfoMap.get(item.getFileName());
			FileInfo updateFileInfo = new FileInfo();
			if (rootFileInfo != null) {
				String fileName = StringTools.rename(rootFileInfo.getFileName());
				updateFileInfo.setFileName(fileName);
			}
			updateFileInfo.setFilePid(filePid);
			this.fileInfoMapper.updateByFileIdAndUserId(updateFileInfo, item.getFileId(), userId);
		}
	}

	@Override
	public void removeFile2RecycleBatch(String userId, String fileIds) {
		String[] fileIdArray = fileIds.split(",");
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setFileIdArray(fileIdArray);
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
		Date currentTime = new Date();
		//选中表面文件
		List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);
		if (fileInfoList == null) {
			return;
		}
		//所有文件夹+表面文件
		List<String> delFilePidList = new ArrayList<>();
		for (FileInfo fileInfo: fileInfoList) {
			findAllSubFolderFileList(delFilePidList, userId, fileInfo.getFileId(), FileDelFlagEnums.USING.getFlag());
		}
		if (!delFilePidList.isEmpty()) {
			FileInfo updateFileInfo = new FileInfo();
			updateFileInfo.setDelFlag(FileDelFlagEnums.DEL.getFlag());
			this.fileInfoMapper.updateFileDelFlagBatch(updateFileInfo, userId, delFilePidList, null, FileDelFlagEnums.USING.getFlag());
		}

		List<String> delFileIdList = Arrays.asList(fileIdArray);
		FileInfo fileInfo = new FileInfo();
		fileInfo.setRecoveryTime(currentTime);
		fileInfo.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
		this.fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFileIdList, FileDelFlagEnums.USING.getFlag());

	}

	//文件夹
	private void findAllSubFolderFileList(List<String> fileIdList, String userId, String fileId, Integer delFlag) {
		fileIdList.add(fileId);
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setFilePid(fileId);
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setDelFlag(delFlag);
		fileInfoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
		List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);
		for (FileInfo fileInfo: fileInfoList) {
			findAllSubFolderFileList(fileIdList, userId, fileInfo.getFileId(), delFlag);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void recoverFile2RecycleBatch(String userId, String fileIds) {
		String[] fileIdArray = fileIds.split(",");
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setFileIdArray(fileIdArray);
		fileInfoQuery.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
		List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);

		List<String> allDelFile = new ArrayList<>();
		for (FileInfo fileInfo: fileInfoList) {
	        if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
				findAllSubFolderFileList(allDelFile, userId, fileInfo.getFileId(), FileDelFlagEnums.DEL.getFlag());
			}
		}

		fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
		fileInfoQuery.setFilePid(Constants.ZERO_STR);
		List<FileInfo> allRootFileList = this.fileInfoMapper.selectList(fileInfoQuery);

		Map<String, FileInfo> allRootFileMap = allRootFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (a,b)->b));

		if (!allDelFile.isEmpty()) {
			FileInfo updateFileInfo = new FileInfo();
			updateFileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
			this.fileInfoMapper.updateFileDelFlagBatch(updateFileInfo, userId, allDelFile, null, FileDelFlagEnums.DEL.getFlag());
		}

		List<String> delFileIdList = Arrays.asList(fileIdArray);
		FileInfo updatefileInfo = new FileInfo();
		updatefileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
		updatefileInfo.setFilePid(Constants.ZERO_STR);
		updatefileInfo.setLastUpdateTime(new Date());
		this.fileInfoMapper.updateFileDelFlagBatch(updatefileInfo, userId, null, delFileIdList, FileDelFlagEnums.RECYCLE.getFlag());

		//重命名
		for (FileInfo fileInfo: fileInfoList) {
			if (allRootFileMap.containsKey(fileInfo.getFileName())) {
				String fileName = StringTools.rename(fileInfo.getFileName());
				FileInfo updateFileInfo = new FileInfo();
				updateFileInfo.setFileName(fileName);
				this.fileInfoMapper.updateByFileIdAndUserId(updateFileInfo, fileInfo.getFileId(), userId);
			}
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteFileBatch(String userId, String fileIds, Boolean isAdmin) {
		String[] fileIdArray = fileIds.split(",");
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setFileIdArray(fileIdArray);
		fileInfoQuery.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
		List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);

		List<String> delFileSubFolderFileIdList =new ArrayList<>();
		for (FileInfo fileInfo: fileInfoList) {
			if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
				findAllSubFolderFileList(delFileSubFolderFileIdList, userId, fileInfo.getFileId(), FileDelFlagEnums.DEL.getFlag());
			}
		}

		if (!delFileSubFolderFileIdList.isEmpty()) {
			this.fileInfoMapper.deleteFileBatch(userId, delFileSubFolderFileIdList, null, isAdmin?null:FileDelFlagEnums.DEL.getFlag());
		}

		this.fileInfoMapper.deleteFileBatch(userId, null, Arrays.asList(fileIdArray), isAdmin?null:FileDelFlagEnums.RECYCLE.getFlag());

		Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
		UserInfo userInfo = new UserInfo();
		userInfo.setUseSpace(useSpace);
		this.userInfoMapper.updateByUserId(userInfo, userId);

		UserSpaceDto userSpaceDto = redisComponent.getUserSpace(userId);
		userSpaceDto.setUseSpace(useSpace);
		redisComponent.saveUserSpace(userId, userSpaceDto);
	}

	@Override
	public void checkFootFilePid(String rootFilePid, String userId, String fileId) {
		if (StringTools.isEmpty(fileId)) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (rootFilePid.equals(fileId)) {
			return;
		}
		checkFilePid(rootFilePid, fileId, userId);
	}

	private void checkFilePid(String rootFilePid, String fileId, String userId) {
		FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
		if (fileInfo == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (Constants.ZERO_STR.equals(fileInfo.getFilePid())) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (fileInfo.getFilePid().equals(rootFilePid)) {
			return;
		}
		checkFilePid(rootFilePid, fileInfo.getFilePid(), userId);
	}

	@Override
	public void saveShare(String shareRootFilePid, String shareFileIds, String myFolderId, String shareUserId, String currentUserId) {
		String[] shareFileIdArray = shareFileIds.split(",");
		//目标文件列表
		FileInfoQuery query = new FileInfoQuery();
		query.setUserId(currentUserId);
		query.setFilePid(myFolderId);
		List<FileInfo> currentFileList = this.fileInfoMapper.selectList(query);

        Map<String, FileInfo> currentFileMap = currentFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (a,b)->b));

		//选择的文件
		query = new FileInfoQuery();
		query.setUserId(shareUserId);
		query.setFileIdArray(shareFileIdArray);
		List<FileInfo> shareFileList = this.fileInfoMapper.selectList(query);
		//重命名选择的文件
		List<FileInfo> copyFileList = new ArrayList<>();
		Date curDate = new Date();
		for (FileInfo item: shareFileList) {
			if (currentFileMap.containsKey(item.getFileName())) {
				item.setFileName(StringTools.rename(item.getFileName()));
			}
			findAllSubFile(copyFileList, item, shareUserId, currentUserId, curDate, myFolderId);
		}

	}

	@Override
	public FileTipVO getFileTipInfoByUserId(String userId) {
		FileTipVO fileTipVO = new FileTipVO();
		FileTipDto fileTipDtoYesterday = redisComponent.getYesterDayCount(userId);
		FileTipDto fileTipDtoToday = redisComponent.findFileTipDtoByTime(0, userId);
		fileTipVO.setFileYesCount(fileTipDtoYesterday.getFileYesCount());
		fileTipVO.setDownLoadYesCount(fileTipDtoYesterday.getDownLoadYesCount());
		fileTipVO.setShowCountYesCount(fileTipDtoYesterday.getShowCountYesCount());
		fileTipVO.setFileCurCount(fileTipDtoToday.getFileYesCount());
		fileTipVO.setDownLoadCurCount(fileTipDtoToday.getDownLoadYesCount());
		fileTipVO.setShowCountCurCount(fileTipDtoToday.getShowCountYesCount());

		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setUserId(userId);
		fileTipVO.setAllFileCount(fileInfoMapper.selectCount(fileInfoQuery));
		DownloadFileQuery downloadFileQuery = new DownloadFileQuery();
		downloadFileQuery.setUserId(userId);
		fileTipVO.setAllDownLoadCount(downloadFileMapper.selectCount(downloadFileQuery));
		FileShareQuery fileShareQuery = new FileShareQuery();
		fileShareQuery.setUserId(userId);
		List<FileShare> fileShareList = fileShareMapper.selectList(fileShareQuery);
		Integer showCount = 0;
		for (FileShare fileShare: fileShareList) {
			showCount += fileShare.getShowCount();
		}
		fileTipVO.setAllshowCount(showCount);
		return fileTipVO;
	}

	private void findAllSubFile(List<FileInfo> copyFileList, FileInfo fileInfo, String sourceUserId,
								String currentUserId, Date curDate, String newFilePid) {
		String sourceFileId = fileInfo.getFileId();
		fileInfo.setCreateTime(curDate);
		fileInfo.setLastUpdateTime(curDate);
		fileInfo.setFilePid(newFilePid);
		fileInfo.setUserId(currentUserId);
		String newFileId = StringTools.getRandomString(Constants.LEN_10);
		fileInfo.setFileId(newFileId);
		copyFileList.add(fileInfo);
		if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
			FileInfoQuery query = new FileInfoQuery();
			query.setFilePid(sourceFileId);
			query.setUserId(sourceUserId);
			List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
			for (FileInfo item: fileInfoList) {
				findAllSubFile(copyFileList, item, sourceUserId, currentUserId, curDate, newFileId);
			}
		}
		this.fileInfoMapper.insertBatch(copyFileList);
	}
}