package kektor.innowise.gallery.comment.msg;


import lombok.Builder;

import java.time.Instant;

@Builder
public record CommentEventMessage(EventType eventType,
                                  Long imageId,
                                  Long commentId,
                                  Long userId,
                                  String username,
                                  String comment,
                                  Instant instant) {

    public enum EventType {
        ADD_COMMENT,
        UPDATE_COMMENT,
        REMOVE_COMMENT
    }

}
