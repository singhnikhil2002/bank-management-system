package com.bank.cms.kafka;

import lombok.extern.slf4j.Slf4j;
import com.bank.cms.kafka.TransactionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionConsumer {

    @KafkaListener(topics = "txn-events", groupId = "banking-group")
    public void consumeTransactionEvent(TransactionEvent event) {  // ✅ public
        log.info("📨 Transaction event received | from={} to={} amount={}",
                event.getFromAccount(),
                event.getToAccount(),
                event.getAmount());

        sendTransactionAlert(event);
    }

    private void sendTransactionAlert(TransactionEvent event) {
        log.info("📱 SMS Alert — ₹{} debited from account {}.",
                event.getAmount(),
                event.getFromAccount());

        log.info("📱 SMS Alert — ₹{} credited to account {}.",
                event.getAmount(),
                event.getToAccount());
    }
}