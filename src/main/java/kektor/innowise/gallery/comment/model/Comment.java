package kektor.innowise.gallery.comment.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "comments", schema = "gallery")
public class Comment {
    @Id
    Long id;

    @Column("user_id")
    Long userId;

    @Column("image_id")
    Long imageId;

    String content;

    @Column("created_at")
    Instant createdAt = Instant.now();

}
