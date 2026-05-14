# APP Token 能力接入说明（customer 工程）

> 适用对象：customer 工程业务开发同学
>
> 目标：在 customer 工程中接入 framework 提供的 APP token 能力，支持微信/支付宝小程序等 APP 端登录、鉴权、服务间透传。

---

## 1. 能力概览

framework 已提供 APP token 独立能力（与平台 token 分离）：

- APP token Header：`app-user-token`
- APP token Parameter：`app_security_token`
- APP token Redis 前缀：`bajie:auth:app-user:`
- APP 请求上下文：`AppLoginUserContext`
- APP token 服务：`AppLoginTokenService`
- APP 鉴权过滤器：`AppTokenAuthenticationFilter`（默认拦截 `/api/app/**`）

---

## 2. customer 接入前置条件

1. customer 升级依赖到包含 APP token 功能的 framework 版本。
2. customer 已接入 Redisson（framework 的 token 存储依赖 Redis）。
3. APP 业务接口建议统一使用 `/api/app/**` 路径前缀。

---

## 3. 登录接入（业务代码）

## 3.1 典型流程

以微信/支付宝小程序登录为例：

1. customer 接收登录请求（`code/authCode`）。
2. customer 调第三方平台换取用户标识（openid/unionid）。
3. customer 查找/创建本地用户并组装 `LoginUser`。
4. customer 调用 `AppLoginTokenService.generateToken(loginUser)`。
5. 返回 token 给前端（响应体字段建议 `accessToken`），前端后续请求放入 `app-user-token`。

## 3.2 关键点

- `generateToken` 内部会自动：
    - 生成 JWT token
    - 将 token 回写到 `loginUser.setToken(...)`
    - 将 `token -> LoginUser` 写入 Redis（APP key 前缀）
- customer 无需手工写 Redis。

---

## 4. 鉴权接入（接口访问）

## 4.1 APP 接口路径规范

- 受保护接口建议全部放在 `/api/app/**`。
- `AppTokenAuthenticationFilter` 会读取 token 并自动完成鉴权。

## 4.2 token 传递规范

优先推荐 Header：

- `app-user-token: <token>`

兼容 Parameter：

- `app_security_token=<token>`

---

## 5. 在业务代码中获取当前 APP 用户

鉴权成功后，当前请求可通过 `AppLoginUserContext.getLoginUser()` 读取登录用户信息。

典型场景：

- `GET /api/app/user/me`：读取当前用户资料
- `PUT /api/app/user/me`：更新当前用户昵称、头像等

---

## 6. 登出接入

customer 处理 APP 登出时，调用：

- `AppLoginTokenService.deleteToken(token)`

效果：

- 删除 Redis 登录态
- token 立即失效

---

## 7. 微服务透传（Feign）

## 7.1 说明

framework 提供了 `AppLoginUserRequestInterceptor`，用于服务间调用时自动透传 APP token。

## 7.2 建议接入方式

- 在 customer 工程将该拦截器声明为 Bean（或等待 framework 后续自动装配）。
- 透传后下游服务可通过 APP 过滤器继续完成登录态识别。

---

## 8. 常见问题与排查

## 8.1 返回 401

优先检查：

1. 请求路径是否为 `/api/app/**`
2. 是否携带 `app-user-token`
3. Redis 中是否存在 `bajie:auth:app-user:<token>`
4. token 是否过期
5. token 是否被误用为平台 token

## 8.2 透传失败

1. Feign 是否启用了 APP 透传拦截器 Bean
2. 上游请求是否已进入 APP 登录态（`AppLoginUserContext` 中有用户）
3. 下游接口是否走 `/api/app/**`

---

## 9. 建议的 customer API 规划

- `POST /api/app/auth/wechat/login`
- `POST /api/app/auth/alipay/login`
- `POST /api/app/auth/logout`
- `GET /api/app/user/me`
- `PUT /api/app/user/me`

---

## 10. 安全建议

1. APP token 与平台 token 严禁混用。
2. 建议在 customer 侧记录登录设备信息与来源渠道（wechat/alipay/app）。
3. 建议对高风险接口增加二次验证能力。
4. 建议接入登录失败/401 监控告警。

