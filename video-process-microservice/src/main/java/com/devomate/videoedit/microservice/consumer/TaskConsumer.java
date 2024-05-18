package com.devomate.videoedit.microservice.consumer;

import com.devomate.videoedit.microservice.service.VideoProcessingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskConsumer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private VideoProcessingService videoProcessingService;

    @Autowired
    private String exchangeName;

    @Autowired
    private String answersQueueName;

    @Autowired
    private String answersRoutingKey;

    private void sendAnswer(String response) {
        rabbitTemplate.convertAndSend(exchangeName, answersRoutingKey, response);
    }

    @RabbitListener(queues = "answersQueueName", containerFactory = "rabbitListenerContainerFactory")
    public void receiveTask(String message) {
        videoProcessingService.processVideo(message);
        String response = "Processed task: " + message;
        sendAnswer(response);
    }
}
