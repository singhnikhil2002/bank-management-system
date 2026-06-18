package com.bank.cms.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing.otp}")
    private String otpRoutingKey;

    @Value("${rabbitmq.routing.notification}")
    private String notificationRoutingKey;

    public void sendOtp(OtpMessage message) {
        log.info("📤 Sending OTP to queue | mobile={} purpose={}",
                message.getMobileNumber(), message.getPurpose());
        rabbitTemplate.convertAndSend(exchange, otpRoutingKey, message);
    }

    public void sendNotification(NotificationMessage message) {
        log.info("📤 Sending notification to queue | account={} type={}",
                message.getAccountNumber(), message.getType());
        rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, message);
    }
}