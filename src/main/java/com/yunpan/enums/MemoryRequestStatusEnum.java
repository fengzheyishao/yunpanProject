package com.yunpan.enums;

public enum MemoryRequestStatusEnum {
    PENDING(0, "待处理"),
    APPROVED(1, "已批准"),
    REJECTED(2, "已拒绝");

    private final Integer code; // 状态码
    private final String desc;  // 状态描述

    MemoryRequestStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取状态描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 根据状态码获取枚举实例
     */
    public static MemoryRequestStatusEnum getByCode(Integer code) {
        for (MemoryRequestStatusEnum status : MemoryRequestStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据状态码获取状态描述
     */
    public static String getDescByCode(Integer code) {
        MemoryRequestStatusEnum status = getByCode(code);
        return status == null ? null : status.getDesc();
    }

    @Override
    public String toString() {
        return "状态码:" + code + ", 状态描述:" + desc;
    }
}
