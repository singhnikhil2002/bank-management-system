package com.bank.cms.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private static final String TOPIC = "txn-events";

    public void publishTransactionEvent(TransactionEvent event) {

        // Use fromAccount as key — guarantees all events for same account
        // go to same partition (ordering guarantee per account)
        CompletableFuture<SendResult<String, TransactionEvent>> future =
                kafkaTemplate.send(TOPIC, event.getFromAccount(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Transaction event published | account={} amount={} partition={} offset={}",
                        event.getFromAccount(),
                        event.getAmount(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("❌ Failed to publish transaction event | account={} error={}",
                        event.getFromAccount(), ex.getMessage());
            }
        });
    }
}