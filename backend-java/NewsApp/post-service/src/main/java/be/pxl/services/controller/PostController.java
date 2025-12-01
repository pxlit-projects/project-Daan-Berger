package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostStatusRequest;
import be.pxl.services.services.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

    @GetMapping
    public List<PostResponse> getPublishedPosts(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String date
    ) {
        return postService.getPublishedPosts(content, author, date);
    }

    @GetMapping("/editor")
    public ResponseEntity<List<PostResponse>> getPostsForEditor(
            @RequestHeader("X-Role") String role,
            @RequestParam(required = false) String status
    ) {
        if (!"editor".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(postService.getAllPostsForEditor(status));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@Valid @RequestBody PostRequest postRequest) {
        postService.addNewPost(postRequest);
    }

    @PutMapping("/{postId}")
    public void editPost(@RequestBody PostEditDto postEditDto, @PathVariable long postId) {
        postService.editPost(postEditDto, postId);
    }

    @PutMapping("/{postId}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePostStatus(
            @PathVariable long postId,
            @RequestBody PostStatusRequest statusRequest)
    {
        postService.updatePostStatus(postId, statusRequest);
    }


}
