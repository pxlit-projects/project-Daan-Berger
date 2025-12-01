package be.pxl.services.services;

import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CreateCommentRequest;

import java.util.List;

public interface ICommentService {
    void createComment(CreateCommentRequest commentRequest, long postId);

    List<CommentResponse> getAllComments();
    void updateComment(long commentId);
    void deleteComment(long commentId);
}
