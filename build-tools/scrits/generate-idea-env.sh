#!/bin/bash

# 脚本: generate-idea-env.sh
# 功能: 将env文件转换为IDEA可识别的环境变量格式
# 用法: ./generate-idea-env.sh [env-file]

ENV_FILE=${1:-"env/env-dev.profile"}
IDEA_ENV_FILE="idea-env.properties"

echo "正在从 $ENV_FILE 生成IDEA环境变量配置..."
echo "# IDEA环境变量配置" > $IDEA_ENV_FILE
echo "# 生成时间: $(date)" >> $IDEA_ENV_FILE
echo "" >> $IDEA_ENV_FILE

# 读取env文件，过滤掉注释和空行，转换为IDEA格式
while IFS='=' read -r key value || [ -n "$key" ]; do
    # 跳过空行和注释行
    [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]] && continue

    # 去除值中的前后空格和可能的注释
    clean_key=$(echo "$key" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
    clean_value=$(echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//' | cut -d'#' -f1 | sed 's/[[:space:]]*$//')

    # 输出到文件
    echo "$clean_key=$clean_value" >> $IDEA_ENV_FILE
    echo "已添加: $clean_key"
done < "$ENV_FILE"

echo ""
echo "配置已生成到: $IDEA_ENV_FILE"
echo ""
echo "在IDEA中使用方法:"
echo "1. 进入 'Run/Debug Configurations'"
echo "2. 选择你的微服务运行配置"
echo "3. 在 'Environment variables' 中添加变量或"
echo "4. 使用 'EnvFile' 插件加载 $IDEA_ENV_FILE 文件"