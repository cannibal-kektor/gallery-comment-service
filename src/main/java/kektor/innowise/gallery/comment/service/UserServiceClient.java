package kektor.innowise.gallery.comment.service;

import kektor.innowise.gallery.comment.dto.UserDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Optional;


@HttpExchange(url = "/api/users", accept = MediaType.APPLICATION_JSON_VALUE)
public interface UserServiceClient {

    String USER_ID = "userId";

    @GetExchange("/id/{userId}")
    Optional<UserDto> fetchUser(@PathVariable(USER_ID) Long userId);

}
