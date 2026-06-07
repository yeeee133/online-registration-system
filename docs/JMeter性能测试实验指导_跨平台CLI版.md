# JMeter 性能测试实验指导（macOS + Win11，超详细步骤版）

> 适用对象：第一次做接口性能测试的学生  
> 实验目标：不仅“跑通”，还要“会解释结果、会定位问题、会提出改进”

---

## A. 你将学会什么
完成本实验后，你应该能独立完成：
1. 启动 Java 后端 + MySQL。
2. 使用 JMeter GUI 和 CLI 执行接口性能测试。
3. 读懂 `result.jtl`、HTML 报告、OverTime 曲线。
4. 区分“业务失败”和“性能失败”。
5. 给出有证据的性能结论与改进建议。

---

## B. 实验整体流程（先看全貌）
1. 环境检查（JDK/Maven/MySQL/JMeter）
2. 初始化数据库
3. 启动 Spring Boot 并验证 API 可用
4. 在 JMeter GUI 跑 `v2_demo` 脚本（观察场景）
5. 用 CLI 一键压测（可复现实验）
6. 读取日志和报告（指标 + 图表）
7. 完成实验结论（含问题分析）

---

## C. 实验目录与文件角色
项目目录：`jmeter_java_signup_pack`

关键文件：
- 后端服务：`online-registration-system`
- JMeter 脚本目录：`jmeter`
- 指导文档：`docs`

JMeter 脚本：
- `online_registration_performance_test.jmx`：原始脚本
- `online_registration_performance_test_v2.jmx`：参数化改进脚本
- `online_registration_performance_test_v2_demo.jmx`：本实验推荐脚本（默认参数更明显）

数据文件：
- `students.csv`
- `students_demo.csv`

一键脚本：
- `run_perf.sh`（自动生成归档结果和对比结果）

---

## D. 第 0 步：环境检查（必须先过）

## D1. macOS
打开 Terminal，执行：
```bash
java -version
mvn -v
mysql --version
jmeter -v
```

## D2. Win11（PowerShell）
```powershell
java -version
mvn -v
mysql --version
jmeter -v
```

预期：以上命令都能返回版本号。

若 `jmeter` 不存在：
- 临时进入 JMeter 安装目录的 `bin` 执行 `jmeter.bat`（Win）
- 或使用完整路径执行（mac 示例 `/opt/homebrew/bin/jmeter`）

---

## E. 第 1 步：初始化数据库

## E1. 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS registration_system DEFAULT CHARSET utf8mb4;
```

## E2. 导入 SQL
顺序执行：
1. `schema.sql`
2. `data.sql`

你可以在 MySQL 客户端里使用：
```sql
USE registration_system;
SOURCE /绝对路径/online-registration-system/src/main/resources/sql/schema.sql;
SOURCE /绝对路径/online-registration-system/src/main/resources/sql/data.sql;
```

检查是否导入成功：
```sql
SHOW TABLES;
SELECT COUNT(*) FROM activity;
```

预期：存在业务表，`activity` 有初始化数据。

---

## F. 第 2 步：启动后端服务 + API 健康检查

## F1. macOS 启动
```bash
cd /Users/daixin/Java_project/jmeter_java_signup_pack/online-registration-system
DB_USERNAME=root DB_PASSWORD='你的密码' mvn spring-boot:run
```

## F2. Win11 启动
```powershell
cd D:\Java_project\jmeter_java_signup_pack\online-registration-system
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的密码"
mvn spring-boot:run
```

## F3. 健康检查（另开终端执行）
```bash
curl http://127.0.0.1:8080/api/activity/list
```
预期响应包含：`"success":true`

如果失败：
- `Connection refused`：后端没启动或端口不对。
- `Access denied for user`：DB 用户名/密码不对。
- `Unknown database`：数据库未创建。

---

## G. 第 3 步：JMeter GUI 演示运行（理解场景）

## G1. 启动 JMeter
macOS：
```bash
cd /Users/daixin/Java_project/jmeter_java_signup_pack/jmeter
/opt/homebrew/bin/jmeter
```

Win11：
```powershell
cd D:\Java_project\jmeter_java_signup_pack\jmeter
jmeter
```

## G2. 打开脚本
打开：`online_registration_performance_test_v2_demo.jmx`

## G3. 观察脚本结构（一定要看）
- Test Plan -> 用户变量（host/port/activityId/并发参数）
- 场景1：活动列表基线
- 场景2：报名并发（登录 -> 报名 -> 查询人数）
- 断言：登录成功、报名可接受结果

## G4. 运行并观察
点击运行后，重点看：
- Summary Report：吞吐量、平均响应、错误率
- Aggregate Report：中位数、90/95/99 分位
- View Results Tree：单请求响应内容

---

## H. 第 4 步：CLI 一键压测（推荐正式实验方式）

> 理由：可复现、可归档、可对比，非常适合实验报告。

## H1. macOS 执行
```bash
cd /Users/daixin/Java_project/jmeter_java_signup_pack/jmeter
./run_perf.sh
```

覆盖参数示例：
```bash
ACTIVITY_ID=1 SCENE2_THREADS=200 SCENE2_LOOPS=1 ./run_perf.sh
```

## H2. Win11 等效命令（手工一键）
```powershell
cd D:\Java_project\jmeter_java_signup_pack\jmeter
$ts = Get-Date -Format "yyyyMMdd_HHmmss"
$runDir = "runs\\$ts"
New-Item -ItemType Directory -Force -Path $runDir | Out-Null

jmeter -n `
  -t online_registration_performance_test_v2_demo.jmx `
  -l "$runDir\\result.jtl" `
  -j "$runDir\\jmeter.log" `
  -e -o "$runDir\\report" `
  -JbaseUrl=localhost `
  -Jport=8080 `
  -Jprotocol=http `
  -JactivityId=3 `
  -JloginUsername=admin `
  -JloginPassword=123456 `
  -JstudentsFile=students_demo.csv `
  -Jscene1Threads=30 `
  -Jscene1Ramp=15 `
  -Jscene1Loops=15 `
  -Jscene2Threads=150 `
  -Jscene2Ramp=30 `
  -Jscene2Loops=2
```

打开报告：
```powershell
start "$runDir\\report\\index.html"
```

---

## I. 第 5 步：结果文件逐个解释（必须会）

每次运行后会生成：`runs/<timestamp>/...`

1. `result.jtl`
- 每个请求一行记录（核心原始数据）
- 可用于二次分析

2. `jmeter.log`
- JMeter 自身运行日志
- 用于排查脚本/插件/执行错误

3. `run.log`
- 本次命令行实时输出
- 快速查看总请求、吞吐量、错误率

4. `report/index.html`
- 可视化报告入口

5. `summary.txt`
- 提炼后的关键总指标

6. `compare_with_previous.txt`
- 与上一次运行总指标对比（回归判断）

---

## J. 第 6 步：怎么读 HTML 报告（按这个顺序）

## J1. 先看 Total（总体）
重点四个指标：
- Throughput（吞吐量，req/s）
- Mean Response Time（平均响应）
- P95（95分位响应）
- Error %（错误率）

## J2. 再看分接口
观察哪个接口最慢、哪个接口错误最多。

## J3. 再看 OverTime（趋势图）
- X 轴：测试进行时间
- Y 轴：指标值

重点图：
1. `responseTimeOverTime`
- 是否随时间上升（系统退化）

2. `latencyOverTime`
- 服务处理与排队趋势

3. `connectTimeOverTime`
- TCP 建连趋势；如果高且波动大，可能连接层瓶颈

4. `threadsStateOverTime`
- 用于对齐并发阶段，解释曲线变化

## J4. 最后看 Errors
- 错误集中在哪个接口？
- 是业务失败还是性能失败？

---

## K. 第 7 步：区分“业务失败”和“性能失败”

业务失败示例：
- `请勿重复报名`
- `活动名额已满`

性能失败示例：
- 超时
- 连接失败
- 5xx 明显上升
- 响应时间随并发明显恶化

实验报告中必须写清：
- 本次错误属于哪一类
- 依据是什么（响应内容、错误分布、趋势图）

---

## L. 第 8 步：建议做 3 轮测试（形成完整结论）

建议参数（可在 CLI 覆盖）：

1. 基线测试（小负载）
- `SCENE2_THREADS=30`，`SCENE2_LOOPS=1`

2. 负载测试（中负载）
- `SCENE2_THREADS=100`，`SCENE2_LOOPS=2`

3. 压力测试（高负载）
- `SCENE2_THREADS=200`，`SCENE2_LOOPS=2`

每轮都保留：
- `summary.txt`
- `compare_with_previous.txt`
- `report` 截图

---

## M. 第 9 步：实验报告填写模板（可直接复制）

## M1. 实验环境
- OS：
- JDK：
- Maven：
- MySQL：
- JMeter：

## M2. 实验步骤摘要
- 后端启动命令：
- 压测命令：
- 参数配置：

## M3. 结果（每轮）
- 吞吐量：
- 平均响应：
- P95：
- 错误率：
- 最慢接口：

## M4. 图表分析
- OverTime 是否出现退化：
- connectTime 是否异常：
- 错误主要来源：

## M5. 结论与改进建议
- 系统当前容量估计：
- 风险点：
- 下一步优化方向：

---

## N. 常见故障排查速查表

1. JMeter 跑不起来
- 检查 `jmeter -v`
- 检查 Java 版本

2. 报告打不开
- 确认 `report/index.html` 存在
- 用浏览器直接打开

3. 全部请求失败
- 先用 `curl` 验证后端
- 检查 host/port/protocol

4. 报名接口总失败
- 通常是业务规则导致，不一定是性能问题
- 检查断言和 `students_demo.csv` 是否重复

5. Win11 命令报路径错误
- 注意 PowerShell 路径和转义（反引号续行）

---

## O. 教师评分建议（可选）
- 30%：环境与执行（是否独立跑通）
- 30%：结果分析（是否能读懂指标与图）
- 20%：问题定位（能否区分业务/性能失败）
- 20%：改进建议（是否有可执行性）

---

## P. 附：最短实验通关命令（mac）
1. 启动后端：
```bash
cd /Users/daixin/Java_project/jmeter_java_signup_pack/online-registration-system
DB_USERNAME=root DB_PASSWORD='你的密码' mvn spring-boot:run
```
2. 新终端跑压测：
```bash
cd /Users/daixin/Java_project/jmeter_java_signup_pack/jmeter
./run_perf.sh
```
3. 打开报告：
```bash
open "$(ls -1dt runs/* | head -n 1)/report/index.html"
```

如果学生只会这 3 组命令，也能完成本实验主流程。
