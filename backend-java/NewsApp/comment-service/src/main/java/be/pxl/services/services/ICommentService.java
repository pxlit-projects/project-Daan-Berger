package be.pxl.services.services;

import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CommentUpdateDto;
import be.pxl.services.domain.dto.CreateCommentRequest;

import java.util.List;

public interface ICommentService {
    void createComment(CreateCommentRequest commentRequest, Long postId, String author);

    List<CommentResponse> getAllComments();
    void updateComment(Long commentId, CommentUpdateDto updateDto);
    void deleteComment(Long commentId);
}
