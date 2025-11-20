package be.pxl.services.services;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.RejectRequest;

import java.util.List;

public interface IReviewService {
    List<PostResponse> getPendingPosts(String role);
    void rejectPost(Long postId, RejectRequest rejectRequest);
    void approvePost(Long postId);
}
