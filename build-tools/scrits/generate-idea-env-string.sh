#!/bin/bash

# 脚本: generate-idea-env-string.sh
# 功能: 将env文件转换为IDEA可识别的环境变量字符串格式
# 格式: key1=value1;key2=value2;key3=value3
# 用法: ./generate-idea-env-string.sh [env-file] [output-file]

ENV_FILE=${1:-"env/dev.profile"}
OUTPUT_FILE=${2:-"idea-env-string.txt"}

echo "正在从 $ENV_FILE 生成IDEA环境变量字符串..."
echo "# IDEA环境变量字符串" > $OUTPUT_FILE
echo "# 生成时间: $(date)" >> $OUTPUT_FILE
echo "# 复制下方字符串，粘贴到IDEA Run Configuration的Environment variables中" >> $OUTPUT_FILE
echo "" >> $OUTPUT_FILE

# 用于拼接字符串
ENV_STRING=""

# 读取env文件，过滤掉注释和空行
while IFS='=' read -r key value || [ -n "$key" ]; do
    # 跳过空行和注释行
    [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]] && continue

    # 去除值中的前后空格和可能的注释
    clean_key=$(echo "$key" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
    clean_value=$(echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//' | cut -d'#' -f1 | sed 's/[[:space:]]*$//')

    # 构建环境变量字符串
    if [[ -n "$ENV_STRING" ]]; then
        ENV_STRING="${ENV_STRING};${clean_key}=${clean_value}"
    else
        ENV_STRING="${clean_key}=${clean_value}"
    fi

    echo "已添加: ${clean_key}=${clean_value}"
done < "$ENV_FILE"

# 输出到文件
echo "$ENV_STRING" >> $OUTPUT_FILE

echo ""
echo "环境变量字符串已生成到: $OUTPUT_FILE"
echo ""
echo "在IDEA中使用方法:"
echo "1. 进入 'Run/Debug Configurations'"
echo "2. 选择你的微服务运行配置"
echo "3. 在 'Environment variables' 字段中粘贴上述字符串"
echo "4. 或者复制下方字符串:"
echo ""
echo "=========================================="
echo "$ENV_STRING"
echo "=========================================="