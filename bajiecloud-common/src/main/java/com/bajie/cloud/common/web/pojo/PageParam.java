package com.bajie.cloud.common.web.pojo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "分页参数")
@Data
public class PageParam implements Serializable {

  /**
   * 每页条数 - 不分页
   * <p>
   * 例如说，导出接口，可以设置 {@link #pageSize} 为 -1 不分页，查询所有数据。
   */
  public static final Integer PAGE_SIZE_NONE = -1;
  private static final Integer PAGE_NO = 1;
  private static final Integer PAGE_SIZE = 10;
  @Schema(description = "页码，从 1 开始", requiredMode = RequiredMode.REQUIRED, example = "1")
  @NotNull(message = "页码不能为空")
  @Min(value = 1, message = "页码最小值为 1")
  private Integer pageNo = PAGE_NO;

  @Schema(description = "每页条数，最大值为 100", requiredMode = RequiredMode.REQUIRED, example = "10")
  @NotNull(message = "每页条数不能为空")
  @Min(value = 1, message = "每页条数最小值为 1")
  @Max(value = 100, message = "每页条数最大值为 100")
  private Integer pageSize = PAGE_SIZE;

  @Schema(description = "是否需要查总数,默认都需要", requiredMode = RequiredMode.NOT_REQUIRED, example = "true")
  private boolean searchCount = true;

  public <T> IPage<T> toPage() {
    return new Page<>(pageNo, pageSize, searchCount);
  }

}