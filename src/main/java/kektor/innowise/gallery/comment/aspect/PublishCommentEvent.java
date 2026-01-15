package kektor.innowise.gallery.comment.aspect;



import kektor.innowise.gallery.comment.msg.CommentEventMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PublishCommentEvent {

    CommentEventMessage.EventType value();

}
