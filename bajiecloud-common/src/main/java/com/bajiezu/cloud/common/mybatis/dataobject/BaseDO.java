package com.bajiezu.cloud.common.mybatis.dataobject;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

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
   */
  private Long updateBy;

  /**
   * 删除标识，0-正常，>0 删除时间戳（秒）
   */
  private Long deleteFlag;
}