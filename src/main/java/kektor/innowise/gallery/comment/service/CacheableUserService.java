package kektor.innowise.gallery.comment.service;

import kektor.innowise.gallery.comment.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static kektor.innowise.gallery.comment.conf.CacheConfig.USERS_CACHE_LOCAL;
import static kektor.innowise.gallery.comment.conf.CacheConfig.USERS_CACHE_REMOTE;

@Service
@Primary
@RequiredArgsConstructor
@CacheConfig(cacheNames = {USERS_CACHE_LOCAL, USERS_CACHE_REMOTE})
public class CacheableUserService implements UserServiceClient {

    private final UserServiceClient userServiceClient;

    @Override
    @Cacheable(key = "'by_id:' + #userId")
    public Optional<UserDto> fetchUser(Long userId) {
        return userServiceClient.fetchUser(userId);
    }
}
