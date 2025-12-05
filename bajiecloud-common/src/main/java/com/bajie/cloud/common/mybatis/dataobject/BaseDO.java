package com.bajie.cloud.common.mybatis.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BaseDO implements Serializable {

    /**
     * 合作商id
     */
    private Long partnerId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 创建者，sys_user表的主键id
     */
    private Long createBy;

    /**
     * 更新者，sys_user表的主键id
     *
     */
    private Long updateBy;

    /**
     * 是否删除 0:否 1:是
     */
    private Integer isDeleted;
}