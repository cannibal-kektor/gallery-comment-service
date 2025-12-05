package kektor.innowise.gallery.comment.service;

import kektor.innowise.gallery.comment.msg.CommentEventMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrokerEventService {

    RetryTemplate retryTemplate;
    KafkaTemplate<Long, CommentEventMessage> kafkaTemplate;

    @NonFinal
    @Value("${app.broker.comment-event-topic}")
    String commentEventTopicName;

    @Async
    @TransactionalEventListener(
            classes = CommentEventMessage.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void newCommentEvent(CommentEventMessage event) {
        retryTemplate.execute(_ -> kafkaTemplate.send(commentEventTopicName, event.imageId(), event).join(),
                context -> {
                    log.error("All retry attempts failed for sending comment event. CommentId: {} ImageId: {} UserId: {}",
                            event.commentId(), event.imageId(), event.userId(), context.getLastThrowable());
                    return null;
                });
    }

}
