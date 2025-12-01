package be.pxl.services.client;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "post-service")
public interface PostClient {
    @GetMapping("/api/post/editor?status=PENDING")
    List<PostResponse> getPendingPosts(@RequestHeader("X-Role") String role);

    @PutMapping("/api/post/{postId}/status")
    void updatePostStatus(@PathVariable long postId, @RequestBody PostStatusRequest statusRequest);
}
