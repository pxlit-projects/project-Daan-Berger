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
            log.warn("Unauthorized access attempt to getPostsForEditor endpoint. Role provided: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(postService.getAllPostsForEditor(status));
    }

    @PostMapping
    public ResponseEntity<Void> createPost(
            @Valid @RequestBody PostRequest postRequest,
            @RequestHeader("X-Role") String role,
            @RequestHeader("X-User") String author
    ) {
        if (!"editor".equalsIgnoreCase(role)) {
            log.warn("Unauthorized access attempt to createPost endpoint. Role provided: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.debug("Received request to create post from user: {}", author);
        postService.addNewPost(postRequest, author);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> editPost(
            @RequestBody PostEditDto postEditDto,
            @PathVariable Long postId,
            @RequestHeader("X-Role") String role

    ) {
        if (!"editor".equalsIgnoreCase(role)) {
            log.warn("Unauthorized access attempt to editPost endpoint. Role provided: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postService.editPost(postEditDto, postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/status")
    public ResponseEntity<Void> updatePostStatus(
            @PathVariable Long postId,
            @RequestBody PostStatusRequest statusRequest,
            @RequestHeader("X-Role") String role
    ) {
        if (!"editor".equalsIgnoreCase(role)) {
            log.warn("Unauthorized access attempt to updatePostStatus endpoint. Role provided: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postService.updatePostStatus(postId, statusRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }
}
