package kektor.innowise.gallery.comment.aspect;


import kektor.innowise.gallery.comment.dto.CommentDto;
import kektor.innowise.gallery.comment.mapper.CommentMapper;
import kektor.innowise.gallery.comment.msg.CommentEventMessage;
import kektor.innowise.gallery.comment.service.SecurityService;
import kektor.innowise.gallery.security.UserPrincipal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Aspect
@Order(LOWEST_PRECEDENCE)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublishingEventsAspect {

    ApplicationEventPublisher eventPublisher;
    SecurityService securityService;
    CommentMapper mapper;

    @Pointcut("within(kektor.innowise.gallery.comment.service.*)")
    public void inService() {
    }

    @Pointcut("inService() && @annotation(event)")
    public void commentEventServiceTriggerMethod(PublishCommentEvent event) {
    }

    @AfterReturning(
            pointcut = "commentEventServiceTriggerMethod(event)",
            returning = "comment",
            argNames = "comment,event"
    )
    public void commentEventAdvice(CommentDto comment, PublishCommentEvent event) {
        UserPrincipal user = securityService.currentUser();
        CommentEventMessage eventMessage = mapper.toEvent(comment, user, event.value());
        eventPublisher.publishEvent(eventMessage);
    }

}