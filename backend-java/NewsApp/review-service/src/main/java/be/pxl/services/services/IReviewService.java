package be.pxl.services.services;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.RejectRequest;

import java.util.List;

public interface IReviewService {
    List<PostResponse> getPendingPosts(String role);
    void rejectPost(Long postId, String reviewer, RejectRequest rejectRequest, String role);
    void approvePost(Long postId, String reviewer, String role);
}
