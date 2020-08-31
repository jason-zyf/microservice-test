package com.pci.hjmos.microservice.test.controller;

import com.pci.hjmos.framework.core.message.api.MessageExtService;
import com.pci.hjmos.framework.core.utils.LogUtils;
import com.pci.hjmos.framework.core.utils.MqUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.body.ConsumerConnection;
import org.apache.rocketmq.common.protocol.body.GroupList;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zyting
 * @sinne 2020-08-31
 * topic 是否有在线订阅消费者
 */
@RestController
public class TopicConsumerTest {

    private static boolean kafkaFlag;
    private static boolean rmqConnectFlag;
    private DefaultMQAdminExt defaultSelfMQAdminExt;

    @PostConstruct
    public void init() throws MQClientException {
        try {
            defaultSelfMQAdminExt = new DefaultMQAdminExt("selfAdminExt");
            defaultSelfMQAdminExt.setNamesrvAddr("10.38.2.12:30076");  // 172.23.125.15:9876
            defaultSelfMQAdminExt.start();
            System.out.println("DefaultMQAdminExt 初始化成功。。。。。");
            ClusterInfo clusterInfo = defaultSelfMQAdminExt.examineBrokerClusterInfo();
            rmqConnectFlag = true;
        }catch (Exception e){
            e.printStackTrace();
            rmqConnectFlag = false;
//            LogUtils.error("connect to rocketmq cluster failed:  "+rmqConnectFlag);
            System.out.println("connect to rocketmq cluster failed:  "+rmqConnectFlag);
        }

    }


    @PreDestroy
    public void destroy(){
        defaultSelfMQAdminExt.shutdown();
        System.out.println("defaultMQAdminExt关闭成功。。。。。。");
    }

    /**
     * 根据topic获取消费者组名集合
     * @param topic  主题
     * @return   返回消费者组名集合
     */
    @GetMapping("/findKafkaConsumerGroup")
    public String findConsumerGroup(String topic) {

        String brokerServers = "172.23.125.15:9092";
//        topic="plc";
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerServers);

        try (AdminClient client = AdminClient.create(props)) {
            List<String> allGroups = client.listConsumerGroups()
                    .valid()
                    .get(10, TimeUnit.SECONDS)
                    .stream()
                    .map(ConsumerGroupListing::groupId)
                    .collect(Collectors.toList());

            Map<String, ConsumerGroupDescription> allGroupDetails =
                    client.describeConsumerGroups(allGroups).all().get(10, TimeUnit.SECONDS);

            final List<String> filteredGroups = new ArrayList<>();
            allGroupDetails.entrySet().forEach(entry -> {
                String groupId = entry.getKey();
                ConsumerGroupDescription description = entry.getValue();
                System.out.println("description:"+description);
                boolean topicSubscribed = description.members().stream().map(MemberDescription::assignment)
                        .map(MemberAssignment::topicPartitions)
                        .map(tps -> tps.stream().map(TopicPartition::topic).collect(Collectors.toSet()))
                        .anyMatch(tps -> tps.contains(topic));
                if (topicSubscribed)
                    filteredGroups.add(groupId);
            });
            return filteredGroups.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/monitorKafka")
    public void monitorKafka(){
        if(!kafkaFlag){
            // 如果连接不上往下走就没有意义；
            System.out.println("连接不上kafka服务器");
            return ;
        }
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "172.23.125.15:9093");
        properties.put("connections.max.idle.ms", 10000);
        properties.put("request.timeout.ms", 30000);
        try (AdminClient client = KafkaAdminClient.create(properties)) {
            ListTopicsResult topics = client.listTopics();
            Set<String> names = topics.names().get();
            System.out.println("connect to kafka cluster success");
            if (names.isEmpty()) {

            }
        }catch (InterruptedException | ExecutionException e){
            // Kafka is not available
            System.out.println("connect to kafka cluster failed");
        }
    }

    /**
     * rocketmq 根据主题查看是否有消费者在线订阅
     * @param topic
     * @return
     */
    @GetMapping("/findrmqConsumerGroup")
    public String findrmqConsumerGroup(String topic){

//        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt("findrmqConsumer");
        String msg = "此主题没有消费者在线订阅。。。。。";
        int num = 0;
        if(rmqConnectFlag){
            try {
//            defaultMQAdminExt.setNamesrvAddr("172.23.125.15:9876");
//            defaultMQAdminExt.start();

                // 1、先获取主题下所有的消费者组名
                GroupList list = defaultSelfMQAdminExt.queryTopicConsumeByWho(topic);  // 如果主题不存在则会抛出异常
                HashSet<String> groupList = list.getGroupList();

                // 2、遍历每个消费者名称连接情况
                for(String groupName : groupList){
                    try {
                        ConsumerConnection connection = defaultSelfMQAdminExt.examineConsumerConnectionInfo(groupName);
                        num++;
                    }catch (Exception e){
                        // 可以不往外抛异常，只日志记录异常信息；
                        // e.printStackTrace();
                        LogUtils.error("消费者组["+groupName+"]连接异常："+e.getMessage());
                    }
                }
                if(num > 0){
                    msg = "有消费者在线订阅此主题。。。。。";
                }
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.error(topic+"主题获取消费者组集合失败:"+e.getMessage());
            }finally {
                defaultSelfMQAdminExt.shutdown();
            }
        }
        return msg;
    }

    /**
     * 判断是否有消费者在线订阅 topic主题，kafka的断掉后会有几秒的延迟时间
     * @param topic 主题
     * @return  true 有消费者  false 无消费者
     */
    @GetMapping("online-subscription")
    public boolean onlineSubscription(String topic){
        // 1、rocketmq是否有订阅
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        HashSet<String> groupList = new HashSet<>();
        int num = 0;
        try {
            defaultMQAdminExt.setNamesrvAddr("172.23.125.15:9876");
            defaultMQAdminExt.start();

            // 1.1、先获取主题下所有的消费者组名
            try {
                GroupList list = defaultMQAdminExt.queryTopicConsumeByWho(topic);
                groupList = list.getGroupList();
            }catch (Exception e){
                LogUtils.error(topic+"主题获取消费者组集合失败");
            }

            // 1.2、遍历每个消费者名称连接情况
            for(String groupName : groupList){
                try {
                    ConsumerConnection connection = defaultMQAdminExt.examineConsumerConnectionInfo(groupName);
                    num++;
                }catch (Exception e){
                    LogUtils.error("消费者组["+groupName+"]连接异常："+e.getMessage());
                }
            }

            // 2、检查kafka是否有订阅
            final List<String> filteredGroups = new ArrayList<>();
            if(kafkaFlag){
                String brokerServers = "172.23.125.15:9092";
                Properties props = new Properties();
                props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerServers);

                try (AdminClient client = AdminClient.create(props)) {
                    List<String> allGroups = client.listConsumerGroups()
                            .valid()
                            .get(10, TimeUnit.SECONDS)
                            .stream()
                            .map(ConsumerGroupListing::groupId)
                            .collect(Collectors.toList());

                    Map<String, ConsumerGroupDescription> allGroupDetails =
                            client.describeConsumerGroups(allGroups).all().get(10, TimeUnit.SECONDS);

                    allGroupDetails.entrySet().forEach(entry -> {
                        String groupId = entry.getKey();
                        ConsumerGroupDescription description = entry.getValue();
                        boolean topicSubscribed = description.members().stream().map(MemberDescription::assignment)
                                .map(MemberAssignment::topicPartitions)
                                .map(tps -> tps.stream().map(TopicPartition::topic).collect(Collectors.toSet()))
                                .anyMatch(tps -> tps.contains(topic));
                        if (topicSubscribed)
                            filteredGroups.add(groupId);
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            // 3、判断kafka或rocketmq是否有订阅
            if(num > 0 || !filteredGroups.isEmpty()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            defaultMQAdminExt.shutdown();
        }
        return false;
    }

    /**
     * 通过封装调用
     * @param topic
     * @return
     */
    @GetMapping("isOblineSub")
    public boolean isOblineSub(String topic){

        MessageExtService extService = MqUtils.getMessageExtService();
        return extService.isOnlineSub(topic);
    }




}
