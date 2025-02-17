package com.yunpan.utils;

import com.yunpan.entity.constants.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;


public class StringTools {
    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }

    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static String encodeByMD5(String orignString) {
        return isEmpty(orignString) ? null : DigestUtils.md5Hex(orignString);
    }

    public static boolean pathIsOk(String path) {
        if (StringTools.isEmpty(path)) {
            return true;
        }
        if (path.contains("../") || path.contains("..\\")) {
            return false;
        }
        return true;
    }

    public static String getFileNamePre(String fileName) {
        int index = fileName.indexOf(".");
        if (index == -1) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    public static String getFileNameSuffix(String fileName) {
        int index = fileName.indexOf(".");
        if (index == -1) {
            return "";
        }
        return fileName.substring(index);
    }

    public static String rename(String fileName) {
        String fileNameReal = getFileNamePre(fileName);
        String suffix = getFileNameSuffix(fileName);
        return fileNameReal + "_" + getRandomString(Constants.LEN_5) + suffix;
    }


}
