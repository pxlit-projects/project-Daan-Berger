package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.RejectRequest;

import be.pxl.services.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/pending")
    public ResponseEntity<List<PostResponse>> getPendingPosts(@RequestHeader("X-Role") String role) {
        if (!"editor".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reviewService.getPendingPosts(role));
    }

    @PostMapping("/{postId}/approve")
    public ResponseEntity<Void> approvePost(
            @PathVariable Long postId,
            @RequestHeader("X-Role") String role
    ) {
        if (!"editor".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reviewService.approvePost(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/reject")
    public ResponseEntity<Void> rejectPost(
            @PathVariable Long postId,
            @RequestBody RejectRequest rejectRequest,
            @RequestHeader("X-Role") String role
    ) {
        if (!"editor".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reviewService.rejectPost(postId, rejectRequest);
        return ResponseEntity.ok().build();
    }
}
