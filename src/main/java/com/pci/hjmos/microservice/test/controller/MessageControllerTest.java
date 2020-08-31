package com.pci.hjmos.microservice.test.controller;

import com.pci.hjmos.framework.core.common.SimpleResult;
import com.pci.hjmos.framework.core.common.exception.MessageSendFailException;
import com.pci.hjmos.framework.core.message.api.MQCallback;
import com.pci.hjmos.framework.core.message.api.MessageConsumerService;
import com.pci.hjmos.framework.core.message.api.MessageListener;
import com.pci.hjmos.framework.core.message.api.MessageProducerService;
import com.pci.hjmos.framework.core.message.entity.MessageBody;
import com.pci.hjmos.framework.core.message.entity.MessageResult;
import com.pci.hjmos.framework.core.utils.LogUtils;
import com.pci.hjmos.framework.core.utils.MqUtils;
import com.pci.hjmos.microservice.test.bean.SendMsgEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zyting
 * @sinne 2020-03-13
 */
@RestController
public class MessageControllerTest {

    @GetMapping("/index")
    public String index(){
        return "sendMsgController";
    }

    @GetMapping("/sendSyncMsg")
    public SimpleResult sendSyncMsg(String topic,String tag, String content) throws Exception {

//        String topic = "pci";
        // 1、初始化一个消息发送实体对象
        List<SendMsgEntity> bodyList = new ArrayList<>();
        MessageBody msgBody = new MessageBody();
        for (int i = 0; i < 1; i++){
            SendMsgEntity msgEntity = new SendMsgEntity();
            msgEntity.setContent(content+"--"+i);
            bodyList.add(msgEntity);
        }
        msgBody.setData(bodyList);
        msgBody.setTimestamp(System.currentTimeMillis());
//        msgBody.setMsgId(1000L);   // 如果二次开发人员没有设置，则自动生成一个全局id

        // 获取生产者实例
        MessageProducerService msgProducer = MqUtils.getMsgProducerService();
        // 调用接口，发送同步消息
        SimpleResult simpleResult = msgProducer.sendSyncMsg(topic,tag, msgBody);

        return simpleResult;
    }

    @GetMapping("/sendAsyncMsg")
    public SimpleResult sendAsyncMsg(String topic,String tag, String content){
        SimpleResult simpleResult = new SimpleResult();
        try {
//            String topic  = "log";
            // 1、初始化消息发送实体对象
            MessageBody msgBody = new MessageBody();
            msgBody.setData(content);
            msgBody.setTimestamp(System.currentTimeMillis());
            msgBody.setMsgId(1000L);
            // 获取消息生产者实例
            MessageProducerService msgProducer = MqUtils.getMsgProducerService();
            // 调用异步发送接口
            msgProducer.sendAsyncMsg(topic,tag, msgBody, new MQCallback() {
                @Override
                public void onComplete(MessageResult result) {
                    // 4、接收到发送接口，处理回调函数
                    LogUtils.info("处理回调方法：" + result.toString());
                }
            });
            simpleResult.setSuccessMessage("发送异步消息成功");
            return simpleResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        simpleResult.setMessage(new MessageSendFailException("消息发送失败"));
        return simpleResult;
    }

    @GetMapping("/sendOnewayMsg")
    public SimpleResult sendOnewayMsg(String topic,String tag, String content){
        SimpleResult simpleResult = new SimpleResult();
        // 1、初始化发送消费实体对象，并设置消息内容
        MessageBody msgBody = new MessageBody();
        msgBody.setData(content);
        msgBody.setTimestamp(System.currentTimeMillis());
        msgBody.setMsgId(1000L);
        // 2、获取生产者实例
        MessageProducerService msgProducer = MqUtils.getMsgProducerService();
        // 3、调用接口，发送异步消息
        msgProducer.sendOneWayMsg(topic,tag, msgBody);

        simpleResult.setSuccessMessage("发送单向消息成功");
        return simpleResult;
    }


    @GetMapping("/initLogConsumer")
    public String initLogConsumer(){
        // 1、获取消费者实例对象
        MessageConsumerService mcs = MqUtils.getMsgConsumerService();
        // 2、调用消息监听接口
        mcs.addMessageListener(new MessageListener() {
            @Override
            public String getTopic() {
                // 消费者需要监听的主题
                return "log";
            }

            @Override
            public String getTag() {
                return null;
            }

            @Override
            public String getGroup() {
                // 消费者组名
                return "kafkaGroup";
            }
            @Override
            public void handle(String msg) {
                // 监听此主题消息,msg为接受的消息内容，接受消息后作业务处理
                LogUtils.info("msg服务接受消息存入数据库：" + msg);

//                LogUtils.info("msg服务接受消息存入数据库：" + msg.getData());
//                LogUtils.info("msg服务接受消息存入数据库：" + msg.getMsgId());
            }
        });
        return "log主题消费者初始化成功";
    }

    @GetMapping("/initHjLogConsumer")
    public String initHjLogConsumer(){
        // 1、获取消费者实例对象
        MessageConsumerService mcs = MqUtils.getMsgConsumerService();
        // 2、调用消息监听接口
        mcs.addMessageListener(new MessageListener() {
            @Override
            public String getTopic() {
                // 消费者需要监听的主题
                return "log";
            }

            @Override
            public String getTag() {
                return "asd,sdf";
            }

            @Override
            public String getGroup() {
                // 消费者组名
                return "kafkaGroup1";
            }
            @Override
            public void handle(String msg) {
                // 监听此主题消息,msg为接受的消息内容，接受消息后作业务处理
                LogUtils.info("msg服务接受消息存入数据库：" + msg);

//                LogUtils.info("msg服务接受消息存入数据库：" + msg.getData());
//                LogUtils.info("msg服务接受消息存入数据库：" + msg.getMsgId());
            }
        });
        return "log主题消费者初始化成功";
    }

    @GetMapping("/initPciConsumer")
    public String initPciConsumer(){

        MessageConsumerService mcs = MqUtils.getMsgConsumerService();
        mcs.addMessageListener(new MessageListener() {
            @Override
            public String getTopic() {
                return "pci";
            }

            @Override
            public String getTag() {
                return "asd,sdf";
            }

            @Override
            public String getGroup() {
                return "userGroup";
            }
            @Override
            public void handle(String msg) {
                // 监听此主题的消费实现类
                // msg 为消息内容
                LogUtils.info("msg服务接受消息存入数据库：" + msg);
            }
        });
        return "pci主题消费者初始化成功-->msg";
    }

    @GetMapping("/initPciConsumer2")
    public String initPciConsumer2(){

        MessageConsumerService mcs = MqUtils.getMsgConsumerService();
        mcs.addMessageListener(new MessageListener() {
            @Override
            public String getTopic() {
                return "pci";
            }

            @Override
            public String getTag() {
                return null;
            }

//            @Override
//            public boolean rocketmqbroadcast(){
//                return true;
//            }

            @Override
            public String getGroup() {
                return "userGroup2";
            }
            @Override
            public void handle(String msg) {
                // 监听此主题的消费实现类
                // msg 为消息内容
                LogUtils.info("msg服务接受消息存入数据库：" + msg);
            }
        });
        return "pci主题消费者初始化成功-->msg";
    }



    @GetMapping("/initMoreConsumer")
    public String initMoreConsumer(){

        List<Map<String,String>> cList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("topic", "asd");
        map.put("tag", "zxc");
        cList.add(map);
        map = new HashMap<>();
        map.put("topic", "qwe");
        map.put("tag", "zxc");
        cList.add(map);
        MessageConsumerService mcs = MqUtils.getMsgConsumerService();

        for(int i = 0; i < cList.size();i++){
            Map<String, String> topicMap = cList.get(i);
            mcs.addMessageListener(new MessageListener() {
                @Override
                public String getTopic() {
                    return topicMap.get("topic");
                }
                @Override
                public String getTag() {
                    return topicMap.get("tag");
                }
                @Override
                public String getGroup() {
//                    return "group_"+topicMap.get("topic");
                    return null;
                }
                @Override
                public void handle(String msg) {
                    System.out.println("接受到消息："+msg);
                }
            });
        }
        return "成功初始化多个消费者";
    }

}