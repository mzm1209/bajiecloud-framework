package com.bajiezu.cloud.common.mybatis.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BaseDO implements Serializable {

    /**
     * 合作商id
     */
    @TableField("partner_id")
    private Long partnerId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 创建者，sys_user表的主键id
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 更新者，sys_user表的主键id
     */
    @TableField("update_by")
    private Long updateBy;

    /**
     * 删除标识，0-正常，1-已删除
     */
    @TableField("is_deleted")
    private Integer isDeleted;
}