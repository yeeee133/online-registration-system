# 在线活动报名系统 API 接口文档

## 1. 登录
- **URL**：`POST /api/auth/login`
- **说明**：登录成功后产生 Session Cookie，供后续接口复用。
- **请求体**
```json
{
  "username": "admin",
  "password": "123456"
}
```

## 2. 创建活动（管理员）
- **URL**：`POST /api/activity/create`
- **说明**：需先登录且角色为 ADMIN
- **请求体**
```json
{
  "name": "篮球比赛",
  "maxPeople": 100
}
```

## 3. 活动列表
- **URL**：`GET /api/activity/list`

## 4. 活动详情
- **URL**：`GET /api/activity/detail?id=1`

## 5. 在线报名（核心压测接口）
- **URL**：`POST /api/register`
- **请求体**
```json
{
  "activityId": 1,
  "studentName": "student001"
}
```

## 6. 报名人数统计
- **URL**：`GET /api/register/count?id=1`
