# 在线活动报名系统（JMeter性能测试教学版）

## 1. 项目定位
本项目用于《软件测试方法和技术》第7章“性能测试”实验教学。
建议学生使用：
- VS Code
- Java 11
- MySQL 8
- JMeter 5.6+

## 2. 功能模块
- 登录接口（会产生 Session Cookie）
- 活动创建接口（管理员）
- 活动列表接口
- 活动详情接口
- 在线报名接口（核心压测接口）
- 报名人数统计接口

## 3. 运行步骤
### 3.1 初始化数据库
执行：
- `src/main/resources/sql/schema.sql`
- `src/main/resources/sql/data.sql`

### 3.2 修改数据库配置
编辑 `src/main/resources/application.yml` 中的：
- username
- password

### 3.3 启动项目
```bash
mvn spring-boot:run
```

启动后访问：
- http://localhost:8080
- http://localhost:8080/api/activity/list

## 4. 推荐测试接口
### 4.1 登录
POST `/api/auth/login`
```json
{
  "username": "admin",
  "password": "123456"
}
```

### 4.2 查询活动列表
GET `/api/activity/list`

### 4.3 报名
POST `/api/register`
```json
{
  "activityId": 1,
  "studentName": "student01"
}
```

### 4.4 查询人数
GET `/api/register/count?id=1`

## 5. JMeter建议场景
- 场景1：20用户访问活动列表
- 场景2：100用户并发报名
- 场景3：500用户压力测试
- 场景4：报名后再查人数，观察吞吐量和响应时间变化
