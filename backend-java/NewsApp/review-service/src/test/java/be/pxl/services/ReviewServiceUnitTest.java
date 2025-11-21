package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostStatusRequest;
import be.pxl.services.domain.dto.RejectRequest;
import be.pxl.services.repository.ReviewRepository;
import be.pxl.services.services.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceUnitTest {

    @Mock
    private ReviewRepository repository;

    @Mock
    private PostClient postClient;

    @InjectMocks
    private ReviewService reviewService;

    @Captor
    ArgumentCaptor<Review> reviewCaptor;

    @Test
    public void getPendingPosts_ShouldReturnListFromClient() {
        PostResponse mockPost = new PostResponse(
                1L, "Test Title", "Content", "Bob", LocalDateTime.now(), PostStatus.PENDING
        );

        String role = "editor";

        when(postClient.getPendingPosts(role)).thenReturn(List.of(mockPost));

        List<PostResponse> result = reviewService.getPendingPosts(role);

        assertEquals(1, result.size());
        assertEquals("Test Title", result.getFirst().title());

        verify(postClient).getPendingPosts(role);
    }

    @Test void approvePost_ShouldSaveReview_And_UpdateStatusPublished() {
        Long postId = 123L;
        reviewService.approvePost(postId);

        verify(postClient).updatePostStatus(
                postId,
                new PostStatusRequest(PostStatus.PUBLISHED)
        );

        verify(repository).save(reviewCaptor.capture());

        Review savedReview = reviewCaptor.getValue();

        assertEquals(postId, savedReview.getPostId());
        assertTrue(savedReview.isApproved());
    }

    @Test
    public void rejectPost_ShouldSaveReview_And_UpdateStatusRejected() {
        Long postId = 123L;
        reviewService.rejectPost(postId, new RejectRequest("Bad title"));

        verify(postClient).updatePostStatus(
                postId,
                new PostStatusRequest(PostStatus.REJECTED)
        );

        verify(repository).save(reviewCaptor.capture());

        Review savedReview = reviewCaptor.getValue();

        assertEquals(postId, savedReview.getPostId());
        assertFalse(savedReview.isApproved());
    }
}