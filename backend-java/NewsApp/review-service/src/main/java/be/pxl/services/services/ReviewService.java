package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostStatusRequest;
import be.pxl.services.domain.dto.RejectRequest;
import be.pxl.services.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService implements IReviewService{

    private final ReviewRepository reviewRepository;
    private final PostClient postClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<PostResponse> getPendingPosts(String role) {
        List<PostResponse> pendingPosts = postClient.getPendingPosts(role);
        log.info("Fetching {} pending posts", pendingPosts.size());
        return pendingPosts;
    }

    @Transactional
    public void approvePost(Long postId, String reviewer, String role) {
        log.debug("Approving post: {}", postId);

        Review review = Review.builder()
                .postId(postId)
                .reviewer(reviewer)
                .approved(true)
                .reviewDate(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        postClient.updatePostStatus(postId, new PostStatusRequest(PostStatus.PUBLISHED), role);

        String message = String.format("Post %d approved by %s", postId, reviewer);

        rabbitTemplate.convertAndSend("post-status-queue", message);

        log.info("Post {} has been APPROVED by {}. Review saved with id: {}",
                postId, reviewer, review.getId());
    }

    @Transactional
    public void rejectPost(Long postId, String reviewer, RejectRequest rejectRequest, String role) {
        log.debug("Rejecting post: {}", postId);

        Review review = Review.builder()
                .postId(postId)
                .reviewer(reviewer)
                .approved(false)
                .comment(rejectRequest.reason())
                .reviewDate(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        postClient.updatePostStatus(postId, new PostStatusRequest(PostStatus.REJECTED), role);

        String message = String.format("Post %d rejected by %s", postId, reviewer);

        rabbitTemplate.convertAndSend("post-status-queue", message);

        log.info("Post {} has been REJECTED by {}. Reason: '{}'. Review saved with id: {}",
                postId, reviewer, rejectRequest.reason(), review.getId());
    }
}
