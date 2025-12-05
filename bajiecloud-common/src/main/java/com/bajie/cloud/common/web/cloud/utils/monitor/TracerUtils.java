package com.bajie.cloud.common.web.cloud.utils.monitor;

import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TracerUtils {

  public String getTraceId() {
    // 先返回UUID， 后续使用哪种再实现
    return UUID.randomUUID().toString();
  }

}
