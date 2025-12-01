package be.pxl.services.services;

import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CreateCommentRequest;

import java.util.List;

public interface ICommentService {
    void createComment(CreateCommentRequest commentRequest);
    List<CommentResponse> getAllComments();
    void updateComment(Long commentId);
    void deleteComment(Long commentId);
}
