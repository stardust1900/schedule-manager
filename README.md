# schedule-manager

#### 介绍
一个简单的定时任务管理系统

使用SpringBoot  JPA 实现

数据库使用 H2

前端 thymeleaf jquery bootstrap

第三方插件：
[cron-selector](https://github.com/MrFengGG/cron-selector)
[bootstrap-datetimepicker](https://www.malot.fr/bootstrap-datetimepicker/)

#### 安装教程

1.  git clone https://gitee.com/wangyidao/schedule-manager.git
2.  cd schedule-manager
3.  ./mvnw spring-boot:run (Linux|Mac)  mvnw spring-boot:run (windows)

#### 使用说明

1.  访问 系统主页 http://localhost:8080   用户名/密码 在启动日志中获取
2.  访问 H2 管理界面 http://localhost:8080/h2-console   默认密码 sa/sa
3.  动态更新日志 curl http://localhost:8080/logLevel/{DEBUG|INFO|WARN|ERROR}

#### 界面截图

**登录**

![登录](https://gitee.com/wangyidao/schedule-manager/raw/master/1.jpg)

**任务列表**

![任务列表](https://gitee.com/wangyidao/schedule-manager/raw/master/2.jpg)

**任务日志**

![任务日志](https://gitee.com/wangyidao/schedule-manager/raw/master/3.jpg)

**增加任务**

![增加任务](https://gitee.com/wangyidao/schedule-manager/raw/master/4.jpg)


**更新日志**

2023-03-10

- 增加动态更新日志功能
- 增加sql语句执行任务
- 增加随机生成初始密码

2023-03-21

- 修改分页，增加显示总条数，可修改每页显示条数
