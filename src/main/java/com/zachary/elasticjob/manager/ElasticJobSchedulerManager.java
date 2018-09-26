package com.zachary.elasticjob.manager;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.JobType;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.script.ScriptJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zachary.elasticjob.model.JobConfig;
import com.zachary.elasticjob.model.Jobs;
import com.zachary.elasticjob.util.ClassUtil;
import com.zachary.elasticjob.util.SpringContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author zachary
 */
public class ElasticJobSchedulerManager {

    private final ZookeeperRegistryCenter regCenter;
    private final Jobs jobs;

    public ElasticJobSchedulerManager(ZookeeperRegistryCenter regCenter, Jobs jobs) {
        this.regCenter = regCenter;
        this.jobs = jobs;
    }

    public void init() throws Exception {
        if (jobs == null || CollectionUtils.isEmpty(jobs.getJobs())) {
            throw new IllegalArgumentException("----- has no jobs");
        }
        Iterator<JobConfig> iterator = jobs.getJobs().iterator();
        while (iterator.hasNext()) {
            initScheduler(iterator.next(), regCenter);
        }
    }

    @SuppressWarnings("unchecked")
    private void initScheduler(JobConfig jobConfig, ZookeeperRegistryCenter regCenter) throws Exception {

        Class jobClass = Class.forName(jobConfig.getJobClass());
        ElasticJob jobInstance = (ElasticJob) SpringContextHolder.getBean(jobClass);
        // 如果再容器中没有找到bean，则手动创建它的一个实例
        if (jobInstance == null) {
            jobInstance = (ElasticJob) ClassUtil.getInstance(jobClass);
        }
        JobType jobType = null;

        Set<Class> interfaces = ClassUtil.getAllParentInterfaces(jobClass);
        if (interfaces != null && interfaces.size() > 0) {
            for (Class each : interfaces) {
                if (SimpleJob.class.equals(each)) {
                    jobType = JobType.SIMPLE;
                    break;
                } else if (DataflowJob.class.equals(each)) {
                    jobType = JobType.DATAFLOW;
                    break;
                } else if (ScriptJob.class.equals(each)) {
                    jobType = JobType.SCRIPT;
                    break;
                }
            }
        }

        List<ElasticJobListener> jobListeners = new ArrayList<>();
        List<String> listeners = jobConfig.getListeners();
        if (!CollectionUtils.isEmpty(listeners)) {
            for (String listener : listeners) {
                ElasticJobListener jobListener = instanceOfListener(listener);
                jobListeners.add(jobListener);
            }
        }
        ElasticJobListener[] jobListenersArr = new ElasticJobListener[jobListeners.size()];
        jobListeners.toArray(jobListenersArr);

        switch (jobType) {
            case SIMPLE:
                new SpringJobScheduler(jobInstance, regCenter, getSimpleJobConfiguration(jobClass, jobConfig.getCron(),
                        jobConfig.getShardingTotalCount(), jobConfig.getShardingItemParameters(), jobConfig.isDisabled()), jobListenersArr).init();
                break;
            case DATAFLOW:
                new SpringJobScheduler(jobInstance, regCenter, getDataFlowJobConfiguration(jobClass, jobConfig.getCron(),
                        jobConfig.getShardingTotalCount(), jobConfig.getShardingItemParameters(), jobConfig.isStreamingProcess(), jobConfig.isDisabled()), jobListenersArr).init();
                break;
            case SCRIPT:
                new SpringJobScheduler(jobInstance, regCenter, getScriptJobConfiguration(jobClass, jobConfig.getCron(),
                        jobConfig.getShardingTotalCount(), jobConfig.getShardingItemParameters(), jobConfig.getScriptCommandLine(), jobConfig.isDisabled()), jobListenersArr).init();
                break;
            default:
                throw new IllegalArgumentException("----- not find jobType for jobClass [" + jobConfig.getJobClass() + "]");
        }
    }

    /**
     * SimpleJob Config
     *
     * @param jobClass
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     * @return
     */
    private LiteJobConfiguration getSimpleJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
                                                           final int shardingTotalCount, final String shardingItemParameters, boolean disabled) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder(
                jobClass.getSimpleName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(),
                jobClass.getCanonicalName())).disabled(disabled).overwrite(true).build();
    }

    /**
     * DataFlowJob Config
     *
     * @param jobClass
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     * @param streamingProcess
     * @return
     */
    private LiteJobConfiguration getDataFlowJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
                                                             final int shardingTotalCount, final String shardingItemParameters,
                                                             final boolean streamingProcess, boolean disabled) {
        return LiteJobConfiguration.newBuilder(new DataflowJobConfiguration(JobCoreConfiguration.newBuilder(
                jobClass.getSimpleName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(),
                jobClass.getCanonicalName(), streamingProcess)).disabled(disabled).overwrite(true).build();
    }

    /**
     * ScriptJob Config
     *
     * @param jobClass
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     * @param scriptCommandLine
     * @return
     */
    private LiteJobConfiguration getScriptJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
                                                           final int shardingTotalCount, final String shardingItemParameters,
                                                           final String scriptCommandLine, boolean disabled) {
        return LiteJobConfiguration.newBuilder(new ScriptJobConfiguration(JobCoreConfiguration.newBuilder(
                jobClass.getSimpleName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(),
                scriptCommandLine)).disabled(disabled).overwrite(true).build();
    }


    /**
     * 获取listener实例
     *
     * @param listener
     * @return
     * @throws Exception
     */
    private ElasticJobListener instanceOfListener(String listener) throws Exception {
        if (StringUtils.isEmpty(listener)) {
            return null;
        }
        Class listenerClass = Class.forName(listener);
        Set<Class> interfaces = ClassUtil.getAllParentInterfaces(listenerClass);
        if (!interfaces.contains(ElasticJobListener.class)) {
            throw new ClassCastException("listener [" + listenerClass.getName() + "] should implements [com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener]");
        }
        ElasticJobListener listenerInstance = (ElasticJobListener) SpringContextHolder.getBean(listenerClass);
        // 如果再容器中没有找到bean，则手动创建它的一个实例
        if (listenerInstance == null) {
            listenerInstance = (ElasticJobListener) ClassUtil.getInstance(listenerClass);
        }
        return listenerInstance;
    }

}
