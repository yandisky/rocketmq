package com.alibaba.rocketmq.store.stats;

import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.alibaba.rocketmq.common.constant.LoggerName;
import com.alibaba.rocketmq.common.stats.MomentStatsItemSet;
import com.alibaba.rocketmq.common.stats.StatsItem;
import com.alibaba.rocketmq.common.stats.StatsItemSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class BrokerStatsManager {

    public static final String TOPIC_PUT_NUMS = "TOPIC_PUT_NUMS";
    public static final String TOPIC_PUT_SIZE = "TOPIC_PUT_SIZE";
    public static final String GROUP_GET_NUMS = "GROUP_GET_NUMS";
    public static final String GROUP_GET_SIZE = "GROUP_GET_SIZE";
    public static final String SNDBCK_PUT_NUMS = "SNDBCK_PUT_NUMS";
    public static final String BROKER_PUT_NUMS = "BROKER_PUT_NUMS";
    public static final String BROKER_GET_NUMS = "BROKER_GET_NUMS";
    public static final String GROUP_GET_FROM_DISK_NUMS = "GROUP_GET_FROM_DISK_NUMS";
    public static final String GROUP_GET_FROM_DISK_SIZE = "GROUP_GET_FROM_DISK_SIZE";
    public static final String BROKER_GET_FROM_DISK_NUMS = "BROKER_GET_FROM_DISK_NUMS";
    public static final String BROKER_GET_FROM_DISK_SIZE = "BROKER_GET_FROM_DISK_SIZE";
    // For commercial
    public static final String COMMERCIAL_SEND_TIMES = "COMMERCIAL_SEND_TIMES";
    public static final String COMMERCIAL_SNDBCK_TIMES = "COMMERCIAL_SNDBCK_TIMES";
    public static final String COMMERCIAL_RCV_TIMES = "COMMERCIAL_RCV_TIMES";
    public static final String COMMERCIAL_RCV_EPOLLS = "COMMERCIAL_RCV_EPOLLS";
    public static final String COMMERCIAL_SEND_SIZE = "COMMERCIAL_SEND_SIZE";
    public static final String COMMERCIAL_RCV_SIZE = "COMMERCIAL_RCV_SIZE";
    public static final String COMMERCIAL_PERM_FAILURES = "COMMERCIAL_PERM_FAILURES";
    public static final String COMMERCIAL_OWNER = "Owner";
    // Message Size limit for one api-calling count.
    public static final double SIZE_PER_COUNT = 64 * 1024;

    // Pull Message Latency
    public static final String GROUP_GET_LATENCY = "GROUP_GET_LATENCY";

    /**
     * 读磁盘落后统计
     */
    public static final String GROUP_GET_FALL = "GROUP_GET_FALL";
    private static final Logger log = LoggerFactory.getLogger(LoggerName.RocketmqStatsLoggerName);
    private static final Logger commercialLog = LoggerFactory.getLogger(LoggerName.CommercialLoggerName);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
            "BrokerStatsThread"));
    private final ScheduledExecutorService commercialExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
            "CommercialStatsThread"));
    private final HashMap<String, StatsItemSet> statsTable = new HashMap<String, StatsItemSet>();
    private final String clusterName;

    private final MomentStatsItemSet momentStatsItemSet = new MomentStatsItemSet(GROUP_GET_FALL, scheduledExecutorService, log);

    public MomentStatsItemSet getMomentStatsItemSet() {
        return momentStatsItemSet;
    }

    public BrokerStatsManager(String clusterName) {
        this.clusterName = clusterName;

        this.statsTable.put(TOPIC_PUT_NUMS, new StatsItemSet(TOPIC_PUT_NUMS, this.scheduledExecutorService, log));
        this.statsTable.put(TOPIC_PUT_SIZE, new StatsItemSet(TOPIC_PUT_SIZE, this.scheduledExecutorService, log));
        this.statsTable.put(GROUP_GET_NUMS, new StatsItemSet(GROUP_GET_NUMS, this.scheduledExecutorService, log));
        this.statsTable.put(GROUP_GET_SIZE, new StatsItemSet(GROUP_GET_SIZE, this.scheduledExecutorService, log));
        this.statsTable.put(GROUP_GET_LATENCY, new StatsItemSet(GROUP_GET_LATENCY, this.scheduledExecutorService, log));
        this.statsTable.put(SNDBCK_PUT_NUMS, new StatsItemSet(SNDBCK_PUT_NUMS, this.scheduledExecutorService, log));
        this.statsTable.put(BROKER_PUT_NUMS, new StatsItemSet(BROKER_PUT_NUMS, this.scheduledExecutorService, log));
        this.statsTable.put(BROKER_GET_NUMS, new StatsItemSet(BROKER_GET_NUMS, this.scheduledExecutorService, log));
        this.statsTable.put(GROUP_GET_FROM_DISK_NUMS, new StatsItemSet(GROUP_GET_FROM_DISK_NUMS, this.scheduledExecutorService, log));
        this.statsTable.put(GROUP_GET_FROM_DISK_SIZE, new StatsItemSet(GROUP_GET_FROM_DISK_SIZE, this.scheduledExecutorService, log));
        this.statsTable.put(BROKER_GET_FROM_DISK_NUMS, new StatsItemSet(BROKER_GET_FROM_DISK_NUMS, this.scheduledExecutorService, log));
        this.statsTable.put(BROKER_GET_FROM_DISK_SIZE, new StatsItemSet(BROKER_GET_FROM_DISK_SIZE, this.scheduledExecutorService, log));

        //ONS商业化
        this.statsTable.put(COMMERCIAL_SEND_TIMES, new StatsItemSet(COMMERCIAL_SEND_TIMES, this.commercialExecutor, commercialLog));
        this.statsTable.put(COMMERCIAL_RCV_TIMES, new StatsItemSet(COMMERCIAL_RCV_TIMES, this.commercialExecutor, commercialLog));
        this.statsTable.put(COMMERCIAL_SEND_SIZE, new StatsItemSet(COMMERCIAL_SEND_SIZE, this.commercialExecutor, commercialLog));
        this.statsTable.put(COMMERCIAL_RCV_SIZE, new StatsItemSet(COMMERCIAL_RCV_SIZE, this.commercialExecutor, commercialLog));
        this.statsTable.put(COMMERCIAL_RCV_EPOLLS, new StatsItemSet(COMMERCIAL_RCV_EPOLLS, this.commercialExecutor, commercialLog));
        this.statsTable.put(COMMERCIAL_SNDBCK_TIMES, new StatsItemSet(COMMERCIAL_SNDBCK_TIMES, this.commercialExecutor, commercialLog));
        this.statsTable.put(COMMERCIAL_PERM_FAILURES, new StatsItemSet(COMMERCIAL_PERM_FAILURES, this.commercialExecutor, commercialLog));
    }

    public void start() {
    }

    public void shutdown() {
        this.scheduledExecutorService.shutdown();
    }

    public StatsItem getStatsItem(final String statsName, final String statsKey) {
        try {
            return this.statsTable.get(statsName).getStatsItem(statsKey);
        } catch (Exception e) {
        }

        return null;
    }

    public void incTopicPutNums(final String topic) {
        this.statsTable.get(TOPIC_PUT_NUMS).addValue(topic, 1, 1);
    }

    public void incTopicPutSize(final String topic, final int size) {
        this.statsTable.get(TOPIC_PUT_SIZE).addValue(topic, size, 1);
    }

    public void incGroupGetNums(final String group, final String topic, final int incValue) {
        final String statsKey = buildStatsKey(topic, group);
        this.statsTable.get(GROUP_GET_NUMS).addValue(statsKey, incValue, 1);
    }

    public String buildStatsKey(String topic, String group) {
        StringBuffer strBuilder = new StringBuffer();
        strBuilder.append(topic);
        strBuilder.append("@");
        strBuilder.append(group);
        return strBuilder.toString();
    }

    public void incGroupGetSize(final String group, final String topic, final int incValue) {
        final String statsKey = buildStatsKey(topic, group);
        this.statsTable.get(GROUP_GET_SIZE).addValue(statsKey, incValue, 1);
    }

    public void incGroupGetLatency(final String group, final String topic, final int queueId, final int incValue) {
        final String statsKey = String.format("%d@%s@%s", queueId, topic, group);
        this.statsTable.get(GROUP_GET_LATENCY).addValue(statsKey, incValue, 1);
    }


    public void incBrokerPutNums() {
        this.statsTable.get(BROKER_PUT_NUMS).getAndCreateStatsItem(this.clusterName).getValue().incrementAndGet();
    }


    public void incBrokerGetNums(final int incValue) {
        this.statsTable.get(BROKER_GET_NUMS).getAndCreateStatsItem(this.clusterName).getValue().addAndGet(incValue);
    }


    public void incSendBackNums(final String group, final String topic) {
        final String statsKey = buildStatsKey(topic, group);
        this.statsTable.get(SNDBCK_PUT_NUMS).addValue(statsKey, 1, 1);
    }


    public double tpsGroupGetNums(final String group, final String topic) {
        final String statsKey = buildStatsKey(topic, group);
        return this.statsTable.get(GROUP_GET_NUMS).getStatsDataInMinute(statsKey).getTps();
    }


    public void incBrokerGetFromDiskNums(final int incValue) {
        this.statsTable.get(BROKER_GET_FROM_DISK_NUMS).getAndCreateStatsItem(this.clusterName).getValue().addAndGet(incValue);
    }


    public void incGroupGetFromDiskSize(final String group, final String topic, final int incValue) {
        final String statsKey = buildStatsKey(topic, group);
        this.statsTable.get(GROUP_GET_FROM_DISK_SIZE).addValue(statsKey, incValue, 1);
    }


    public void incGroupGetFromDiskNums(final String group, final String topic, final int incValue) {
        final String statsKey = buildStatsKey(topic, group);
        this.statsTable.get(GROUP_GET_FROM_DISK_NUMS).addValue(statsKey, incValue, 1);
    }


    public void incBrokerGetFromDiskNums(final String group, final String topic, final int incValue) {
        final String statsKey = buildStatsKey(topic, group);
        this.statsTable.get(BROKER_GET_FROM_DISK_NUMS).addValue(statsKey, incValue, 1);
    }


    public void incBrokerGetFromDiskSize(final String group, final String topic, final int incValue) {
        final String statsKey = buildStatsKey(topic, group);
        this.statsTable.get(BROKER_GET_FROM_DISK_SIZE).addValue(statsKey, incValue, 1);
    }

    public void recordDiskFallBehind(final String group, final String topic, final int queueId, final long fallBehind) {
        final String statsKey = String.format("%d@%s@%s", queueId, topic, group);
        this.momentStatsItemSet.getAndCreateStatsItem(statsKey).getValue().set(fallBehind);
    }

    //ONS商业化
    public void incCommercialValue(final String key, final String owner, final String group,
                                   final String topic, final String type, final int incValue) {
        final String statsKey = buildCommercialStatsKey(owner, topic, group, type);
        this.statsTable.get(key).addValue(statsKey, incValue, 1);
    }

    public String buildCommercialStatsKey(String owner, String topic, String group, String type) {
        StringBuffer strBuilder = new StringBuffer();
        strBuilder.append(owner);
        strBuilder.append("@");
        strBuilder.append(topic);
        strBuilder.append("@");
        strBuilder.append(group);
        strBuilder.append("@");
        strBuilder.append(type);
        return strBuilder.toString();
    }


    public enum StatsType {
        SEND_SUCCESS,
        SEND_FAILURE,
        SEND_BACK,
        SEND_TIMER,
        SEND_TRANSACTION,
        RCV_SUCCESS,
        RCV_EPOLLS,
        PERM_FAILURE
    }
}
