# 实验8：GitHub Actions + Docker 持续测试实践

## 一、项目说明

本项目基于在线报名系统，用于第12章“部署测试基础设施”实验。学生需要完成本地 Maven/JUnit 测试、GitHub Actions 持续集成配置、Docker 镜像构建与本地容器运行。

## 二、本地测试

```bash
cd online-registration-system
mvn clean test
```

看到 `BUILD SUCCESS` 表示本地单元测试通过。

## 三、GitHub Actions

项目已提供：

```text
.github/workflows/ci.yml
```

将项目上传到 GitHub 后，每次 push 或 pull request 会自动执行：

1. 拉取代码
2. 安装 JDK 11
3. 执行 `mvn clean test`
4. 执行 `mvn package -DskipTests`
5. 构建 Docker 镜像

## 四、Docker 构建

```bash
mvn package -DskipTests
docker build -t online-registration-system:exp8 .
```

## 五、Docker 运行

本项目默认连接 MySQL。若 MySQL 运行在宿主机，请先导入数据库脚本，然后执行：

```bash
docker run --rm -p 8080:8080       -e DB_URL="jdbc:mysql://host.docker.internal:3306/registration_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"       -e DB_USERNAME="root"       -e DB_PASSWORD="你的MySQL密码"       online-registration-system:exp8
```

Linux 用户如果 `host.docker.internal` 不可用，可使用宿主机 IP 地址替换。

## 六、数据库初始化

```bash
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p < src/main/resources/sql/data.sql
```

## 七、访问验证

浏览器访问：

```text
http://localhost:8080
```

或验证接口：

```bash
curl http://localhost:8080/api/activity/list
```
