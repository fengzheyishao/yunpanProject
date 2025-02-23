package com.yunpan.controller;

import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.dto.SessionShareDto;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.enums.ResponseCodeEnum;
import com.yunpan.exception.BusinessException;
import com.yunpan.utils.CopyTools;
import com.yunpan.utils.StringTools;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.entity.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;


public class ABaseController {
    private static final Logger logger = LoggerFactory.getLogger(ABaseController.class);

    protected static final String STATUC_SUCCESS = "success";

    protected static final String STATUC_ERROR = "error";

    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected <T> ResponseVO getBusinessErrorResponseVO(BusinessException e, T t) {
        ResponseVO vo = new ResponseVO();
        vo.setStatus(STATUC_ERROR);
        if (e.getCode() != null) {
            vo.setCode(ResponseCodeEnum.CODE_600.getCode());
        } else {
            vo.setCode(e.getCode());
        }
        vo.setInfo(e.getMessage());
        vo.setData(t);
        return vo;
    }

    protected <T> ResponseVO getServerErrorResponseVO(T t) {
        ResponseVO vo = new ResponseVO();
        vo.setStatus(STATUC_ERROR);
        vo.setCode(ResponseCodeEnum.CODE_500.getCode());
        vo.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        vo.setData(t);
        return vo;
    }

    protected void readFile(HttpServletResponse response, String filePath) {
        if (!StringTools.pathIsOk(filePath)) {
            return;
        }
        OutputStream outputStream = null;
        FileInputStream inputStream = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            inputStream = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            outputStream = response.getOutputStream();
            int len = 0;
            while ((len = inputStream.read(byteData)) != -1) {
                outputStream.write(byteData, 0, len);
            }
            outputStream.flush();
        } catch (Exception e) {
            logger.error("读取文件失败", e);
        } finally {
            try {
                if (inputStream!= null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error("IO异常", e);
            }
            try {
                if (outputStream!= null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                logger.error("IO异常", e);
            }
        }
    }

    protected SessionWebUserDto getUserInfoFromSession(HttpSession session) {
        return (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
    }

    protected SessionShareDto getShareInfoFromSession(HttpSession session, String shareId) {
        return (SessionShareDto) session.getAttribute(Constants.SESSION_SHARE_KEY + shareId);
    }

    protected <S, T> PaginationResultVO<T> convert2PaginationVO(PaginationResultVO<S> pageVO, Class<T> classz) {
        PaginationResultVO<T> vo = new PaginationResultVO<>();
        vo.setList(CopyTools.copyList(pageVO.getList(), classz));
        vo.setPageNo(pageVO.getPageNo());
        vo.setPageSize(pageVO.getPageSize());
        vo.setTotalCount(pageVO.getTotalCount());
        vo.setPageTotal(pageVO.getPageTotal());
        return vo;
    }

}

