# Weather Query
基于 Spring Boot 的天气查询示例，展示如何结合 Redis 缓存、Redisson 分布式锁以及令牌桶/滑动窗口限流器来保护上游接口。

## 功能特性
- 令牌桶限流作为快速入口控制，可按需扩展滑动窗口限流以平滑突发流量。
- Redis 缓存天气数据并设置随机 TTL，缓解缓存击穿。
- Redisson 分布式锁在单个城市键上序列化缓存重建，避免并发回源。
- Resilience4j 提供自定义限流器配置及后续接入 Actuator 指标和熔断策略的基础。

## 项目结构
```
src/
  main/java/com/wilson/weatherquery/
    controller/    -> REST 入口 (`WeatherController`)
    service/       -> 业务逻辑、限流器与缓存/锁编排
    config/        -> 基础设施 Bean（Redis、Redisson、限流器）
  main/resources/  -> `application.properties`、脚本（限流压测）
  test/java/       -> 与主代码包结构一致的 JUnit 5 用例
```
更多架构图、时序图位于 `docs/`。

## 环境要求
- JDK 17 及以上
- Apache Maven 3.9+
- 可访问的 Redis 服务（默认 `localhost:6379`）

## 快速开始
1. 安装依赖并构建：
   ```bash
   mvn clean package
   ```
2. 根据部署环境修改 `src/main/resources/application.properties` 中的 Redis 与 Resilience4j 配置。
3. 启动服务：
   ```bash
   mvn spring-boot:run
   ```
4. 发起请求：
   ```bash
   curl "http://localhost:8080/weather?cityId=101010100"
   ```
   返回结果会缓存在 `weather:<cityId>`，锁键为 `lock:weather:<cityId>`。

