package kektor.innowise.gallery.comment.conf;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class BrokerConfig {

    @Value("${app.broker.comment-event-topic}")
    String commentEventTopicName;

    @Profile("!smoke")
    @Bean
    public NewTopic commentEventTopic() {
        return TopicBuilder.name(commentEventTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
