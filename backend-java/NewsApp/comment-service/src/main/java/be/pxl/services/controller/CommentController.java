package be.pxl.services.controller;

import be.pxl.services.domain.dto.CommentResponse;
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
    private ICommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@Valid @RequestBody CreateCommentRequest commentRequest) {
        service.createComment(commentRequest);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments() {
        return ResponseEntity.ok(service.getAllComments());
    }

    @PutMapping("{commentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateComment(@PathVariable long commentId) {
        service.updateComment(commentId);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteComment(@PathVariable long commentId) {
        service.deleteComment(commentId);
    }
}
