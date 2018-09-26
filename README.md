> ## spring-cloud-elastic-job-config
> 2018-09-26 | Zachary Bai | `spring-boot-autoconfigure` 与 [`elastic-job`](http://elasticjob.io/) 的整合，简化 `elastic-job` 的配置

---
### 环境依赖

  1. 需要依赖 **zookeeper** 集群，集群搭建请自行 Google
  2. `spring-boot-autoconfigure` 版本 `2.0.3.RELEASE`
  3. `elastic-job-lite-core`，`elastic-job-lite-spring` 版本 `2.1.5`

---
### 使用方法
  1. 下载此项目，打成 **jar** 包，deploy 到自己的 maven 私服
  2. 在 maven `pom.xml` 文件中引入相应依赖：
```xml
    <dependency>
        <groupId>com.zachary.elasticjob</groupId>
        <artifactId>spring-boot-elastic-job-config</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```
  3. 需要使用分布式定时任务的 ***Job*** 请实现
`com.dangdang.ddframe.job.api.ElasticJob` 的子接口 `SimpleJob`, `ScriptJob` 或 `DataflowJob<T>`

  若需要为 ***Job*** 添加 listener，请实现 `com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener` 接口

---
### 配置参考
请在 **spring-boot** 项目的配置文件 `application.yml` 中按照如下方式配置：

    # 分布式分片定时任务配置
    elastic-job:
      enabled: true  # 是否启用 elastic-job
      reg-center:
        serverLists: zk1.com:2181,zk2.com:2181,zk3.com:2181
        namespace: test-name-space
        sessionTimeoutMillis: 30000  # 会话超时【可选】
        connectionTimeoutMillis: 30000  # 连接超时【可选】
        digest: ××××××  # 权限令牌【可选；无，则表示不需要认证】
      job-config:
        jobs:
          - jobClass: com.zachary.elasticjob.job.JobExampleOne  # 实现 ElasticJob 子接口的定时任务
            cron: 0 0/2 * * * ? #从0分钟开始，每2分钟执行一次
            shardingTotalCount: 3  # 分片数量
            shardingItemParameters: 0=×××,1=×××,2=×××  # 每个分片的传入参数【可选】
            listeners:  # Job 监听器【可选】
              - com.zachary.elasticjob.job.listener.ListenerExampleOne
              - com.zachary.elasticjob.job.listener.ListenerExampleTwo
              - com.zachary.elasticjob.job.listener.ListenerExampleThree
            disabled: false  # 是否禁用当前分布式定任务，默认值 false【可选】
          - jobClass: com.zachary.elasticjob.job.JobExampleTwo
            cron: 0 4/5 * * * ? #从4分钟开始，每5分钟执行一次
            shardingTotalCount: 1
            shardingItemParameters: 0=Default
            disabled: true
          - jobClass: com.zachary.elasticjob.job.JobExampleThree
            cron: 0 2/5 * * * ? #从2分钟开始，每5分钟执行一次
            shardingTotalCount: 1
            shardingItemParameters: 0=Default
