package be.pxl.services.controller;

import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CommentUpdateDto;
import be.pxl.services.domain.dto.CreateCommentRequest;
import be.pxl.services.services.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService service;

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @Valid @RequestBody CreateCommentRequest commentRequest,
            @PathVariable Long postId,
            @RequestHeader("X-User") String author,
            @RequestHeader("X-Role") String role
    ) {
        return service.createComment(commentRequest, postId , author, role);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments() {
        return ResponseEntity.ok(service.getAllComments());
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateDto updateDto,
            @RequestHeader("X-User") String author
    ) {
        return service.updateComment(commentId, updateDto, author);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("X-User") String author
    ) {
        service.deleteComment(commentId, author);
    }
}
