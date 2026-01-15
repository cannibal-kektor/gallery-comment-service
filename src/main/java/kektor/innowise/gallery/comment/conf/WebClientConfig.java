package kektor.innowise.gallery.comment.conf;

import kektor.innowise.gallery.comment.service.ImageServiceClient;
import kektor.innowise.gallery.comment.service.UserServiceClient;
import kektor.innowise.gallery.security.conf.client.ProtectedImageServiceClient;
import kektor.innowise.gallery.security.conf.client.ProtectedUserServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Bean
    public ImageServiceClient imageServiceClient(@ProtectedImageServiceClient RestClient imageRestClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(imageRestClient))
                .build()
                .createClient(ImageServiceClient.class);
    }

    @Bean
    public UserServiceClient userServiceClient(@ProtectedUserServiceClient RestClient userRestClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(userRestClient))
                .build()
                .createClient(UserServiceClient.class);
    }

}
