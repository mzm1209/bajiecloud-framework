# 微服务开发调试指南

## 开发环境配置

### 个人 MSE Namespace 设置
每个开发者在阿里云 MSE 中拥有独立的 `namespace`，命名规范为 `dev-{username}`，确保服务注册和配置获取的隔离性。

### 本地数据库准备
- 启动本地 `MySQL` 数据库实例
- 创建对应的服务数据库
- 确保端口 `3306` 可访问

## 调试启动方式

### 1. 启动依赖服务
```bash
# 启动本地依赖服务（数据库、缓存等）
docker-compose up -d mysql redis
```


### 2. 配置环境变量
设置个人开发环境变量：
```bash
export NACOS_NAMESPACE=dev-yourname
export DEV_DB_USERNAME=your_db_username
export DEV_DB_PASSWORD=your_db_password
```


### 3. 本地启动服务
通过以下任一方式启动服务进行调试：

**IDE 方式**：
- 直接运行主启动类
- 配置 VM options：`-DNACOS_NAMESPACE=dev-yourname`

**Maven 方式**：
```bash
mvn spring-boot:run -DNACOS_NAMESPACE=dev-yourname
```


## 配置文件说明

### bootstrap.yml
- 包含 MSE 服务发现和配置中心的核心连接配置
- 在应用启动初期加载，确保能正确连接阿里云 MSE

### application-dev.yml
- 开发环境的本地配置
- 数据库连接指向本地实例
- 日志级别设置为 DEBUG 便于调试

## 调试最佳实践

### 服务注册与发现
- 服务自动注册到 MSE 个人 `namespace`
- 通过 MSE 控制台可查看服务注册状态
- 支持服务间调用调试

### 配置管理
- 个人配置存储在 MSE 配置中心对应 `namespace`
- 支持配置动态刷新，修改后无需重启服务
- 可通过 MSE 控制台在线编辑配置

### 注意事项
- 确保网络可访问阿里云 MSE 服务
- 不同开发者间的服务完全隔离
- 本地调试不影响测试和生产环境