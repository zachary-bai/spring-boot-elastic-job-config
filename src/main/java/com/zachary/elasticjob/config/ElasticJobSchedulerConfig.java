package com.zachary.elasticjob.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zachary.elasticjob.manager.ElasticJobSchedulerManager;
import com.zachary.elasticjob.model.Jobs;
import com.zachary.elasticjob.model.ZkConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zachary
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnExpression("${elastic-job.enabled}")
public class ElasticJobSchedulerConfig {

    @Bean
    @ConfigurationProperties(prefix = "elastic-job.reg-center")
    public ZkConfig zkConfig() {
        return new ZkConfig();
    }

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter regCenter(ZkConfig zkConfig) {
        ZookeeperConfiguration zk = new ZookeeperConfiguration(zkConfig.getServerLists(), zkConfig.getNamespace());
        zk.setSessionTimeoutMilliseconds(zkConfig.getSessionTimeoutMillis());
        zk.setConnectionTimeoutMilliseconds(zkConfig.getConnectionTimeoutMillis());
        zk.setDigest(zkConfig.getDigest());
        return new ZookeeperRegistryCenter(zk);
    }

    @Bean
    @ConfigurationProperties(prefix = "elastic-job.job-config")
    public Jobs jobs() {
        return new Jobs();
    }

    @Bean(initMethod = "init")
    public ElasticJobSchedulerManager springJobSchedulerManager(ZookeeperRegistryCenter regCenter, Jobs jobs) {
        return new ElasticJobSchedulerManager(regCenter, jobs);
    }


}
