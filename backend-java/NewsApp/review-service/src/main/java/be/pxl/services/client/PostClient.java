package be.pxl.services.client;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

@FeignClient(name = "post-service", url = "http://localhost:8081")
public interface PostClient {
    List<PostResponse> getPendingPosts(String role);
    void updatePostStatus(Long post, PostStatusRequest statusRequest);
}
