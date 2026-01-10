package com.bajiezu.cloud.common.mybatis.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.regex.Pattern;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class PatternMetaObjectHandler implements MetaObjectHandler {

  // 定义时间字段模式
  private static final Pattern TIME_FIELD_PATTERN =
      Pattern.compile(".*(Time|At|Date|Timestamp)$", Pattern.CASE_INSENSITIVE);

  @Override
  public void insertFill(MetaObject metaObject) {
    fillField(metaObject);
  }


  private void fillField(MetaObject metaObject) {
    // 获取实体类类型
    Object originalObject = metaObject.getOriginalObject();
    if (originalObject == null) {
      return;
    }

    Class<?> clazz = originalObject.getClass();

    // 遍历所有字段
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      String fieldName = field.getName();

      // 判断是否是时间类型字段
      if (Date.class.isAssignableFrom(field.getType())) {
        // 判断字段名匹配模式
        if (TIME_FIELD_PATTERN.matcher(fieldName).matches()) {
          // 检查是否有 @TableField 注解
          TableField tableField = field.getAnnotation(TableField.class);
          if (tableField != null && (tableField.fill() == FieldFill.INSERT
              || tableField.fill() == FieldFill.INSERT_UPDATE)) {
            if (metaObject.getValue(fieldName) == null) {
              this.fillStrategy(metaObject, fieldName, new Date());
            }
          }
        }
      }
    }
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    fillField(metaObject);
  }
}