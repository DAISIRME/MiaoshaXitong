package org.springboot.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springboot.mapper.KillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author toutou
 * 监听服务器上的kafka是否有相关的消息发过来
 * @date by 2019/08
 */
@Component
public class ConsumerDemo {
    /**
     * 定义此消费者接收topics = "demo"的消息，与controller中的topic对应上即可
     * @param record 变量代表消息本身，可以通过ConsumerRecord<?,?>类型的record变量来打印接收的消息的各种信息
     */
    @Autowired
    private KillMapper killMapper;
    @KafkaHandler
    public void k(){
    }
    @KafkaListener(topics = "demo")
    public void listen(ConsumerRecord<?, ?> record){
        System.out.printf("topic is %s, offset is %d, value is %s \n", record.topic(), record.offset(), record.value());
        String record1= (String) record.value();
        if (record1.startsWith("order"))
        {
            killMapper.Order(Integer.parseInt(record1.substring(5,record1.length())));
        }
        else if(record1.startsWith(""))
        {

        }
}}