package com.yunpan.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: Mapper
 * @auther: lnorly
 * @Date: 2024/09/11
 */
public interface FileInfoMapper<T, P> extends BaseMapper {

	/**
	 * 根据FileIdAndUserId查询
	 */
	T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

	/**
	 * 根据FileIdAndUserId更新
	 */
	Integer updateByFileIdAndUserId(@Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);

	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

	Long selectUseSpace(@Param("userId") String userId);

	void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId, @Param("bean") T t, @Param("oldStatus") Integer oldStatus);

	void updateFileDelFlagBatch(@Param("bean") T t,
								@Param("userId") String userId,
								@Param("filePidList") List<String> filePidList,
								@Param("fileIdList") List<String> fileIdList,
								@Param("oldDelFlag") Integer oldDelFlag);

	void deleteFileBatch(@Param("userId") String userId,
						 @Param("filePidList") List<String> filePidList,
						 @Param("fileIdList") List<String> fileIdList,
						 @Param("oldDelFlag") Integer oldDelFlag);

	void deleteFileByUserId(@Param("userId") String userId);

}