## 本地调试指南

### 推荐部署方式

为了更好的开发体验和避免影响他人，建议在本地调试时使用个人独立的 MySQL 和 Redis 实例。

### 准备工作

1. **部署个人独立的 MySQL 和 Redis**
    - 在本地或云环境中部署自己的 MySQL 和 Redis 实例
    - 确保这些服务可以通过网络访问

2. **复制并修改配置文件**
   ```bash
   # 复制 dev.profile 到本地配置文件
   cp env/dev.profile env/local.profile
   ```

修改 `env/local.profile` 中的相关配置项：

   ```properties
   # 修改数据库配置指向您的本地实例
DB_HOST=your-local-db-host
DB_PORT=3306
DB_NAME=your-db-name
DB_USERNAME=your-username
DB_PASSWORD=your-password
# 修改Redis配置指向您的本地实例
REDIS_HOST=your-local-redis-host
REDIS_PORT=6379
REDIS_USERNAME=your-redis-username
REDIS_PASSWORD=your-redis-password
   ```

3. **修改脚本引用的环境文件**
   修改 `generate-idea-env-string.sh` 脚本中的默认环境文件路径：
   ```bash
   ENV_FILE=${1:-"env/local.profile"}  # 将 dev.profile 改为 local.profile
   ```

### 生成 IDEA 环境变量

执行脚本来生成适用于 IntelliJ IDEA 的环境变量字符串：

```bash
# 执行脚本生成环境变量字符串
./generate-idea-env-string.sh
```

脚本会生成一个 `idea-env-string.txt` 文件，其中包含了格式化的环境变量字符串。

### 配置 IntelliJ IDEA

1. 打开 IntelliJ IDEA
2. 进入 'Run/Debug Configurations'
3. 选择您的微服务运行配置
4. 在 'Environment variables' 字段中粘贴生成的环境变量字符串

或者您可以直接从终端输出中复制环境变量字符串：

```
==========================================
spring.active.profile=dev;MSE_NACOS_SERVER_ADDR=mse-553b8e52-nacos-ans.mse.aliyuncs.com:8848;NACOS_NAMESPACE=xiaoming-dev;NACOS_GROUP=DEFAULT_GROUP;DB_HOST=your-local-db-host;DB_PORT=3306;DB_NAME=your-db-name;DB_USERNAME=your-username;DB_PASSWORD=your-password;REDIS_HOST=your-local-redis-host;REDIS_PORT=6379;REDIS_USERNAME=your-redis-username;REDIS_PASSWORD=your-redis-password;SYSTEM_SERVICE_PORT=8081;CUSTOMER_SERVICE_PORT=8082;ORDER_SERVICE_PORT=8083
==========================================
```

### 启动依赖服务

如果您需要使用 Docker Compose
来启动依赖服务，请修改相应模块中的 [docker-compose.yml](file://E:\git\bajiecloud-framewark\build-tools\docker-compose\dev\docker-compose.yml)
文件，并启动相关服务：

```bash
# 启动依赖服务
docker-compose up -d
```

### 启动本地服务

完成以上配置后，您就可以在 IntelliJ IDEA 中正常启动和调试您的本地服务了。

### 注意事项

- 请确保您的本地 MySQL 和 Redis 实例已经正确配置并运行
- 根据实际需要调整 `local.profile` 中的配置参数
-
如果有新增的服务依赖，请相应地更新配置文件和 [docker-compose.yml](file://E:\git\bajiecloud-framewark\build-tools\docker-compose\dev\docker-compose.yml)