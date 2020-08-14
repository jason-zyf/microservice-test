package com.pci.hjmos.microservice.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.protocol.body.ConsumerConnection;
import org.apache.rocketmq.common.protocol.body.GroupList;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;

import java.util.HashSet;

/**
 * @author zyting
 * @sinne 2020-08-14
 * 判断rocketmq是否有消费者在订阅消息
 *
 *  Set<String> aa = defaultMQAdminExt.getClusterList("AA");
 *  TopicConfig topicConfig = defaultMQAdminExt.examineTopicConfig("172.23.125.15:9876", "AA");
 *  TopicRouteData topicRouteData = defaultMQAdminExt.examineTopicRouteInfo("AA");
 *  TopicStatsTable topicStatsTable = defaultMQAdminExt.examineTopicStats("AA");
 *  Set<String> topicClusterList = defaultMQAdminExt.getTopicClusterList("AA");
 *
 */
@Slf4j
public class AdminExtSample {

    public static void main(String[] args) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        try {
            defaultMQAdminExt.setNamesrvAddr("172.23.125.15:9876");
            defaultMQAdminExt.start();

            // 1、先获取主题下所有的消费者组名
            GroupList list = defaultMQAdminExt.queryTopicConsumeByWho("AA");
            HashSet<String> groupList = list.getGroupList();

            // 2、遍历每个消费者名称连接情况
            int num = 0;
            for(String groupName : groupList){
                try {
                    ConsumerConnection connection = defaultMQAdminExt.examineConsumerConnectionInfo(groupName);
                    num++;
                }catch (Exception e){
                    // 可以不往外抛异常，只日志记录异常信息；
                    // e.printStackTrace();
                    log.error("消费者组["+groupName+"]连接异常："+e.getMessage());
                }
            }
            if(num > 0){
                System.out.println("有消费者在订阅。。。。");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            defaultMQAdminExt.shutdown();
        }
    }

}
