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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService service;

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@Valid @RequestBody CreateCommentRequest commentRequest, @PathVariable Long postId) {
        service.createComment(commentRequest, postId);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments() {
        return ResponseEntity.ok(service.getAllComments());
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateDto updateDto) {
        service.updateComment(commentId, updateDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteComment(@PathVariable Long commentId) {
        service.deleteComment(commentId);
    }
}
