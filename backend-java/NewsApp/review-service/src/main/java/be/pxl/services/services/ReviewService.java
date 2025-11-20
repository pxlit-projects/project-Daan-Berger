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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService{

    private final ReviewRepository reviewRepository;
    private final PostClient postClient;

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    public List<PostResponse> getPendingPosts(String role) {
        return postClient.getPendingPosts(role);
    }

    @Transactional
    public void approvePost(Long postId) {
        Review review = Review.builder()
                .postId(postId)
                .approved(true)
                .reviewDate(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        postClient.updatePostStatus(postId, new PostStatusRequest(PostStatus.PUBLISHED));

        logger.info("Post {}", postId);
    }

    @Transactional
    public void rejectPost(Long postId, RejectRequest rejectRequest) {
        Review review = Review.builder()
                .postId(postId)
                .approved(false)
                .comment(rejectRequest.reason())
                .reviewDate(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        postClient.updatePostStatus(postId, new PostStatusRequest(PostStatus.REJECTED));

        logger.warn("Post {} rejected, Reason: {}", postId, rejectRequest.reason());
    }
}
