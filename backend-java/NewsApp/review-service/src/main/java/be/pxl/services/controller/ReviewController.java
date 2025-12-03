package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.RejectRequest;

import be.pxl.services.services.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/pending")
    public ResponseEntity<List<PostResponse>> getPendingPosts(@RequestHeader("X-Role") String role) {
        if (!"editor".equalsIgnoreCase(role)) {
            log.warn("Unauthorized access attempt to pending posts endpoint. Role provided: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reviewService.getPendingPosts(role));
    }

    @PostMapping("/{postId}/approve")
    public ResponseEntity<Void> approvePost(
            @PathVariable Long postId,
            @RequestHeader("X-Role") String role,
            @RequestHeader("X-User") String reviewer
    ) {
        if (!"editor".equalsIgnoreCase(role)) {
            log.warn("Unauthorized access attempt to approve post endpoint. Role provided: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reviewService.approvePost(postId, reviewer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/reject")
    public ResponseEntity<Void> rejectPost(
            @PathVariable Long postId,
            @RequestBody RejectRequest rejectRequest,
            @RequestHeader("X-Role") String role,
            @RequestHeader("X-User") String reviewer
    ) {
        if (!"editor".equalsIgnoreCase(role)) {
            log.warn("Unauthorized access attempt to reject post endpoint. Role provided: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reviewService.rejectPost(postId, reviewer, rejectRequest);
        return ResponseEntity.ok().build();
    }
}
