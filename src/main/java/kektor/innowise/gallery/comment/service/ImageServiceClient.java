package kektor.innowise.gallery.comment.service;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "/api/images")
public interface ImageServiceClient {

    @HttpExchange(url = "/{imageId}", method = "HEAD")
    ResponseEntity<Void> checkImageExists(@PathVariable Long imageId);

}
