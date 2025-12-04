package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostStatusRequest;
import be.pxl.services.services.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Slf4j
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
            log.warn("Unauthorized access attempt to editor endpoint. Role provided: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(postService.getAllPostsForEditor(status));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(
            @Valid @RequestBody PostRequest postRequest,
            @RequestHeader("X-User") String author
    ) {
        log.debug("Received request to create post from user: {}", author);

        postRequest.setAuthor(author);

        postService.addNewPost(postRequest);
    }

    @PutMapping("/{postId}")
    public void editPost(@RequestBody PostEditDto postEditDto, @PathVariable Long postId) {
        postService.editPost(postEditDto, postId);
    }

    @PutMapping("/{postId}/status")
    public ResponseEntity<Void> updatePostStatus(
            @PathVariable Long postId,
            @RequestBody PostStatusRequest statusRequest)
    {
        postService.updatePostStatus(postId, statusRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }
}
