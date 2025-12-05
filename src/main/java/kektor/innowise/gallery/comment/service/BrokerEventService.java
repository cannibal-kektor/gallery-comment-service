package kektor.innowise.gallery.comment.service;

import kektor.innowise.gallery.comment.msg.CommentEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerEventService {

    final KafkaTemplate<Long, CommentEventMessage> kafkaTemplate;

    @Value("${app.broker.comment-event-topic}")
    String commentEventTopicName;

    @Async
    @TransactionalEventListener(
            classes = CommentEventMessage.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void newCommentEvent(CommentEventMessage event) {
        kafkaTemplate.send(commentEventTopicName, event.imageId(), event)
                .whenComplete((_, error) -> {
                    if (error != null) {
                        log.error("Error while sending comment event", error);
                    }
                });
    }

}
