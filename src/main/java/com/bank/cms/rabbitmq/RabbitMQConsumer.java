package com.bank.cms.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.otp}")
    public void consumeOtp(OtpMessage message) {
        log.info("📨 OTP received from queue | mobile={} purpose={}",
                message.getMobileNumber(), message.getPurpose());

        // In production → integrate Twilio/AWS SNS to send real SMS
        log.info("📱 Sending OTP {} to mobile {} for {}",
                message.getOtp(),
                message.getMobileNumber(),
                message.getPurpose());
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void consumeNotification(NotificationMessage message) {
        log.info("📨 Notification received from queue | account={} type={}",
                message.getAccountNumber(), message.getType());

        // In production → integrate SendGrid/AWS SES to send real email
        log.info("📧 Email to {} — Dear customer, ₹{} {} in account {}. {}",
                message.getEmail(),
                message.getAmount(),
                message.getType().equals("DEBIT") ? "debited" : "credited",
                message.getAccountNumber(),
                message.getMessage());
    }
}