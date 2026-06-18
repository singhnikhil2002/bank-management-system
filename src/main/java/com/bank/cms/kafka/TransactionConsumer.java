package com.bank.cms.kafka;

import com.bank.cms.rabbitmq.NotificationMessage;
import com.bank.cms.rabbitmq.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bank.cms.kafka.TransactionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionConsumer {

    private final RabbitMQProducer rabbitMQProducer;   // ✅ inject this

    @KafkaListener(topics = "txn-events", groupId = "banking-group")
    public void consumeTransactionEvent(TransactionEvent event) {
        log.info("📨 Transaction event received | from={} to={} amount={}",
                event.getFromAccount(), event.getToAccount(), event.getAmount());

        // Push debit notification to RabbitMQ
        rabbitMQProducer.sendNotification(new NotificationMessage(
                event.getFromAccount(),
                "sender@bank.com",        // in production: fetch from DB
                "9876543210",
                event.getAmount(),
                "DEBIT",
                "Transaction successful. Ref: " + event.getEventId()
        ));

        // Push credit notification to RabbitMQ
        rabbitMQProducer.sendNotification(new NotificationMessage(
                event.getToAccount(),
                "receiver@bank.com",      // in production: fetch from DB
                "9876543210",
                event.getAmount(),
                "CREDIT",
                "Amount received. Ref: " + event.getEventId()
        ));
    }
}